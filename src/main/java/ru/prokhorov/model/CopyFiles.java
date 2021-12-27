package ru.prokhorov.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CopyFiles implements AbstractMessage{

    private String copyFile;
    byte[] bytes;

    public String getCopyFile() {
        return copyFile;
    }

    public byte[] getFile(){
        return bytes;
    }

    public void setCopyFile(String copyFile) throws IOException {
        this.copyFile = copyFile;
        bytes = Files.readAllBytes(Paths.get(copyFile));
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.COPY_FILES;
    }
}
