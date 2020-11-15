package ru.vtb.servicemesh.test.client.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.vtb.servicemesh.test.client.model.City;
import ru.vtb.servicemesh.test.client.service.ITestService;

import java.util.List;

@RestController
@Slf4j
public class ClientController {

    @Autowired
    private ITestService clientService;

    @RequestMapping("/ping")
    public String ping() {
        return clientService.ping();
    }

    @PostMapping(value = "/bigBody", consumes = { MediaType.APPLICATION_OCTET_STREAM_VALUE })
    public String bigBody(@RequestBody() byte[] data) {
        return clientService.bigBody(data);
    }

    @RequestMapping("/pingServer")
    public String pingServer() {
        return clientService.pingServer();
    }


    @RequestMapping("/pingServerLoop/{loopCount}")
    public String pingServerLoop(@PathVariable("loopCount") Long loopCount) {
        return clientService.pingServerLoop(loopCount);
    }

    @RequestMapping("/pingServerLoopRx/{loopCount}")
    public String pingServerLoopRx(@PathVariable("loopCount") Long loopCount) {
        return clientService.pingServerRx(loopCount);
    }

    @RequestMapping("/cities")
    public List<City> findCities() {
        return clientService.findAll();
    }

    @RequestMapping("/city/{cityId}")
    public City findCity(@PathVariable Long cityId) {
        return clientService.findById(cityId);
    }
}