package ru.prokhorov.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ru.prokhorov.model.DatabaseConnection;

import java.io.IOException;
import java.sql.SQLException;

public class Auth {
    public TextField login;
    public TextField password;

    public void send(ActionEvent actionEvent) throws SQLException, IOException {
        String login = this.login.getText();
        String password = this.password.getText();

        if(!login.isEmpty() && !password.isEmpty()){
            DatabaseConnection databaseConnection = new DatabaseConnection();
            databaseConnection.sendingRequest(login, password);
            if(databaseConnection.isEnter()){
                Stage primary = (Stage) this.login.getScene().getWindow();
                primary.setScene(loadMainWindow());
                primary.show();
            }
        }
    }

    private Scene loadMainWindow() throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("cloud_client.fxml"));
        return new Scene(parent);
    }

    private Scene loadRegistrationWindow() throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("registration.fxml"));
        return new Scene(parent);
    }

    public void registration(ActionEvent actionEvent) throws IOException {
        Stage primary = (Stage) this.login.getScene().getWindow();
        primary.setScene(loadRegistrationWindow());
        primary.show();
    }
}
