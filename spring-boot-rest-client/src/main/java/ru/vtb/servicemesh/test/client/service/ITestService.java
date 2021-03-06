package ru.vtb.servicemesh.test.client.service;

import ru.vtb.servicemesh.test.client.model.City;

import java.util.List;

public interface ITestService {

    List<City> findAll();

    City findById(Long id);

    String ping();

    String pingServer();

    String pingServerLoop(Long loopCount);
}
