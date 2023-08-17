package com.example.common;

import com.azure.cosmos.CosmosAsyncClient;
import com.azure.cosmos.CosmosAsyncContainer;
import com.azure.cosmos.CosmosAsyncDatabase;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.cosmos.models.CosmosContainerProperties;
import com.azure.cosmos.models.CosmosItemRequestOptions;
import com.azure.cosmos.models.ThroughputProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyMemoryIssue {

    private static Logger logger = LoggerFactory.getLogger(NettyMemoryIssue.class);

    public static void main(String[] args) throws InterruptedException {

        CosmosAsyncClient cosmosClient = new CosmosClientBuilder()
                .endpoint(Configurations.endpoint)
                .key(Configurations.key)
                .contentResponseOnWriteEnabled(true)
                .gatewayMode()
                .buildAsyncClient();

        cosmosClient.createDatabaseIfNotExists("testdb").block();
        CosmosAsyncDatabase testdb = cosmosClient.getDatabase("testdb");
        CosmosContainerProperties cosmosContainerProperties = new CosmosContainerProperties("testcontainer", "/pk");
        CosmosAsyncContainer testcontainer1 = testdb.getContainer("testcontainer");
        try {
            testcontainer1.delete().block();
        } catch (Exception e) {
            logger.info("Container does not exist");
        }

        testdb.createContainerIfNotExists(cosmosContainerProperties, ThroughputProperties.createManualThroughput(100000)).block();
        CosmosAsyncContainer testcontainer = testdb.getContainer("testcontainer");

        for (int i = 0; i < 1000; i++) {
            logger.info("Creating item " + i);
            testcontainer.createItem(new TestItem("id" + i, "pk" + i), new CosmosItemRequestOptions())
                    .subscribe(response -> {
                        logger.info("Item created " + response.getItem().getId());
                    }, throwable -> {
                        logger.error("Error creating item", throwable);
                    });
        }

        Thread.sleep(1000000);
    }
}
