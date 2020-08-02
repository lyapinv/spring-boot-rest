package ru.vtb.servicemesh.test.server;

import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.workqueue.AutomaticWorkQueue;
import org.apache.cxf.workqueue.AutomaticWorkQueueImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.web.reactive.function.client.WebClient;
import ru.vtb.test.jaxws.async.client.HelloWorldImplService;

@SpringBootApplication
@ImportResource({"classpath:META-INF/cxf/cxf.xml"})
public class Application {

    @Autowired
    private SpringBus bus;

    @Value("${MOCK_URI}")
    private String url;

    @Bean
    public WebClient restTemplate() {
        return WebClient.create(url);
    }

    @Bean
    public ru.vtb.test.jaxws.async.client.HelloWorld helloWorldPort() {
        return new HelloWorldImplService().getHelloWorldImplPort();
    }

    @Bean(name = "cxf.default.workqueue")
    public AutomaticWorkQueue cxfQueue() {
        return new AutomaticWorkQueueImpl(
                1000,
                0,
                3,
                3,
                2 * 60 * 1000,
                "default");
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
