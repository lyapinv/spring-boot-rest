package ru.vtb.servicemesh.test.server.reactive;

import org.apache.cxf.workqueue.AutomaticWorkQueueImpl;

public class CustomAutomaticWorkQueueImpl extends AutomaticWorkQueueImpl {

    @Override
    public void execute(Runnable command) {

    }
}
