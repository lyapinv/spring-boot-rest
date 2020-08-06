package ru.vtb.test.jaxws.async;

import javax.xml.ws.Endpoint;

public class HelloWorldPublisher {

    public static void main(String[]args) {
        Endpoint.publish("http://0.0.0.0:9999/ws/hello", new HelloWorldImpl());
    }
}
