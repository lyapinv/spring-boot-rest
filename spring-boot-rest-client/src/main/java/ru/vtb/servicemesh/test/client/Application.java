package ru.vtb.servicemesh.test.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import ru.vtb.servicemesh.test.client.metrics.MetricRegistryFactory;
import ru.vtb.servicemesh.test.client.metrics.SystemOutReporter;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder.build();
    }

    @Bean
    public MetricRegistryFactory metricRegistryFactory() {
        return new MetricRegistryFactory();
    }

    @Bean
    public SystemOutReporter systemOutReporter() {
        return new SystemOutReporter();
    }
}
