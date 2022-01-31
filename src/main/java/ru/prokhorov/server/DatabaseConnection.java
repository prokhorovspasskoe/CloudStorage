package ru.prokhorov.server;

import lombok.extern.slf4j.Slf4j;

import java.sql.*;
@Slf4j
public class DatabaseConnection {

    private final Connection connection;
    private boolean isEnter = false;
    private boolean isReg = false;

    public boolean isEnter() {
        return isEnter;
    }

    public boolean isReg() {
        return isReg;
    }

    public DatabaseConnection() throws SQLException {
        String URL = "jdbc:mysql://localhost:3306/clouds_base";
        connection = DriverManager.getConnection(URL, "root", "root");
        if(connection == null){
            log.debug("No connect to database...");
        }else {
            Statement statement = connection.createStatement();
        }
    }

    public void sendingRequest(String userLog, String userPass) throws SQLException {
        int id = 0;
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT id FROM users WHERE login = "
                    + userLog + " AND " + "password = " + userPass + ";");
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

    public void sendingRegistration(String login, String password, String email) throws SQLException {
        int id = 0;
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT id FROM users WHERE login = "
                    + login + " AND " + "email = " + email + ";");
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                id = resultSet.getInt("id");
            }
            if (id == 0) {
                isReg = true;
                PreparedStatement rps = connection.prepareStatement("INSERT INTO users (login, email, password) " +
                        "VALUES (" + login + ", " + email + ", " + password + ");");
                ResultSet resultReg = rps.executeQuery();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            connection.close();
        }
    }
}
