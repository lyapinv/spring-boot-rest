package ru.vtb.servicemesh.test.server.reactive;

import lombok.extern.slf4j.Slf4j;
import org.apache.cxf.workqueue.AutomaticWorkQueueImpl;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
public class CustomAutomaticWorkQueueImpl extends AutomaticWorkQueueImpl {

    public CustomAutomaticWorkQueueImpl(int mqs, int initialThreads, int highWaterMark, int lowWaterMark,
                                        long dequeueTimeout, String name) {
        super(mqs, initialThreads, highWaterMark, lowWaterMark, dequeueTimeout, name);
    }

//    @Override
//    public void execute(Runnable command) {
//        log.info("CustomAutomaticWorkQueueImpl execute...");
//        Mono.fromRunnable(command)
//                .subscribeOn(Schedulers.elastic())
//                .subscribe();
//    }


}
