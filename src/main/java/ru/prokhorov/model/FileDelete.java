package ru.prokhorov.model;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Data
@Slf4j
public class FileDelete implements  AbstractMessage {

    private String deletePach;

    public FileDelete(String fileName) throws IOException {
        deletePach = "serverFiles";
        String deleteP = deletePach + "/" + fileName;
        Files.delete(Paths.get(deleteP));
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.DELETE;
    }
}
