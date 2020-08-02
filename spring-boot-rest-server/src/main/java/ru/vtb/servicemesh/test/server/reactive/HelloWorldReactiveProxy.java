//package ru.vtb.servicemesh.test.server.reactive;
//
//import reactor.core.publisher.Mono;
//import ru.vtb.test.jaxws.async.client.HelloWorldImplService;
//
//import static ru.vtb.servicemesh.test.server.reactive.ReactorAsyncHandler.into;
//
//public class HelloWorldReactiveProxy {
//    public Mono<String> operation(String input) {
//
//        return Mono.create(sink -> {
//            ru.vtb.test.jaxws.async.client.HelloWorld hello =
//                    new HelloWorldImplService().getHelloWorldImplPort();
//            hello.getHelloWorldAsStringAsync(input, into(sink));
//        });
//    }
//}
