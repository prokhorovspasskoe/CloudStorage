package ru.prokhorov.model;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import ru.prokhorov.server.AbstractMessageHandler;

import java.io.File;

@Data
@Slf4j
public class FileRename implements AbstractMessage{
    private String serverPath;

    public FileRename(String fileName, String newName) {
        ServerDir serverDir = new ServerDir();
        String renameFile = "serverFiles" + "/" + fileName;
        File oldFile = new File(renameFile);
        File newFile = new File("serverFiles" + "/" + newName);
        if(oldFile.renameTo(newFile)){
            log.debug("Rename file " + oldFile + " to " + newFile);
        }else{
            log.debug("No rename file " + oldFile);
        }
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.FILE_RENAME;
    }
}
