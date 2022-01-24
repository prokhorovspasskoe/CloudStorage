package ru.prokhorov.model;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.*;

@Data
@Slf4j
public class CopyDirectory implements AbstractMessage{
    private int gnc;
    private String newDir;

    public CopyDirectory(String sourceDir) throws IOException {
        Path path = Paths.get(sourceDir);
        gnc = path.getNameCount();
        newDir = path.getName(gnc - 1).toString();
    }

    public String getNewDir() {
        return newDir;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.COPY_DIR;
    }
}
