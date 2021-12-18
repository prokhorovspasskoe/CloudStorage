package ru.prokhorov.server;

import io.netty.channel.ChannelHandlerContext;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AbstractMessageHandler  extends SimpleChannelInboundHandler<AbstractMessage>{
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
