package com.trainbooking.trainticketmanagement;

import com.mongodb.client.model.*;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.time.*;
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
            Object depObj = doc.get("departureTime");
            LocalDateTime depLdt;
            if (depObj instanceof Date) {
                depLdt = Instant.ofEpochMilli(((Date) depObj).getTime())
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime();
            } else {
                // assume String
                depLdt = LocalDateTime.parse(depObj.toString());
            }
            doc.put("departureTime", depLdt);

            // ---- normalize arrivalTime to LocalDateTime ----
            Object arrObj = doc.get("arrivalTime");
            LocalDateTime arrLdt;
            if (arrObj instanceof Date) {
                arrLdt = Instant.ofEpochMilli(((Date) arrObj).getTime())
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime();
            } else {
                arrLdt = LocalDateTime.parse(arrObj.toString());
            }
            doc.put("arrivalTime", arrLdt);

            // ---- normalize date to LocalDate ----
            Object dateObj = doc.get("date");
            LocalDate localDate;
            if (dateObj instanceof Date) {
                localDate = ((Date) dateObj).toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
            } else {
                localDate = LocalDate.parse(dateObj.toString());
            }
            doc.put("date", localDate);

            return new LinkedHashMap<>(doc);
        } else {
            return null;
        }
    }

    static List<Object> cancelTicket(int pnr) {
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
//            String departureTimeStr = (String) ticket.get("departureTime");
            LocalDateTime departureTime = (LocalDateTime) ticket.get("departureTime");
//            String arrivalTimeStr = (String) ticket.get("arrivalTime");
            LocalDateTime arrivalTime = (LocalDateTime) ticket.get("arrivalTime");
            LocalDate date = (LocalDate) ticket.get("date");

            ticketCollection.updateOne(Filters.eq("pnr", pnr), Updates.set("status", -1));

            TrainSeat seat = new TrainSeat(
                    trainNumber, trainName, startStation, endStation,
                    departureTime, arrivalTime, date, seatClass, coach, berth, seatNumber);

            updateWaitlist(seat);
            response = "Ticket cancelled successfully";
        }
        List<Object> list = new ArrayList<>();
        list.add(pnr);
        list.add(response);
        return list;
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

    static List<Object> upgradeTicket(int pnr, String seatClass, String coach, String berth) {
        Map<String, Object> ticket = getTicket(pnr);
        List<Object> list = new ArrayList<>();
        list.add(pnr);
        String response;
        if (ticket == null) {
            response = "No ticket is present with given PNR";
            list.add(response);
            return list;
        }
        LocalDate date = (LocalDate) ticket.get("date");
        ticket.put("date", date.toString());
        TrainSeat newSeat = seatAllocation(ticket, seatClass, coach, berth);
        if (newSeat == null) {
            response = "No seat is available for the given seat class and coach";
        }
        else{
            cancelTicket(pnr);
            Bson updates = Updates.combine(
                    Updates.set("seatClass", newSeat.seatClass),
                    Updates.set("coach", newSeat.coach),
                    Updates.set("berth", newSeat.berth),
                    Updates.set("seatNumber", newSeat.seatNumber),
                    Updates.set("status", 1)
            );
            ticketCollection.updateOne(Filters.eq("pnr", pnr), updates);
            bookSeat(newSeat);
            response = "Ticket upgraded successfully";
        }
        list.add(response);
        return list;
    }

    static List<Object> rescheduleTicket(int pnr, LocalDate date, String seatClass, String coach, String berth) {
        Map<String, Object> ticket = getTicket(pnr);
        List<Object> list = new ArrayList<>();
        list.add(pnr);
        String response;
        if (ticket == null) {
            response = "No ticket is present with given PNR";
            list.add(response);
            return list;
        }

        Bson filter = Filters.and(
                Filters.eq("date", date.toString()),
                Filters.eq("trainNumber", ticket.get("trainNumber"))
        );
        Document train = trainCollection.find(filter).first();

        if (train == null) {
            response = "No train is running on given date";
        }
        else {
            ticket.put("date", date.toString());
            TrainSeat newSeat = seatAllocation(ticket, seatClass, coach, berth);
            if (newSeat == null) {
                response = "No seat is available for the given Date and Seat Class";
            } else {
                cancelTicket(pnr);
                List<Map<String, Object>> tr = getTrains(newSeat.startStation, newSeat.endStation, date);
                Object d = tr.getFirst().get("departureTime");
                Object a = tr.getFirst().get("arrivalTime");
                Bson updates = Updates.combine(
                        Updates.set("date", date),
                        Updates.set("departureTime", d),
                        Updates.set("arrivalTime", a),
                        Updates.set("seatClass", newSeat.seatClass),
                        Updates.set("coach", newSeat.coach),
                        Updates.set("berth", newSeat.berth),
                        Updates.set("seatNumber", newSeat.seatNumber),
                        Updates.set("status", 1)
                );
                ticketCollection.updateOne(Filters.eq("pnr", pnr), updates);
                bookSeat(newSeat);
                response = "Ticket rescheduled successfully";
            }
        }
        list.add(response);
        return list;
    }

    static List<Object> bookTicket(String startStation, String endStation, LocalDate date, int trainNumber,
                                   LocalDateTime departureTime, LocalDateTime arrivalTime, String trainName,
                                   String seatClass, String name, int age, String gender, String coach, String berth){
        String response;
        Map<String, Object> temp = new HashMap<>();
        temp.put("startStation", startStation);
        temp.put("endStation", endStation);
        temp.put("date", date.toString());
        temp.put("trainNumber", trainNumber);
        temp.put("name", name);
        temp.put("age", age);
        temp.put("gender", gender);
        temp.put("seatClass", seatClass);

        TrainSeat seat = seatAllocation(temp, seatClass, coach, berth);
        int pnr = Math.abs(UUID.randomUUID().hashCode());
        Ticket ticket;
        if (seat == null) {
            ticket = new Ticket(trainNumber, trainName, startStation, endStation, departureTime, arrivalTime,
                    date, seatClass, "", "", 0, name, age, gender, LocalDateTime.now(), pnr, 0 );
            response = "Your Ticket is Waitlisted";
        }
        else{
            ticket = new Ticket(trainNumber, trainName, startStation, endStation, departureTime, arrivalTime,
                    date, seatClass, seat.coach, seat.berth, seat.seatNumber, name, age, gender, LocalDateTime.now(), pnr, 1);
            bookSeat(seat);
            response = "Your Ticket is Confirmed";
        }
        ticket.addTicket();
        List<Object> list = new ArrayList<>();
        list.add(pnr);
        list.add(response);
        return list;
    }
}