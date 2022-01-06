package ru.prokhorov.model;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class DatabaseConnection {

    private final Connection connection;
    private boolean isEnter = false;

    public boolean isEnter() {
        return isEnter;
    }

    public DatabaseConnection() throws SQLException {
        String URL = "jdbc:mysql://localhost:3306/clouds_base";
        connection = DriverManager.getConnection(URL, "root", "root");
        Statement statement = connection.createStatement();
    }

    public void sendingRequest(String userLog, String userPass) throws SQLException {
        int id = 0;
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT id FROM users WHERE login = "
                    + userLog + " AND " + "password = " + userPass);
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()){
                id = resultSet.getInt("id");
            }
            if(id != 0){
                isEnter = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            connection.close();
        }

    }
}
