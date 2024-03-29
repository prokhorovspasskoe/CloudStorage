package ru.prokhorov.client;

import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import ru.prokhorov.model.*;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class Controller implements Initializable {
    public ListView<String> clientFiles;
    public ListView<String> serverFiles;
    public TextField clientFolder;
    private Path baseDir;
    private Path serverDir;
    private ObjectDecoderInputStream is;
    private ObjectEncoderOutputStream os;

    private void read() {
        try {
            while (true) {
                AbstractMessage msg = (AbstractMessage) is.readObject();
                switch (msg.getMessageType()) {
                    case FILE:
                        FileMessage fileMessage = (FileMessage) msg;
                        Files.write(
                                baseDir.resolve(fileMessage.getFileName()),
                                fileMessage.getBytes()
                        );
                        Platform.runLater(() -> fillClientView(getFileNames()));
                        break;
                    case FILES_LIST:
                        FilesList files = (FilesList) msg;
                        Platform.runLater(() -> fillServerView(files.getFiles()));
                        break;
                    case CHANGE_DIR:

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fillServerView(List<String> list) {
        serverFiles.getItems().clear();
        serverFiles.getItems().addAll(list);
    }

    private void fillClientView(List<String> list) {
        clientFiles.getItems().clear();
        clientFiles.getItems().addAll(list);
    }

    private List<String> getFileNames() {
        try {
            return Files.list(baseDir)
                    .map(p -> p.getFileName().toString())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            baseDir = Paths.get(System.getProperty("user.home"));
            clientFiles.getItems().addAll(getFileNames());
            clientFiles.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2) {
                    String file = clientFiles.getSelectionModel().getSelectedItem();
                    Path path = baseDir.resolve(file);
                    if (Files.isDirectory(path)) {
                        baseDir = path;
                        fillClientView(getFileNames());
                    }
                }
            });
            serverFiles.setOnMouseClicked(e -> {
               if(e.getClickCount() == 2){
                   String dirName = serverFiles.getSelectionModel().getSelectedItem();
                   try {
                       ChangeDir changeDir = new ChangeDir();
                       changeDir.setChangeDir(dirName);
                       os.writeObject(changeDir);
                   } catch (IOException ex) {
                       ex.printStackTrace();
                   }
               }
            });
            Socket socket = new Socket("localhost", 8189);
            os = new ObjectEncoderOutputStream(socket.getOutputStream());
            is = new ObjectDecoderInputStream(socket.getInputStream());
            Thread thread = new Thread(this::read);
            thread.setDaemon(true);
            thread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void upload(ActionEvent actionEvent) throws IOException {
        String file = clientFiles.getSelectionModel().getSelectedItem();
        Path filePath = baseDir.resolve(file);
        boolean isDir = Files.isDirectory(filePath);
        if(isDir){
            String newPath = String.valueOf(filePath);
            os.writeObject(new CopyDirectory(newPath));
            File dirExport = new File(newPath);
            File[] arrDirExport = dirExport.listFiles();
            assert arrDirExport != null;
            for (File fileExport : arrDirExport){
                String exportFile = String.valueOf(fileExport);
                CopyFiles copyFiles = new CopyFiles();
                copyFiles.setCopyFile(exportFile);
                os.writeObject(copyFiles);
            }
        }else {
            os.writeObject(new FileMessage(filePath));
        }
    }

    public void downLoad(ActionEvent actionEvent) throws IOException {
        String file = serverFiles.getSelectionModel().getSelectedItem();
        os.writeObject(new FileRequest(file));
    }

    public void delete(ActionEvent actionEvent) throws IOException {
        String file = serverFiles.getSelectionModel().getSelectedItem();
        os.writeObject(new FileDelete(file));
    }

    public void rename(ActionEvent actionEvent) throws IOException {
        String file = serverFiles.getSelectionModel().getSelectedItem();
        TextInputDialog textInput = new TextInputDialog();
        textInput.setTitle("Rename file");
        Optional<String> result = textInput.showAndWait();

        if(result.isPresent()){
            os.writeObject(new FileRename(file, result.get()));
        }
    }

    public void openUserDir(ActionEvent actionEvent) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        Stage primary = (Stage) this.clientFolder.getScene().getWindow();
        File file = directoryChooser.showDialog(primary);
        if(file != null){
            baseDir = Paths.get(file.getAbsolutePath());
            clientFolder.setText(file.toString());
            clientFiles.getItems().clear();
            clientFiles.getItems().addAll(getFileNames());
            clientFiles.refresh();
        }
    }

    public void dirUp(ActionEvent actionEvent) throws IOException {
        os.writeObject(new DirUp());
    }

    public void home(ActionEvent actionEvent) throws IOException {
        os.writeObject(new Home());
    }
}
