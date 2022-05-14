package ru.prokhorov.model;
import lombok.Data;

@Data
public class FileRename implements AbstractMessage{
    private String oldFile;
    private String newFile;

    public FileRename(String oldFile, String newFile) {
        this.oldFile = oldFile;
        this.newFile = newFile;
    }

    public String getOldFile() {
        return oldFile;
    }

    public String getNewFile() {
        return newFile;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.FILE_RENAME;
    }
}
