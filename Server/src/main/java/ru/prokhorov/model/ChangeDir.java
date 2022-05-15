package ru.prokhorov.model;

public class ChangeDir implements AbstractMessage{
    private String changeDir;

    public String getChangeDir() {
        return changeDir;
    }

    public void setChangeDir(String changeDir) {
        this.changeDir = changeDir;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.CHANGE_DIR;
    }
}
