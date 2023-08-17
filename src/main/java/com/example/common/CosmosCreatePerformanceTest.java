package com.example.common;

import com.azure.cosmos.CosmosAsyncClient;
import com.azure.cosmos.CosmosAsyncContainer;
import com.azure.cosmos.CosmosAsyncDatabase;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.cosmos.CosmosDiagnostics;
import com.azure.cosmos.DirectConnectionConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.scheduler.Schedulers;

import java.util.UUID;

public class CosmosCreatePerformanceTest {

    private static final Logger logger = LoggerFactory.getLogger(CosmosCreatePerformanceTest.class);

    public static void main(String[] args) throws InterruptedException {
        CosmosAsyncClient cosmosAsyncClient = new CosmosClientBuilder()
            .endpoint(Configurations.endpoint)
            .key(Configurations.key)
            .contentResponseOnWriteEnabled(true)
            .directMode(DirectConnectionConfig.getDefaultConfig().setMaxConnectionsPerEndpoint(1))
            .buildAsyncClient();

        CosmosAsyncDatabase cosmosAsyncDatabase = cosmosAsyncClient.getDatabase("testdb");
        CosmosAsyncContainer cosmosAsyncContainer = cosmosAsyncDatabase.getContainer("testcontainer");

        for (int i = 0; i < 1; i++) {
            Pojo pojo = createPojo();
            cosmosAsyncContainer.createItem(pojo)
                                .doOnSuccess(cosmosItemResponse -> {
                                    CosmosDiagnostics diagnostics = cosmosItemResponse.getDiagnostics();
                                    logger.info("diagnostics is : {}", diagnostics);
                                })
                                .subscribeOn(Schedulers.boundedElastic()).subscribe();
        }

        Thread.sleep(30000);
    }

    private static Pojo createPojo() {
        String id = UUID.randomUUID().toString();
        String pk = "pk";
        String field = "field-" + id;
        return new Pojo(id, pk, field);
    }
}
