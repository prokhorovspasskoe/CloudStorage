package ru.prokhorov.client;

import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ru.prokhorov.model.AbstractMessage;
import ru.prokhorov.model.DatabaseQueryRegistration;
import ru.prokhorov.model.MessageType;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Objects;

public class Registration {
    public TextField login;
    public TextField email;
    public TextField password;
    public TextField pass_conf;
    private ObjectDecoderInputStream is;
    private ObjectEncoderOutputStream os;
    private Socket socket;
    boolean isRegistration;

    public void initialize() throws IOException {
        socket = new Socket("localhost", 8189);
        os = new ObjectEncoderOutputStream(socket.getOutputStream());
        is = new ObjectDecoderInputStream(socket.getInputStream());
    }

    public void send(ActionEvent actionEvent) throws SQLException, IOException {
        String login = this.login.getText();
        String password = this.password.getText();
        String pass_conf = this.pass_conf.getText();
        String email = this.email.getText();

        if(!login.isEmpty() && !email.isEmpty() && !password.isEmpty() && !pass_conf.isEmpty()){
            if(password.equals(pass_conf)){
                DatabaseQueryRegistration databaseQueryRegistration = new DatabaseQueryRegistration(login, password, email);
                initialize();
                os.writeObject(databaseQueryRegistration);
                read();
            }
        }
    }

    private void read() throws IOException {
        try {
            isRegistration = true;
            while (isRegistration) {
                AbstractMessage msg = (AbstractMessage) is.readObject();
                if (msg.getMessageType() == MessageType.REGISTRATION) {
                    DatabaseQueryRegistration databaseQueryRegistration = (DatabaseQueryRegistration) msg;
                    if (databaseQueryRegistration.isRegistration()) {
                        isRegistration = false;
                        databaseQueryRegistration.setRegistration(false);
                        Stage stage = (Stage) login.getScene().getWindow();
                        FXMLLoader loader = new FXMLLoader();
                        loader.setLocation(getClass().getResource("/cloud_client.fxml"));
                        Parent parent = loader.load();
                        Scene scene = new Scene(parent);
                        Platform.runLater(()-> stage.setScene(scene));
                        Platform.runLater(stage::show);
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }finally {
            socket.close();
            os.close();
            is.close();
        }
    }

    private Scene loadMainWindow() throws IOException {
        Parent parent = FXMLLoader.load(Objects.requireNonNull(getClass()
                .getResource("cloud_client.fxml")));
        return new Scene(parent);
    }
}
