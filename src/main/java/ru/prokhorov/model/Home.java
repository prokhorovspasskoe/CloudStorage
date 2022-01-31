package ru.prokhorov.model;

public class Home implements AbstractMessage{

    @Override
    public MessageType getMessageType() {
        return MessageType.HOME;
    }
}
