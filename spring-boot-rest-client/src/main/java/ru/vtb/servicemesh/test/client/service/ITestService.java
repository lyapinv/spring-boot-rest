package ru.vtb.servicemesh.test.client.service;

import ru.vtb.servicemesh.test.client.model.City;

import java.util.List;

public interface ITestService {

    List<City> findAll();

    City findById(Long id);

    String ping();

    String bigBody(byte[] data);

    String pingServer();

    String pingServerRx(Long loopCount);

    String pingServerLoop(Long loopCount);
}
