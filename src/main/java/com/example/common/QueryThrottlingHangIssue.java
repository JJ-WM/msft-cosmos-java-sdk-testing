package com.example.common;

import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.models.FeedResponse;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.example.common.Configurations.endpoint;
import static com.example.common.Configurations.key;

public class QueryThrottlingHangIssue {

    private final static Logger logger = LoggerFactory.getLogger(QueryThrottlingHangIssue.class);

    public static void main(String[] args) {
        CosmosClient cosmosClient = new CosmosClientBuilder()
            .endpoint(endpoint)
            .key(key)
            .buildClient();

        CosmosContainer container = cosmosClient.getDatabase("testDB").getContainer("testContainer");
        CosmosQueryRequestOptions options = new CosmosQueryRequestOptions();
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for(int i = 1; i < 30; i++) {
            executorService.submit(() -> {
                for (int j = 1; j < 5000; j++) {
                    String query = "select * from c where c.prop = 'prop-" + (10000 % j) + "'";

                    //  ASK: There is no way to provide a timeout to iterator API from CosmosPagedIterable
                    Iterator<FeedResponse<ObjectNode>> iterator = container.queryItems(query, options,
                        ObjectNode.class).iterableByPage(1).iterator();

                    while (iterator.hasNext()) {
                        FeedResponse<ObjectNode> feedResponse = iterator.next();
                        logger.info("Response is  - {}", feedResponse.getResults());
                    }
                }
            });
        }
        try {
            executorService.awaitTermination(10, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
