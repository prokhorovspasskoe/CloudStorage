package ru.prokhorov.server;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import ru.prokhorov.model.*;

@Slf4j
public class AbstractMessageHandler extends SimpleChannelInboundHandler<AbstractMessage> {

    public Path currentPath;
    private DatabaseConnection databaseConnection;

    public AbstractMessageHandler() {
        currentPath = Paths.get("serverFiles");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(new FilesList(currentPath));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx,
                                AbstractMessage message) throws Exception {
        log.debug("received: {}", message);

        switch (message.getMessageType()) {
            case FILE_REQUEST:
                FileRequest req = (FileRequest) message;
                ctx.writeAndFlush(
                        new FileMessage(currentPath.resolve(req.getFileName()))
                );
                break;
            case CHANGE_DIR:
                ChangeDir changeDir = (ChangeDir) message;
                String nameDir = changeDir.getChangeDir();
                File changeFilesDir = new File(currentPath + "/" + nameDir);
                currentPath = Paths.get(currentPath + "/" + nameDir);
                List<String> changeListDir = Arrays.asList(Objects.requireNonNull(changeFilesDir.list()));
                ctx.writeAndFlush(new FilesList(changeListDir));
                break;
            case COPY_FILES:
                CopyFiles copyFiles = (CopyFiles) message;
                String fileNameDir = copyFiles.getCopyFile();
                Path path = Paths.get(fileNameDir);
                int gnc = path.getNameCount();
                String newDir = path.getName(gnc - 2).toString();
                Path fileName = path.getFileName();
                path = Paths.get(newDir);
                Files.write(Paths.get(currentPath + "/" + path + "/" + fileName), copyFiles.getFile());
                updateDir(ctx);
                break;
            case FILE_RENAME:
                FileRename fileRename = (FileRename) message;
                File oldFile = new File(currentPath + "/" + fileRename.getOldFile());
                File newFile = new File(currentPath + "/" + fileRename.getNewFile());
                boolean isRename = oldFile.renameTo(newFile);
                if(isRename){
                    log.debug("Rename file " + oldFile + " to " + newFile);
                    updateDir(ctx);
                }
                break;
            case COPY_DIR:
                CopyDirectory copyDirectory = (CopyDirectory) message;
                String createNewDir = copyDirectory.getNewDir();
                Files.createDirectory(Paths.get(currentPath + "/" + createNewDir));
                log.debug("New dir - " + createNewDir);
                updateDir(ctx);
                break;
            case DELETE:
                FileDelete fileDelete = (FileDelete) message;
                File deleteFile = new File(currentPath + "/" + fileDelete.getDeleteFileName());
                if(deleteFile.delete()) {
                    updateDir(ctx);
                }
                break;
            case FILE:
                FileMessage fileMessage = (FileMessage) message;
                Files.write(
                        currentPath.resolve(fileMessage.getFileName()),
                        fileMessage.getBytes()
                );
                ctx.writeAndFlush(new FilesList(currentPath));
                break;
            case AUTH:
                DatabaseQueryAuth databaseQueryAuth = (DatabaseQueryAuth) message;
                String login = databaseQueryAuth.getLogin();
                String password  = databaseQueryAuth.getPassword();
                if(!login.isEmpty() && !password.isEmpty()){
                    databaseConnection = new DatabaseConnection();
                    databaseConnection.sendingRequest(login, password);
                    databaseQueryAuth.setAuth(databaseConnection.isEnter());
                    ctx.writeAndFlush(databaseQueryAuth);
                }
                break;
            case REGISTRATION:
                DatabaseQueryRegistration databaseQueryRegistration = (DatabaseQueryRegistration) message;
                String loginReg = databaseQueryRegistration.getLogin();
                String passReg = databaseQueryRegistration.getPassword();
                String email = databaseQueryRegistration.getEmail();
                if(!loginReg.isEmpty() && !passReg.isEmpty() && !email.isEmpty()){
                    databaseConnection = new DatabaseConnection();
                    databaseConnection.sendingRegistration(loginReg, passReg, email);
                    databaseQueryRegistration.setRegistration(databaseConnection.isReg());
                    ctx.writeAndFlush(databaseQueryRegistration);
                }
                break;
        }
    }
    public void updateDir(ChannelHandlerContext ctx){
        File getFilesDir = new File(String.valueOf(currentPath));
        List<String> updateDir = Arrays.asList(Objects.requireNonNull(getFilesDir.list()));
        ctx.writeAndFlush(new FilesList(updateDir));
    }
}
