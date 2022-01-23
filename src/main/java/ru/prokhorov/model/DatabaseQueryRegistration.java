package ru.prokhorov.model;

public class DatabaseQueryRegistration implements AbstractMessage{
    @Override
    public MessageType getMessageType() {
        return MessageType.REGISTRATION;
    }
}
