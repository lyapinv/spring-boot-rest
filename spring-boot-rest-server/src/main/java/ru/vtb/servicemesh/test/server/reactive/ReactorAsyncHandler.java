package ru.vtb.servicemesh.test.server.reactive;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.MonoSink;

import javax.xml.ws.AsyncHandler;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
public class ReactorAsyncHandler {
    public static <T> AsyncHandler<T> into(MonoSink<T> sink) {
        return res -> {
            try {
                log.info("!!! Success response received");
                sink.success(res.get(1, TimeUnit.MILLISECONDS));
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                log.info("### Fault response received");
                sink.error(e);
            }
        };
    }
}
