package ru.prokhorov.model;

public class DirUp implements AbstractMessage{
    @Override
    public MessageType getMessageType() {
        return MessageType.DIR_UP;
    }
}
