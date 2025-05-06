package com.trainbooking.trainticketmanagement;

import com.mongodb.client.*;
import com.mongodb.client.model.*;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

class Server {
    private static final DbConnection obj = new DbConnection();
    private static final MongoCollection<Document> ticketCollection = obj.connection("ticket");
    private static final MongoCollection<Document> trainCollection = obj.connection("seats");
    private static final MongoCollection<Document> userCollection = obj.connection("user");

    protected Document loginAuthentication(String userName, String password){
        Bson filter = Filters.and(
                Filters.eq("userName", userName),
                Filters.eq("password", password)
        );

        return userCollection.find(filter).first();
    }

    protected Document createTicket() {
        Document doc = new Document();
        doc.append("a","b");
        return doc;
    }

    protected Document findTicket(int pnr){
        return ticketCollection.find(Filters.eq("pnr", pnr)).first();
    }

    protected boolean cancelTicket(int pnr){
        Document ticket = findTicket(pnr);
        if(ticket==null){
            System.out.println("No ticket is present with given PNR: " + pnr);
            return false;
        }
        else if (ticket.containsKey("waitList") & Integer.valueOf(1).equals(ticket.getInteger("waitList"))) {
            ticketCollection.updateOne(Filters.eq("pnr", pnr), Updates.set("status", 0));
            return true;
        }
        else {

            String seatClass = ticket.getString("seatClass");
            String berth = ticket.getString("berth");
            int seatNumber = ticket.getInteger("seatNumber");
            String startStation = ticket.getString("startStation");
            String endStation = ticket.getString("endStation");
            int trainNumber = ticket.getInteger("trainNumber");
            String trainName = ticket.getString("trainName");
            String coach = ticket.getString("coach");
            LocalDateTime departureTime = ticket.get("departureTime", LocalDateTime.class);
            LocalDateTime arrivalTime = ticket.get("arrivalTime", LocalDateTime.class);

            ticketCollection.updateOne(Filters.eq("pnr", pnr), Updates.set("status", 0));

            TrainSeat seat = new TrainSeat(trainNumber, trainName, startStation, endStation,
                                            departureTime, arrivalTime, seatClass, coach,
                                            berth, seatNumber);

            updateWaitlist(seat);

            return true;
        }
    }

    protected void updateWaitlist(TrainSeat seat){
        Bson filter = Filters.and(
                Filters.exists("waitList", true),
                Filters.eq("waitList", 1),
                Filters.eq("trainNumber", seat.trainNumber),
                Filters.eq("seatClass", seat.seatClass),
                Filters.eq("startStation", seat.startStation),
                Filters.eq("endStation", seat.endStation)
        );

        Document ticket = ticketCollection.find(filter)
                .sort(Sorts.ascending("bookTime"))
                .first();

        if (ticket!=null){
            int pnr = ticket.getInteger("pnr");
            Bson filter_pnr = Filters.eq("pnr", pnr);
            Bson update = Updates.combine(
                    Updates.set("status", 1),
                    Updates.set("waitList", 0),
                    Updates.set("berth", seat.berth),
                    Updates.set("coach", seat.coach),
                    Updates.set("seatNumber", seat.seatNumber)
            );

            ticketCollection.updateOne(filter_pnr, update);
            System.out.println("Ticket updated successfully.");

        }

    }

//    protected Document seatAllocation(){
//
//    }

    protected List<Document> searchTrains(String startStation, String endStation, LocalDate date){
        Date queryDate = java.sql.Date.valueOf(date);
        Bson filter = Filters.and(
                Filters.eq("startStation", startStation),
                Filters.eq("endStation", endStation),
                Filters.eq("date", queryDate)
        );

        FindIterable<Document> iterable = trainCollection.find(filter);

        List<Document> result = new ArrayList<>();
        for (Document doc : iterable) {
            result.add(doc);
        }
        return result;
    }


}
