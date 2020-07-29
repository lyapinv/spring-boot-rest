package ru.vtb.servicemesh.test.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import ru.vtb.servicemesh.test.server.service.ITestService;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RestControllers {

    @Autowired
    private ITestService serverService;

    @RequestMapping("/pingrx")
    public Mono<String> ping() {
        return Mono.fromCallable(() -> serverService.ping())
                .subscribeOn(Schedulers.elastic())
                .doOnNext(s -> log.info("!!! WebFlux ping request"));
    }
}
