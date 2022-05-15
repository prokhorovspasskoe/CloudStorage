package ru.prokhorov.model;

public class DatabaseQueryAuth implements AbstractMessage{
    private final String login;
    private final String password;
    private boolean isAuth = false;

    public DatabaseQueryAuth(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public boolean isAuth() {
        return isAuth;
    }

    public void setAuth(boolean auth) {
        isAuth = auth;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.AUTH;
    }
}
