package ru.vtb.servicemesh.test.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import ru.vtb.servicemesh.test.server.service.ITestService;

import static ru.vtb.servicemesh.test.server.reactive.ReactorAsyncHandler.into;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RestControllers {

    @Autowired
    private ITestService serverService;

    @Autowired
    private ru.vtb.test.jaxws.async.client.HelloWorld helloWorldClient;

    private final WebClient webClient;

    @RequestMapping("/pingrx")
    public Mono<String> ping() {
        return Mono.fromCallable(() -> serverService.ping())
                .subscribeOn(Schedulers.elastic())
                .doOnNext(s -> log.info("!!! WebFlux ping request"));
    }

    @RequestMapping("/pingrx_ws")
    public Mono<String> pingRxWs() {
        return Mono.create(sink -> helloWorldClient.getHelloWorldAsStringAsync("Reactor SOAP ping: ", into(sink)));
    }

    @RequestMapping("/pingrx_mock")
    public Mono<String> pingMock() {
        log.info("!!! WebFlux ping_mock request");
        return webClient.get().uri("/ping").retrieve().bodyToMono(String.class);
    }
}
