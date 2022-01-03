package ru.prokhorov.model;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Data
@Slf4j
public class FileDelete implements  AbstractMessage {


    private String deletePach;

    public FileDelete(String fileName) throws IOException {
        deletePach = "serverFiles";
        String deleteP = deletePach + "/" + fileName;
        boolean existsFile = Files.exists(Paths.get(deleteP));
        if(existsFile) {
            Files.delete(Paths.get(deleteP));
        }else{
            log.debug("File exists!");
        }
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.DELETE;
    }
}
