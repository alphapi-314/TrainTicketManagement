package com.trainbooking.trainticketmanagement;

import com.mongodb.client.*;
import org.bson.Document;

import java.io.Serializable;

public class Test implements DbConnection {
    public static void main(String[] args) {
        Document doc = new Document();
        doc.append("name", "Shiv");
        doc.append("pnr", 1234);
        doc.append("train_number", 14200);
        doc.append("seat class", "2A");
        doc.append("coach number", "A1");
        doc.append("seat number", 34);
        doc.append("berth type", "lower");
        doc.append("start", "dehradun");
        doc.append("end", "haldwani");

        ticketCollection.insertOne(doc);

        System.out.println("Inserted success");

    }
}
