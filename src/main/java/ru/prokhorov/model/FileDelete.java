package ru.prokhorov.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileDelete implements  AbstractMessage{

    private Path deletePach;

    public FileDelete(Path path) throws IOException {
        deletePach = Paths.get("serverFiles");
        String deleteP = deletePach.toString() + "/" + path.toString();
        Files.delete(Paths.get(deleteP));
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.DELETE;
    }
}
