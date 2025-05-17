package com.trainbooking.trainticketmanagement;

import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
}