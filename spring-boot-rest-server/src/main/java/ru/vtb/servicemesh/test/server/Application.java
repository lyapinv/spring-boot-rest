package ru.vtb.servicemesh.test.server;

import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.workqueue.AutomaticWorkQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.web.reactive.function.client.WebClient;
import ru.vtb.servicemesh.test.server.reactive.CustomAutomaticWorkQueueImpl;
import ru.vtb.test.jaxws.async.client.HelloWorldImplService;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceClient;
import java.net.MalformedURLException;
import java.net.URL;

@SpringBootApplication
@ImportResource({"classpath:META-INF/cxf/cxf.xml"})
public class Application {

    @Autowired
    private SpringBus bus;

//    @Value("${MOCK_URI}")
//    private String url;
    @Value("${WS_URI}")
    private String url;

    @Bean
    public WebClient restTemplate() {
        return WebClient.create(url);
    }

    @Bean
    public ru.vtb.test.jaxws.async.client.HelloWorld helloWorldPort() throws MalformedURLException {
        System.out.println("!!! url: " + url);
        WebServiceClient annotation = HelloWorldImplService.class.getAnnotation(WebServiceClient.class);
        QName qName = new QName(annotation.targetNamespace(), annotation.name());
        URL baseUrl = HelloWorldImplService.class.getResource("/");
        URL url = new URL(baseUrl, "wsdl/hello-1.wsdl");
        ru.vtb.test.jaxws.async.client.HelloWorld port = new HelloWorldImplService(url, qName)
                .getHelloWorldImplPort();
        BindingProvider bp = (BindingProvider)port;
        bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, "http://192.168.1.84:9999/ws/hello");
        return port;
    }

    @Bean(name = "cxf.default.workqueue")
    public AutomaticWorkQueue cxfQueue() {
        return new CustomAutomaticWorkQueueImpl(
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
