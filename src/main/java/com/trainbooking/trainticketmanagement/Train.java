package com.trainbooking.trainticketmanagement;

import java.time.LocalDateTime;
import org.bson.Document;

class Train {
    int trainNumber;
    String trainName;
    String startStation;
    String endStation;
    LocalDateTime departureTime;
    LocalDateTime arrivalTime;

    Train(int trainNo, String name, String start, String end, LocalDateTime departure, LocalDateTime arrival ){
        trainNumber = trainNo;
        trainName = name;
        startStation = start;
        endStation = end;
        departureTime = departure;
        arrivalTime = arrival;
    }

    Train(Train other) {
        this.trainNumber = other.trainNumber;
        this.trainName = other.trainName;
        this.startStation = other.startStation;
        this.endStation = other.endStation;
        this.departureTime = other.departureTime;
        this.arrivalTime = other.arrivalTime;
    }

    Train(Document doc) {
        this.trainNumber = doc.getInteger("trainNumber");
        this.trainName = doc.getString("trainName");
        this.startStation = doc.getString("startStation");
        this.endStation = doc.getString("endStation");
        this.departureTime = doc.get("departureTime", LocalDateTime.class);
        this.arrivalTime = doc.get("arrivalTime", LocalDateTime.class);
    }

    Document toDocument() {
        return new Document("trainNumber", trainNumber)
                .append("trainName", trainName)
                .append("startStation", startStation)
                .append("endStation", endStation)
                .append("departureTime", departureTime)
                .append("arrivalTime", arrivalTime);
    }

}
