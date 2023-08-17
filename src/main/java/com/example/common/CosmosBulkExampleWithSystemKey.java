package com.example.common;

import com.azure.cosmos.CosmosAsyncClient;
import com.azure.cosmos.CosmosAsyncContainer;
import com.azure.cosmos.CosmosAsyncDatabase;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.cosmos.models.CosmosBulkOperations;
import com.azure.cosmos.models.CosmosItemOperation;
import com.azure.cosmos.models.CosmosItemRequestOptions;
import com.azure.cosmos.models.CosmosItemResponse;
import com.azure.cosmos.models.PartitionKey;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

public class CosmosBulkExampleWithSystemKey {

    private static final Logger logger = LoggerFactory.getLogger(CosmosBulkExampleWithSystemKey.class);

    public static void main(String[] args) {
        CosmosAsyncClient cosmosAsyncClient = new CosmosClientBuilder()
            .endpoint(Configurations.endpoint)
            .key(Configurations.key)
            .contentResponseOnWriteEnabled(true)
            .buildAsyncClient();

        CosmosAsyncDatabase cosmosAsyncDatabase = cosmosAsyncClient.getDatabase("testdb");
        CosmosAsyncContainer cosmosAsyncContainer = cosmosAsyncDatabase.getContainer("testcontainer");


        Family family = new Family("id-4", "Kushagra", "Thapar", "pk-3");
        logger.info("Creating Item");
        cosmosAsyncContainer.createItem(family, new PartitionKey(family.get_partitionKey()), new CosmosItemRequestOptions()).block();
        logger.info("Item created");

        logger.info("Creating items through bulk");
        family = new Family("id-5", "Kushagra", "Thapar", "pk-4");
        CosmosItemOperation createItemOperation = CosmosBulkOperations.getCreateItemOperation(family,
            new PartitionKey(family.get_partitionKey()));
        cosmosAsyncContainer.executeBulkOperations(Flux.just(createItemOperation)).blockLast();
        logger.info("Items through bulk created");

        CosmosItemResponse<JsonNode> cosmosItemResponse = cosmosAsyncContainer.readItem("075a9793-d096-4e6f"
            + "-83b4-c774d740f54a", PartitionKey.NONE, JsonNode.class).block();

        logger.info("Cosmos Item response is : {}", cosmosItemResponse.getItem().toPrettyString());
    }

    private static class Family {
        private String id;
        private String firstName;
        private String lastName;
        private String _partitionKey;

        public Family(String id, String firstName, String lastName, String _partitionKey) {
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
            this._partitionKey = _partitionKey;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String get_partitionKey() {
            return _partitionKey;
        }

        public void set_partitionKey(String _partitionKey) {
            this._partitionKey = _partitionKey;
        }
    }
}
