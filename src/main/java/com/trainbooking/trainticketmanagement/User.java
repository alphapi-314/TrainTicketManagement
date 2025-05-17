package com.trainbooking.trainticketmanagement;

import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;

class User implements DbConnection {
    String userName;
    private final String password;
    String emailID;

    User(String username, String emailID, String password) {
        this.userName = username;
        this.password = password;
        this.emailID = emailID;
    }

    Document toDocument(){
        return new Document("userName",userName)
                .append("password",password)
                .append("emailID",emailID);
    }

    void addUser(){
        Document doc = this.toDocument();
        userCollection.insertOne(doc);
    }

    static boolean getUser(String userName, String password) {
        Bson filter = Filters.and(
                Filters.eq("userName", userName),
                Filters.eq("password", password)
        );

        return userCollection.find(filter).first() != null;
    }

}
