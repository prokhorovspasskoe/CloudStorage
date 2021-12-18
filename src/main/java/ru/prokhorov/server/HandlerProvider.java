package ru.prokhorov.server;

import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

public class HandlerProvider {
    private final UserNameService nameService;
    private final ContextStoreService contextStoreService;

    public HandlerProvider(UserNameService nameService,
                           ContextStoreService contextStoreService) {
        this.nameService = nameService;
        this.contextStoreService = contextStoreService;
    }
    public ChannelHandler getSerializePipeline() {
        return new ChannelHandler[] {
                new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                new ObjectEncoder(),
                new AbstractMessageHandler()
        };
    }
}
