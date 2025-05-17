package com.trainbooking.trainticketmanagement;

import com.mongodb.client.*;
import org.bson.Document;

interface DbConnection {

    String URI = "mongodb://localhost:27017";
    MongoClient mongoClient = MongoClients.create(URI);
    MongoDatabase database = mongoClient.getDatabase("train");
    MongoCollection<Document> trainCollection = database.getCollection("trains");
    MongoCollection<Document> ticketCollection = database.getCollection("tickets");
    MongoCollection<Document> seatCollection = database.getCollection("seats");
    MongoCollection<Document> userCollection = database.getCollection("users");

}

