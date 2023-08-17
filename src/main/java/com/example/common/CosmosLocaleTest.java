package com.example.common;

import com.azure.cosmos.CosmosAsyncClient;
import com.azure.cosmos.CosmosAsyncContainer;
import com.azure.cosmos.CosmosAsyncDatabase;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.cosmos.models.CosmosItemResponse;
import com.azure.cosmos.models.PartitionKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class CosmosLocaleTest {
    private static final Logger logger = LoggerFactory.getLogger(CosmosLocaleTest.class);

    public static void main(String[] args) {
        CosmosAsyncClient cosmosAsyncClient = new CosmosClientBuilder()
            .endpoint(Configurations.endpoint)
            .key(Configurations.key)
            .contentResponseOnWriteEnabled(true)
            .buildAsyncClient();

        CosmosAsyncDatabase cosmosAsyncDatabase = cosmosAsyncClient.getDatabase("testdb");
        CosmosAsyncContainer cosmosAsyncContainer = cosmosAsyncDatabase.getContainer("testcontainer");
        String id = UUID.randomUUID().toString();
        String pk = UUID.randomUUID().toString();
        Family f = new Family(id, "परीक्षण", "परीक्षण", pk);

        cosmosAsyncContainer.createItem(f)
                            .map(cosmosItemResponse -> {
                                Family item = cosmosItemResponse.getItem();
                                logger.info("item is : {}", item);
                                return item;
                            })
                            .block();

        CosmosItemResponse<Family> cosmosItemResponse = cosmosAsyncContainer
            .readItem(id, new PartitionKey(pk), Family.class).block();

        Family readFamily = cosmosItemResponse.getItem();
        assert readFamily.id.equals(f.id);
        assert readFamily.firstName.equals(f.firstName);
        assert readFamily.lastName.equals(f.lastName);
        assert readFamily.myPk.equals(f.myPk);
    }

    private static class Family {
        private String id;
        private String firstName;
        private String lastName;
        private String myPk;

        public Family() {}

        public Family(String id, String firstName, String lastName, String myPk) {
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
            this.myPk = myPk;
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

        @Override
        public String toString() {
            return "Family{" +
                "id='" + id + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", myPk='" + myPk + '\'' +
                '}';
        }
    }
}
