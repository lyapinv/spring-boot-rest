package ru.vtb.servicemesh.test.mock.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.vtb.servicemesh.test.mock.service.ITestService;

@RestController
public class MockController {

    @Autowired
    private ITestService serverService;

    @RequestMapping("/ping")
    public String ping() {
        return serverService.ping();
    }
}