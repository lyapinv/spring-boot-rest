package ru.vtb.servicemesh.test.external.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RestControllers {

    @RequestMapping("/ping")
    public Mono<String> ping() {
        return Mono.fromCallable(() -> "ext ping")
                .subscribeOn(Schedulers.elastic())
                .doOnNext(s -> log.info("!!! WebFlux ping request"));
    }
}
