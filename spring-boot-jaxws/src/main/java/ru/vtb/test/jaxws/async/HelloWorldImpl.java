package ru.vtb.test.jaxws.async;

import lombok.extern.slf4j.Slf4j;

import javax.jws.WebService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

//Service Implementation
@Slf4j
@WebService(endpointInterface = "ru.vtb.test.jaxws.async.HelloWorld")
public class HelloWorldImpl implements HelloWorld {

    private final AtomicInteger counter = new AtomicInteger();

    @Override
    public String getHelloWorldAsString(String name) throws InterruptedException {
        int executionCount = counter.incrementAndGet();
        log.info("Current executionCount: {}", executionCount);
        TimeUnit.SECONDS.sleep(10);
        return "Hello World JAX-WS " + name;
    }

}
