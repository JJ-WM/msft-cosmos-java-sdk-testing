package com.example.common;

import com.azure.cosmos.CosmosAsyncClient;
import com.azure.cosmos.CosmosAsyncContainer;
import com.azure.cosmos.CosmosAsyncDatabase;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.cosmos.implementation.Utils;
import com.azure.cosmos.models.CosmosItemRequestOptions;
import com.azure.cosmos.models.CosmosItemResponse;
import com.azure.cosmos.models.PartitionKey;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;

public class CosmosCRUDExample {

    private static final Logger logger = LoggerFactory.getLogger(CosmosCRUDExample.class);

    public static void main(String[] args) {
        CosmosAsyncClient cosmosAsyncClient = new CosmosClientBuilder()
            .endpoint(Configurations.endpoint)
            .key(Configurations.key)
            .contentResponseOnWriteEnabled(true)
            .buildAsyncClient();

//        Utils.getSimpleObjectMapper().registerModule(new JavaTimeModule());

        CosmosAsyncDatabase cosmosAsyncDatabase = cosmosAsyncClient.getDatabase("testdb");
        CosmosAsyncContainer cosmosAsyncContainer = cosmosAsyncDatabase.getContainer("testcontainer");


        Family family = new Family("id-5", "Kushagra", "Thapar", "pk-3", Instant.now());
        logger.info("Creating Item");
        cosmosAsyncContainer.createItem(family, new PartitionKey(family.getMyPk()), new CosmosItemRequestOptions()).block();
        logger.info("Item created");

        logger.info("Reading item");
        CosmosItemResponse<Family> cosmosItemResponse = cosmosAsyncContainer.readItem(family.getId(),
            new PartitionKey(family.getMyPk()), Family.class).block();

        logger.info("Cosmos Item response is : {}", cosmosItemResponse.getItem());
    }

    private static class Family {
        private String id;
        private String firstName;
        private String lastName;
        private String myPk;
        private Instant time;

        public Family(){}

        public Family(String id, String firstName, String lastName, String myPk, Instant time) {
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
            this.myPk = myPk;
            this.time = time;
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

        public String getMyPk() {
            return myPk;
        }

        public void setMyPk(String myPk) {
            this.myPk = myPk;
        }

        public Instant getTime() {
            return time;
        }

        public void setTime(Instant time) {
            this.time = time;
        }

        @Override
        public String toString() {
            return "Family{" +
                "id='" + id + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", pk='" + myPk + '\'' +
                ", time=" + time +
                '}';
        }
    }
}
