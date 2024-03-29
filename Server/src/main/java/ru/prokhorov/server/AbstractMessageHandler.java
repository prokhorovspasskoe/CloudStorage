package ru.prokhorov.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import ru.prokhorov.model.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Stack;

import static ru.prokhorov.model.MessageType.*;

@Slf4j
public class AbstractMessageHandler extends SimpleChannelInboundHandler<AbstractMessage> {

    private final Path currentPath;
    private Path userPath;
    private final Stack<String> stackDir;

    public AbstractMessageHandler() {
        currentPath = Paths.get("serverFiles");
        stackDir = new Stack<String>();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(new FilesList(currentPath));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx,
                                AbstractMessage message) throws Exception {
        log.debug("received: {}", message);

        DatabaseConnection databaseConnection;

        switch (message.getMessageType()) {
            case FILE_REQUEST:
                FileRequest req = (FileRequest) message;
                ctx.writeAndFlush(
                        new FileMessage(userPath.resolve(req.getFileName()))
                );
                break;
            case CHANGE_DIR:
                ChangeDir changeDir = (ChangeDir) message;
                String nameDir = changeDir.getChangeDir();
                stackDir.push(userPath + "/" + nameDir);
                File changeFilesDir = new File(userPath + "/" + nameDir);
                userPath = Paths.get(userPath + "/" + nameDir);
                List<String> changeListDir = Arrays.asList(Objects.requireNonNull(changeFilesDir.list()));
                ctx.writeAndFlush(new FilesList(changeListDir));
                break;
            case DIR_UP:
                int ss = stackDir.size();
                if(ss <= 1){
                    updateDir(ctx);
                }else {
                    stackDir.pop();
                    userPath = Paths.get(stackDir.pop());
                }
                updateDir(ctx);
                break;
            case HOME:

                updateDir(ctx);
                break;
            case COPY_FILES:
                CopyFiles copyFiles = (CopyFiles) message;
                String fileNameDir = copyFiles.getCopyFile();
                Path path = Paths.get(fileNameDir);
                int gnc = path.getNameCount();
                String newDir = path.getName(gnc - 2).toString();
                Path fileName = path.getFileName();
                path = Paths.get(newDir);
                Files.write(Paths.get(userPath + "/" + path + "/" + fileName), copyFiles.getFile());
                updateDir(ctx);
                break;
            case FILE_RENAME:
                FileRename fileRename = (FileRename) message;
                File oldFile = new File(userPath + "/" + fileRename.getOldFile());
                File newFile = new File(userPath + "/" + fileRename.getNewFile());
                boolean isRename = oldFile.renameTo(newFile);
                if(isRename){
                    log.debug("Rename file " + oldFile + " to " + newFile);
                    updateDir(ctx);
                }
                break;
            case COPY_DIR:
                CopyDirectory copyDirectory = (CopyDirectory) message;
                String createNewDir = copyDirectory.getNewDir();
                Files.createDirectory(Paths.get(userPath + "/" + createNewDir));
                log.debug("New dir - " + createNewDir);
                updateDir(ctx);
                break;
            case DELETE:
                FileDelete fileDelete = (FileDelete) message;
                File deleteFile = new File(userPath + "/" + fileDelete.getDeleteFileName());
                if(deleteFile.delete()) {
                    updateDir(ctx);
                }
                break;
            case FILE:
                FileMessage fileMessage = (FileMessage) message;
                Files.write(
                        userPath.resolve(fileMessage.getFileName()),
                        fileMessage.getBytes()
                );
                ctx.writeAndFlush(new FilesList(userPath));
                break;
            case AUTH:
                DatabaseQueryAuth databaseQueryAuth = (DatabaseQueryAuth) message;
                String login = databaseQueryAuth.getLogin();
                String password  = databaseQueryAuth.getPassword();
                if(!login.isEmpty() && !password.isEmpty()){
                    databaseConnection = new DatabaseConnection();
                    databaseConnection.sendingRequest(login, password);
                    databaseQueryAuth.setAuth(databaseConnection.isEnter());
                    userPath = Paths.get(currentPath + "/" + login);
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
                    Files.createDirectory(Paths.get(currentPath + "/" + loginReg));
                    userPath = Paths.get(currentPath + "/" + loginReg);
                    ctx.writeAndFlush(databaseQueryRegistration);
                }
                break;
        }
    }
    public void updateDir(ChannelHandlerContext ctx){
        File getFilesDir = new File(String.valueOf(userPath));
        List<String> updateDir = Arrays.asList(Objects.requireNonNull(getFilesDir.list()));
        ctx.writeAndFlush(new FilesList(updateDir));
    }
}
