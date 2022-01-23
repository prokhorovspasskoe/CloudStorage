package ru.prokhorov.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ru.prokhorov.server.DatabaseConnection;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;

public class Registration {
    public TextField login;
    public TextField email;
    public TextField password;
    public TextField pass_conf;

    public void send(ActionEvent actionEvent) throws SQLException, IOException {
        String login = this.login.getText();
        String password = this.password.getText();
        String pass_conf = this.pass_conf.getText();
        String email = this.email.getText();

        if(!login.isEmpty() && !email.isEmpty() && !password.isEmpty() && !pass_conf.isEmpty()){
            if(password.equals(pass_conf)){
                DatabaseConnection databaseConnection = new DatabaseConnection();
                databaseConnection.sendingRegistration(login, password, email);
                if(databaseConnection.isReg()){
                    Stage primary = (Stage) this.login.getScene().getWindow();
                    primary.setScene(loadMainWindow());
                    primary.show();
                }
            }
        }
    }

    private Scene loadMainWindow() throws IOException {
        Parent parent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("ru.prokhorov.client/cloud_client.fxml")));
        return new Scene(parent);
    }
}
