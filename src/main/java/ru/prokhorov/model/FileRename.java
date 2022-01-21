package ru.prokhorov.model;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import java.io.File;

@Data
@Slf4j
public class FileRename implements AbstractMessage{
    private String serverPath;

    public FileRename(String fileName, String newName) {
        serverPath = "serverFiles";
        String renameFile = serverPath + "\\" + fileName;
        File oldFile = new File(renameFile);
        File newFile = new File(newName);
        if(oldFile.renameTo(newFile)){
            log.debug("Rename file " + oldFile + "to " + newFile);
        }else{
            log.debug("No rename file " + oldFile);
        }
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.FILE_RENAME;
    }
}
