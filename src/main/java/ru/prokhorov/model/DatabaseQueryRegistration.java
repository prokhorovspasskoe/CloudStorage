package ru.prokhorov.model;

public class DatabaseQueryRegistration implements AbstractMessage{

    private final String login;
    private final String password;
    private final String email;
    private boolean isRegistration;

    public DatabaseQueryRegistration(String login, String password, String email) {
        this.login = login;
        this.password = password;
        this.email = email;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public boolean isRegistration() {
        return isRegistration;
    }

    public void setRegistration(boolean registration) {
        isRegistration = registration;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.REGISTRATION;
    }
}
