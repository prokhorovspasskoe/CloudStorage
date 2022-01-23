package ru.prokhorov.client;

import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ru.prokhorov.model.AbstractMessage;
import ru.prokhorov.model.DatabaseQueryAuth;
import ru.prokhorov.model.MessageType;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Objects;

public class Auth {
    public TextField login;
    public TextField password;
    private ObjectDecoderInputStream is;
    private ObjectEncoderOutputStream os;
    private Socket socket;
    boolean isAuth;

    public void initialize() throws IOException {
        socket = new Socket("localhost", 8189);
        os = new ObjectEncoderOutputStream(socket.getOutputStream());
        is = new ObjectDecoderInputStream(socket.getInputStream());
    }

    private void read() throws IOException {
        try {
            isAuth = true;
            while (isAuth) {
                AbstractMessage msg = (AbstractMessage) is.readObject();
                if (msg.getMessageType() == MessageType.AUTH) {
                    DatabaseQueryAuth databaseQueryAuth = (DatabaseQueryAuth) msg;
                    if (databaseQueryAuth.isAuth()) {
                        isAuth = false;
                        databaseQueryAuth.setAuth(false);
                        Stage primary = (Stage) this.login.getScene().getWindow();
                        primary.setScene(loadMainWindow());
                        primary.show();
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

        public void send(ActionEvent actionEvent) throws SQLException, IOException {
        String login = this.login.getText();
        String password = this.password.getText();

        if(!login.isEmpty() && !password.isEmpty()){
            DatabaseQueryAuth databaseQueryAuth = new DatabaseQueryAuth(login, password);
            initialize();
            os.writeObject(databaseQueryAuth);
            read();
        }
    }

    private Scene loadMainWindow() throws IOException {
        Parent parent = FXMLLoader.load(Objects.requireNonNull(getClass()
                .getResource("ru.prokhorov.client/cloud_client.fxml")));
        return new Scene(parent);
    }

    private Scene loadRegistrationWindow() throws IOException {
        Parent parent = FXMLLoader.load(Objects.requireNonNull(getClass()
                .getResource("ru.prokhorov.client/registration.fxml")));
        return new Scene(parent);
    }

    public void registration(ActionEvent actionEvent) throws IOException {
        Stage primary = (Stage) this.login.getScene().getWindow();
        primary.setScene(loadRegistrationWindow());
        primary.show();
    }
}
