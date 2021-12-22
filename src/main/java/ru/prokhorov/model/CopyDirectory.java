package ru.prokhorov.model;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@Slf4j
public class CopyDirectory implements AbstractMessage{
    private int gnc;

    public CopyDirectory(String sourceDir) throws IOException {
        Path path = Paths.get(sourceDir);
        gnc = path.getNameCount();
        String newDir = path.getName(gnc - 1).toString();
        log.debug("New dir - " + newDir);
        Files.createDirectory(Paths.get("serverFiles" + "\\" + newDir));
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.COPY_DIR;
    }
}
