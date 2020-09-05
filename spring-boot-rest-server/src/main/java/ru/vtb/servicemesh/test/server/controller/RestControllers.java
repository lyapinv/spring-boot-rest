package ru.vtb.servicemesh.test.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import ru.vtb.servicemesh.test.server.service.ITestService;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RestControllers {

//    @Autowired
//    private ITestService serverService;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${NEXT_SERVICE_URI}")
    private String nextServiceURL;

    @RequestMapping("/ping")
    public String ping() {
        log.info("!!! ping request");
        return "pong from server";
    }

//    @RequestMapping("/ping")
//    public Mono<String> ping() {
//        return Mono.fromCallable(() -> "pong from server")
//                .subscribeOn(Schedulers.elastic())
//                .doOnNext(s -> log.info("!!! WebFlux ping request"));
//    }

    @RequestMapping("/ping_chain")
    public String pingChain() {
        log.info(" Call ping server. nextServiceURL: {}", nextServiceURL);
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(nextServiceURL + "/ping", String.class);
        String response = responseEntity.getBody();
        log.info("!!! response from server: {}", response);
        return response;
    }
}