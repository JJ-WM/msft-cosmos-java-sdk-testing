package com.example.common;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.config.NamingConvention;
import io.micrometer.core.instrument.dropwizard.DropwizardConfig;
import io.micrometer.core.instrument.dropwizard.DropwizardMeterRegistry;
import io.micrometer.core.instrument.util.HierarchicalNameMapper;
import io.micrometer.core.lang.Nullable;

import java.util.concurrent.TimeUnit;

public final class ConsoleLoggingRegistryFactory {
    public ConsoleLoggingRegistryFactory() {
    }

    public static MeterRegistry create(int step) {
        MetricRegistry dropwizardRegistry = new MetricRegistry();
        final ConsoleReporter consoleReporter = ConsoleReporter.forRegistry(dropwizardRegistry).convertRatesTo(TimeUnit.SECONDS).convertDurationsTo(TimeUnit.MILLISECONDS).build();
        consoleReporter.start((long)step, TimeUnit.SECONDS);
        DropwizardConfig dropwizardConfig = new DropwizardConfig() {
            public String get(@Nullable String key) {
                return null;
            }

            public String prefix() {
                return "console";
            }
        };
        DropwizardMeterRegistry consoleLoggingRegistry = new DropwizardMeterRegistry(dropwizardConfig, dropwizardRegistry, HierarchicalNameMapper.DEFAULT, Clock.SYSTEM) {
            protected Double nullGaugeValue() {
                return Double.NaN;
            }

            public void close() {
                super.close();
                consoleReporter.stop();
                consoleReporter.close();
            }
        };
        consoleLoggingRegistry.config().namingConvention(NamingConvention.dot);
        return consoleLoggingRegistry;
    }
}
