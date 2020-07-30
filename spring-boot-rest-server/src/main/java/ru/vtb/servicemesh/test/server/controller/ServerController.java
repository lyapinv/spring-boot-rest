package ru.vtb.servicemesh.test.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.vtb.servicemesh.test.server.model.City;
import ru.vtb.servicemesh.test.server.service.ITestService;

import java.util.List;

@RestController
public class ServerController {

    @Autowired
    private ITestService serverService;

    @RequestMapping("/ping")
    public String ping() {
        return serverService.ping();
    }

    @RequestMapping("/pingMock")
    public String pingMock() {
        return serverService.pingMock();
    }

    @RequestMapping("/cities")
    public List<City> findCities() {
        return serverService.findAll();
    }

    @RequestMapping("/city/{cityId}")
    public City findCity(@PathVariable Long cityId) {
        return serverService.findById(cityId);
    }
}