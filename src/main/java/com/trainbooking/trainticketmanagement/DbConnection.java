package com.trainbooking.trainticketmanagement;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

interface DbConnection {

    static final String URI = "mongodb://localhost:27017";
    static final MongoClient mongoClient = MongoClients.create(URI);
    static final MongoDatabase database = mongoClient.getDatabase("train");
    static final MongoCollection<Document> trainCollection = database.getCollection("trains");
    static final MongoCollection<Document> ticketCollection = database.getCollection("tickets");
    static final MongoCollection<Document> seatCollection = database.getCollection("seats");
    static final MongoCollection<Document> userCollection = database.getCollection("users");

}

