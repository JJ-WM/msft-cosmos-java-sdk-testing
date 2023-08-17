package com.example.common;

import com.azure.cosmos.BridgeInternal;
import com.azure.cosmos.CosmosAsyncClient;
import com.azure.cosmos.CosmosAsyncContainer;
import com.azure.cosmos.CosmosAsyncDatabase;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.cosmos.models.PartitionKey;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;
import com.fasterxml.jackson.databind.JsonNode;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.util.HierarchicalNameMapper;
import io.micrometer.graphite.GraphiteConfig;
import io.micrometer.graphite.GraphiteMeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

public class CosmosMetricsRegistryExample {

    private final static Logger logger = LoggerFactory.getLogger(CosmosMetricsRegistryExample.class);

    public static void main(String[] args) throws Exception {

        final Graphite graphite = new Graphite(new InetSocketAddress(3000));
        final MetricRegistry metricsRegistry = new MetricRegistry();

        GraphiteReporter reporter = GraphiteReporter.forRegistry(metricsRegistry)
                                                 .convertRatesTo(TimeUnit.SECONDS)
                                                 .convertDurationsTo(TimeUnit.MILLISECONDS)
                                                 .filter(MetricFilter.ALL)
                                                 .build(graphite);

        GraphiteMeterRegistry graphiteMeterRegistry = new GraphiteMeterRegistry(GraphiteConfig.DEFAULT, Clock.SYSTEM, HierarchicalNameMapper.DEFAULT, metricsRegistry, reporter);

        CosmosAsyncClient cosmosAsyncClient = new CosmosClientBuilder()
            .endpoint(Configurations.endpoint)
            .key(Configurations.key)
            .buildAsyncClient();

        BridgeInternal.monitorTelemetry(graphiteMeterRegistry);

        //force an exception
        CosmosAsyncDatabase database = cosmosAsyncClient.getDatabase("testdb");
        CosmosAsyncContainer container = database.getContainer("testcontainer");
        while (true) {
            try {
                container.readItem("test", new PartitionKey("test"), JsonNode.class).block();
            } catch (Exception e) {
                logger.error("Error reading item");
            }
        }
    }
}