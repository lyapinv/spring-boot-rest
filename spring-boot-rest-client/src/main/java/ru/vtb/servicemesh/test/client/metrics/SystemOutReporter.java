package ru.vtb.servicemesh.test.client.metrics;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.TimeUnit;

public class SystemOutReporter implements InitializingBean {

    @Autowired
    private MetricRegistry metricRegistry;

    @Override
    public void afterPropertiesSet() throws Exception {
        ConsoleReporter reporter = ConsoleReporter.forRegistry(this.metricRegistry)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build();
        reporter.start(1, TimeUnit.SECONDS);
    }
}
