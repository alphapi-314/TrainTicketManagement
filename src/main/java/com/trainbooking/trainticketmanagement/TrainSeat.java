package com.trainbooking.trainticketmanagement;

import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.time.LocalDateTime;

class TrainSeat extends Train implements DbConnection {
    String seatClass;
    String coach;
    String berth;
    int seatNumber;

    public TrainSeat(int trainNumber, String trainName, String startStation, String endStation,
                     LocalDateTime departureTime, LocalDateTime arrivalTime,
                     String seatClass, String coach, String berth, int seatNumber) {
        super(trainNumber, trainName, startStation, endStation, departureTime, arrivalTime);
        this.seatClass = seatClass;
        this.coach = coach;
        this.berth = berth;
        this.seatNumber = seatNumber;
    }

    Document toDocument() {
        return super.toDocument()
                .append("seatClass", seatClass)
                .append("coach", coach)
                .append("berth", berth)
                .append("seatNumber", seatNumber);
    }

}
