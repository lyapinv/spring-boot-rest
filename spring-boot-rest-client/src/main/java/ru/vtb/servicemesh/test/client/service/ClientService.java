package ru.vtb.servicemesh.test.client.service;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.RestTemplate;
import ru.vtb.servicemesh.test.client.model.City;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
public class ClientService implements ITestService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private ExecutorService executorService = null;
    private CountDownLatch latch;

    @Autowired
    private MetricRegistry metricRegistry;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${SERVER_URI}")
    private String serverServiceURL;

    public static final String METRICS_PREFIX = "meter-api.";

    @Override
    public List<City> findAll() {
        logger.info(" Call client findAll()");
        ResponseEntity<City[]> responseEntity = restTemplate.getForEntity(serverServiceURL + "/cities", City[].class);
        City[] response = responseEntity.getBody();
        logger.info("!!! List response: {}", response);
        return Arrays.asList(response);
    }

    @Override
    public City findById(Long id) {
        logger.info(" Call client findById({})", id);
        ResponseEntity<City> responseEntity = restTemplate.getForEntity(serverServiceURL + "/city", City.class);
        City response = responseEntity.getBody();
        logger.info("!!! response: {}", response);
        return response;
    }

    @Override
    public String ping() {
        logger.info(" client ping/pong request");
        return "clients pong";
    }

    @Override
    public String pingServer() {
        logger.info(" Call ping server ");
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(serverServiceURL + "/ping", String.class);
        String response = responseEntity.getBody();
        logger.info("!!! response from server: {}", response);
        return response;
    }

    @Override
    public String pingServerRx(Long loopCount) {
        logger.info(" Call ping serverrx ");
        return pingRemoteServer(loopCount, "/pingrx");
    }

    @Override
    public String pingServerRxMock(Long loopCount) {
        logger.info(" Call ping serverrx ");
        return pingRemoteServer(loopCount, "/pingrx_mock");
    }

    @Override
    public String pingServerRxWs(Long loopCount) {
        logger.info(" Call ping serverrx ");
        return pingRemoteServer(loopCount, "/pingrx_ws");
    }

    @Override
    public String pingServerLoop(Long loopCount) {
        return pingRemoteServer(loopCount, "/ping");
    }

    private String pingRemoteServer(Long loopCount, String uriPath) {
        logger.info("!!! Call ping server loop. loopCount= {}", loopCount);
        int threadsCount = loopCount.intValue() == -1 ? 300 : loopCount.intValue();
        executorService = Executors.newFixedThreadPool(threadsCount);
        latch = new CountDownLatch(loopCount.intValue() == -1 ? Integer.MAX_VALUE : loopCount.intValue());

        long startTime = System.currentTimeMillis();
        try {
            int callIterations = loopCount.intValue() == -1 ? Integer.MAX_VALUE : loopCount.intValue();
            for (int i = 0; i < callIterations; i++) {
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        Timer.Context context = metricRegistry.timer(METRICS_PREFIX + "pingServerRxMock").time();

                        ResponseEntity<String> responseEntity = restTemplate.getForEntity(serverServiceURL + uriPath, String.class);
                        String response = responseEntity.getBody();
                        logger.info("!!! Thread {} - response from server: {}", Thread.currentThread().getName(), response);
                        latch.countDown();
                        context.stop();
                    }
                });
                logger.info("!!! Call ping server loop. iteration: {}", i);
                TimeUnit.MILLISECONDS.sleep(10);
            }
            latch.await(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.error("Exception handled on latch waiting.", e);
        }

        double timeInSec = (System.currentTimeMillis() - startTime)/1000;

        logger.info("!!! responses from server received. LoopCount: {}, Avg tps: {}", loopCount, String.format("%.2f", loopCount/timeInSec));
        return "Looped response received. LoopCount=" + loopCount;
    }

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<Object> handleException(Exception e) {
        logger.error("!!! Error on A service. Return http 503 error code: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(e.getMessage());
    }
}