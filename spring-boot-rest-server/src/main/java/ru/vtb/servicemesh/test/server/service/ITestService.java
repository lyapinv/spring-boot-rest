package ru.vtb.servicemesh.test.server.service;

import ru.vtb.servicemesh.test.server.model.City;

import java.util.List;

public interface ITestService {

    List<City> findAll();

    City findById(Long id);

    String ping();
}
