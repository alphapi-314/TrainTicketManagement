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

    User(String username, String password) {
        this.userName = username;
        this.password = password;
    }

    User(Document doc){
        this.userName = doc.getString("userName");
        this.password = doc.getString("password");
        this.emailID = doc.getString("emailID");
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

    Document getUser(){
        Bson filter = Filters.and(
                Filters.eq("userName", userName),
                Filters.eq("password", password)
        );

        return userCollection.find(filter).first();
    }

}
