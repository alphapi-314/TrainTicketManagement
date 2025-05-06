package com.trainbooking.trainticketmanagement;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

class DbConnection {

    private final String URI = "mongodb://localhost:27017";
    private final MongoClient mongoClient = MongoClients.create(URI);

    MongoCollection<Document> connection(String collection) {
        MongoDatabase db = mongoClient.getDatabase("train");
        return db.getCollection(collection);
    }

    void closeConnection() {
        if (mongoClient != null) {
            mongoClient.close();
            System.out.println("MongoDB connection closed.");
        }
    }
}
