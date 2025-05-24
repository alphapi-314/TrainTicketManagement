package com.trainbooking.trainticketmanagement;

import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.time.*;
import java.util.*;

class TrainSeat extends Train implements DbConnection {
    String seatClass;
    String coach;
    String berth;
    int seatNumber;
    
    TrainSeat(int trainNumber, String trainName, String startStation, String endStation,
                     LocalDateTime departureTime, LocalDateTime arrivalTime, LocalDate date,
                     String seatClass, String coach, String berth, int seatNumber) {
        super(trainNumber, trainName, startStation, endStation, departureTime, arrivalTime, date);
        this.seatClass = seatClass;
        this.coach = coach;
        this.berth = berth;
        this.seatNumber = seatNumber;
    }

    TrainSeat(TrainSeat seat){
        super(seat.trainNumber, seat.trainName, seat.startStation, seat.endStation,
                seat.departureTime, seat.arrivalTime, seat.date);
        this.seatClass = seat.seatClass;
        this.coach = seat.coach;
        this.berth = seat.berth;
        this.seatNumber = seat.seatNumber;
    }

    Document toDocument() {
        return super.toDocument()
                .append("seatClass", seatClass)
                .append("coach", coach)
                .append("berth", berth)
                .append("seatNumber", seatNumber);
    }
    
    protected static void bookSeat(TrainSeat seat){
        Bson filter = Filters.and(
                Filters.eq("trainNumber", seat.trainNumber),
                Filters.eq("date", seat.date),
                Filters.eq("seatClass", seat.seatClass),
                Filters.eq("coach", seat.coach),
                Filters.eq("seatNumber", seat.seatNumber)
        );

        Document doc = seatCollection.find(filter).first();
        if (doc != null) {
            List<Document> bookings = (List<Document>) doc.get("bookings");
            boolean flag = false;
            for (Document booking : bookings) {
                String from = booking.getString("from");
                String to = booking.getString("to");

                if (from.equalsIgnoreCase(seat.startStation)) flag = true;
                if (flag) booking.put("status", 1);
                if (to.equalsIgnoreCase(seat.endStation)) break;
            }
            Bson update = new Document("$set", new Document("bookings", bookings));
            seatCollection.updateOne(filter, update);
        }
    }
    
    protected static void cancelSeat(TrainSeat seat) {
        Bson filter = Filters.and(
            Filters.eq("trainNumber", seat.trainNumber),
            Filters.eq("date", seat.date),
            Filters.eq("seatClass", seat.seatClass),
            Filters.eq("coach", seat.coach),
            Filters.eq("seatNumber", seat.seatNumber)
        );

        Document doc = seatCollection.find(filter).first();
        if (doc != null) {
            List<Document> bookings = (List<Document>) doc.get("bookings");
            boolean flag = false;
            for (Document booking : bookings) {
                String from = booking.getString("from");
                String to = booking.getString("to");

                if (from.equalsIgnoreCase(seat.startStation)) flag = true;
                if (flag) booking.put("status", 0);
                if (to.equalsIgnoreCase(seat.endStation)) break;
            }
            Bson update = new Document("$set", new Document("bookings", bookings));
            seatCollection.updateOne(filter, update);
        }
    }

    protected static TrainSeat seatAllocation(Map<String, Object> ticket, String seatClass, String coach, String berth){
        Bson filter = Filters.and(
                Filters.eq("trainNumber", (int) ticket.get("trainNumber")),
                Filters.eq("date", ticket.get("date").toString()),
                Filters.eq("seatClass", ticket.get("seatClass").toString())
        );
        return null;
    }
}