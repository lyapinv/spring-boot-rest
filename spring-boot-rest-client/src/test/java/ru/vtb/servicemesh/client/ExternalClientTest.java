//package ru.vtb.servicemesh.client;
//
//import lombok.extern.slf4j.Slf4j;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.ResponseEntity;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.Arrays;
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//@RunWith(SpringRunner.class)
//@SpringBootTest
//@Slf4j
//public class ExternalClientTest {
//
//    @Autowired
//    private RestTemplate restTemplate;
//
//    @Value("${CLIENTS_URI}")
//    private String clientsURL;
//
//    static byte[] data = new byte[1024 * 1024 * 10];
//
//    static {
//        Arrays.fill(data, (byte) '-');
//    }
//
//    @Test
//    public void testPingCallForClientInServiceMesh() {
//        log.info(" Call client in SM. Method: ping");
//            ResponseEntity<String> responseEntity = restTemplate.getForEntity(clientsURL + "/ping", String.class);
//            log.info("Result: {}", responseEntity.getBody());
//    }
//
//    @Test
//    public void testPingChainCallForInServiceMesh() {
//        log.info(" Call client in SM. Method: ping");
//        for (int i = 0; i < 1_000; i++) {
//            ResponseEntity<String> responseEntity = restTemplate.getForEntity(clientsURL + "/pingServer", String.class);
//            log.info("Result: {}", responseEntity.getBody());
//        }
//    }
//
//    @Test
//    public void testBigBodyCallForClientInServiceMesh() throws InterruptedException {
//        log.info(" Call client in SM. Method: byteArray body");
//        ExecutorService executor = Executors.newFixedThreadPool(5);
//        int loops = 30;
//        CountDownLatch latch = new CountDownLatch(loops);
//
//        for (int i = 0; i < 30; i++) {
//            final int loopCount = i;
//            HttpEntity<byte[]> entity = new HttpEntity<>(data);
//            executor.execute(() -> {
//                ResponseEntity<String> responseEntity = restTemplate.postForEntity(clientsURL + "/bigBody", entity,
//                        String.class);
//                log.info("{} result: {}", loopCount, responseEntity.getBody());
//                latch.countDown();
//            });
//        }
//        latch.await();
//    }
//}
