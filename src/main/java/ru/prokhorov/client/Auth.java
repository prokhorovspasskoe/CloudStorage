package ru.prokhorov.client;

import javafx.event.ActionEvent;
import javafx.scene.control.TextField;
import ru.prokhorov.model.DatabaseConnection;
import java.sql.SQLException;

public class Auth {
    public TextField login;
    public TextField password;

    public void send(ActionEvent actionEvent) throws SQLException {
        String login = this.login.getText();
        String password = this.password.getText();

        if(!login.isEmpty() && !password.isEmpty()){
            DatabaseConnection databaseConnection = new DatabaseConnection();
            databaseConnection.sendingRequest(login, password);
        }
    }
}
