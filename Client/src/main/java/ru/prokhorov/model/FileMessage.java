package ru.prokhorov.model;

import lombok.Data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Data
public class FileMessage implements AbstractMessage{
    private String fileName;
    private byte[] bytes;
    private int gnc;

    public FileMessage(Path path) throws IOException {
        fileName = path.getFileName().toString();
        bytes = Files.readAllBytes(path);
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.FILE;
    }
}
