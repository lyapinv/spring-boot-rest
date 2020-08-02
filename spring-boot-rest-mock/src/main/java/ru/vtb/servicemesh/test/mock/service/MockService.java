package ru.vtb.servicemesh.test.mock.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class MockService implements ITestService {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final AtomicInteger count = new AtomicInteger(0);

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
}