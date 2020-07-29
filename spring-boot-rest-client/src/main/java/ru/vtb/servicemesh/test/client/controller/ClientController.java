package ru.vtb.servicemesh.test.client.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.vtb.servicemesh.test.client.model.City;
import ru.vtb.servicemesh.test.client.service.ITestService;

import java.util.List;

@RestController
public class ClientController {

    @Autowired
    private ITestService clientService;

    @RequestMapping("/ping")
    public String ping() {
        return clientService.ping();
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