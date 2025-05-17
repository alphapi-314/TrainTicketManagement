package com.trainbooking.trainticketmanagement;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

class Ticket extends TrainSeat implements DbConnection {
    String name;
    int age;
    String gender;
    LocalDateTime bookTime;
    int pnr;
    int status;

    Ticket(int trainNumber, String trainName, String startStation, String endStation,
                  LocalDateTime departureTime, LocalDateTime arrivalTime, LocalDate date,
                  String seatClass, String coach, String berth, int seatNumber,
                  String name, int age, String gender, LocalDateTime bookTime, int pnr, int status) {

        super(trainNumber, trainName, startStation, endStation,
                departureTime, arrivalTime, date, seatClass, coach, berth, seatNumber);

        this.name = name;
        this.age = age;
        this.gender = gender;
        this.bookTime = bookTime;
        this.pnr = pnr;
        this.status = status;
    }

    Document toDocument() {
        return super.toDocument()
                .append("name", name)
                .append("age", age)
                .append("gender", gender)
                .append("bookTime", bookTime)
                .append("pnr", pnr)
                .append("status", status);
    }

    void addTicket(){
        Document ticket = this.toDocument();
        ticketCollection.insertOne(ticket);
    }

    static Map<String, Object> getTicket(int pnr) {
        Bson filter = Filters.eq("pnr", pnr);
        Document doc = ticketCollection.find(filter).first();

        if (doc != null) {
            doc.remove("_id");
            return new LinkedHashMap<>(doc);
        } else {
            return null;
        }
    }

    static String cancelTicket(int pnr) {
        Map<String, Object> ticket = getTicket(pnr);
        String response;
        if (ticket == null) {
            response = "No ticket is present with given PNR";
        }
        else if (Integer.valueOf(-1).equals(ticket.get("status"))) {
            response = "Ticket is already cancelled";
        }
        else if (Integer.valueOf(0).equals(ticket.get("status"))) {
            ticketCollection.updateOne(Filters.eq("pnr", pnr), Updates.set("status", -1));
            response = "Ticket cancelled successfully";
        }
        else {
            String seatClass = (String) ticket.get("seatClass");
            String berth = (String) ticket.get("berth");
            int seatNumber = (int) ticket.get("seatNumber");
            String startStation = (String) ticket.get("startStation");
            String endStation = (String) ticket.get("endStation");
            int trainNumber = (int) ticket.get("trainNumber");
            String trainName = (String) ticket.get("trainName");
            String coach = (String) ticket.get("coach");
            LocalDateTime departureTime = (LocalDateTime) ticket.get("departureTime");
            LocalDateTime arrivalTime = (LocalDateTime) ticket.get("arrivalTime");
            LocalDate date = (LocalDate) ticket.get("date");

            ticketCollection.updateOne(Filters.eq("pnr", pnr), Updates.set("status", -1));

            TrainSeat seat = new TrainSeat(
                    trainNumber, trainName, startStation, endStation,
                    departureTime, arrivalTime, date, seatClass, coach, berth, seatNumber);

            updateWaitlist(seat);
            response = "Ticket cancelled successfully";
        }
        return response;
    }

    private static void updateWaitlist(TrainSeat seat) {
        Bson filter = Filters.and(
                Filters.eq("status", 0),
                Filters.eq("trainNumber", seat.trainNumber),
                Filters.eq("seatClass", seat.seatClass),
                Filters.eq("startStation", seat.startStation),
                Filters.eq("endStation", seat.endStation),
                Filters.eq("departureTime", seat.departureTime),
                Filters.eq("arrivalTime", seat.arrivalTime)
        );

        Document waitingTicket = ticketCollection.find(filter)
                .sort(Sorts.ascending("bookTime"))
                .first();

        if (waitingTicket != null) {
            int waitlistPnr = waitingTicket.getInteger("pnr");
            Bson updateWaitlistTicket = Updates.combine(
                    Updates.set("status", 1),
                    Updates.set("berth", seat.berth),
                    Updates.set("coach", seat.coach),
                    Updates.set("seatNumber", seat.seatNumber)
            );
            ticketCollection.updateOne(Filters.eq("pnr", waitlistPnr), updateWaitlistTicket);
            System.out.println("Waitlisted ticket promoted to confirmed and updated with seat: " 
                    + seat.coach + "-" + seat.berth + "-" + seat.seatNumber);
        }
        else{
            cancelSeat(seat);
        }
    }
}