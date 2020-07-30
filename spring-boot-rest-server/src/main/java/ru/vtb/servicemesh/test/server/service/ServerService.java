package ru.vtb.servicemesh.test.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.vtb.servicemesh.test.server.model.City;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ServerService implements ITestService {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final AtomicInteger count = new AtomicInteger(0);

    @Autowired
    private RestTemplate restTemplate;

    @Value("${MOCK_URI}")
    private String mockURL;

    private final List<City> cities = Arrays.asList(
            new City(1L, "Bratislava", 432000),
            new City(2L, "Budapest", 1759000),
            new City(3L, "Berlin", 3671000)
    );

    @Override
    public List<City> findAll() {
        logger.info(" Call server findAll()");
        return cities;
    }

    @Override
    public City findById(Long id) {
        logger.info(" Call server findById({})", id);
        return cities.stream()
                .filter(c -> c.getId() == 1)
                .findFirst().get();
    }

    @Override
    public String ping() {
        int sleepSecValue = 10;
        long currentTimeNano = System.nanoTime();
        int iteration = count.addAndGet(1);
        logger.info("{} - server ping/pong request. currentTimeMill: {}. Sleep for {}sec", iteration, currentTimeNano, sleepSecValue);
        try {
            TimeUnit.SECONDS.sleep(sleepSecValue);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return iteration + " - servers pong. currentTimeMill=" + currentTimeNano;
    }


    @Override
    public String pingMock() {
        logger.info(" Call ping mock ");
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(mockURL + "/ping", String.class);
        String response = responseEntity.getBody();
        logger.info("!!! response from mock: {}", response);
        return response;
    }
}