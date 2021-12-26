package ru.prokhorov.server;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import ru.prokhorov.model.*;

@Slf4j
public class AbstractMessageHandler extends SimpleChannelInboundHandler<AbstractMessage> {

    private Path currentPath;

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
                File changeFilesDir = new File(currentPath + "\\" + nameDir);
                List<String> changeListDir = Arrays.asList(Objects.requireNonNull(changeFilesDir.list()));
                ctx.writeAndFlush(new FilesList(changeListDir));
                break;
            case COPY_DIR:
            case DELETE:
                File getFilesDir = new File(String.valueOf(currentPath));
                List<String> updateDir = Arrays.asList(Objects.requireNonNull(getFilesDir.list()));
                ctx.writeAndFlush(new FilesList(updateDir));
                break;
            case FILE:
                FileMessage fileMessage = (FileMessage) message;
                Files.write(
                        currentPath.resolve(fileMessage.getFileName()),
                        fileMessage.getBytes()
                );
                ctx.writeAndFlush(new FilesList(currentPath));
                break;
        }
    }
}
