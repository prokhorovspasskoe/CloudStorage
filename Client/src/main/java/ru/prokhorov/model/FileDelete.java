package ru.prokhorov.model;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class FileDelete implements  AbstractMessage {

    private String deleteFileName;

    public FileDelete(String deleteFileName) {
        this.deleteFileName = deleteFileName;
    }

    public String getDeleteFileName() {
        return deleteFileName;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.DELETE;
    }
}
