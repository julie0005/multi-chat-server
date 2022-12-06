package org.ajou.multichatserver.socket;

import java.lang.reflect.Type;
import java.util.concurrent.CompletableFuture;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

public class MessageFrameHandler<T> extends StompSessionHandlerAdapter {

    private final CompletableFuture<T> completableFuture = new CompletableFuture<>();

    private final Class<T> tClass;

    public MessageFrameHandler(Class<T> tClass) {
        this.tClass = tClass;
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return this.tClass;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        completableFuture.complete((T)payload);
    }

    public CompletableFuture<T> getCompletableFuture() {
        return completableFuture;
    }
}
