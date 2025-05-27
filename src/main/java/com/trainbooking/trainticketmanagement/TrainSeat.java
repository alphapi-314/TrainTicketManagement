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

protected static TrainSeat seatAllocation(Map<String, Object> ticket, String seatClass, String coachPref, String berthPref) {
    List<Document> free = freeSeats(ticket, seatClass);
    if (free.isEmpty()) return null;

    int age = ticket.get("age") != null ? (int) ticket.get("age") : 30;
    String gender = ticket.get("gender") != null ? ticket.get("gender").toString().toLowerCase() : "";

    // Define berth preference order
    List<String> preferredBerths;
    if (age > 60 || age < 10 || gender.equals("female")) {
        // Senior citizen, child, or female
        preferredBerths = Arrays.asList("Lower", "Side Lower", "Middle", "Upper", "Side Upper");
    } else {
        // General preference (can customise further)
        preferredBerths = Arrays.asList("Lower", "Middle", "Upper", "Side Lower", "Side Upper");
    }

    // Group seats by compartment (by coach + compartment number)
    Map<String, List<Document>> compartmentMap = new LinkedHashMap<>();
    for (Document seat : free) {
        String coach = seat.getString("coach");
        int seatNum = seat.getInteger("seatNumber");
        int compartmentSize = 8; // Change as per actual config/class
        int compNum = (seatNum - 1) / compartmentSize; // compartment numbers are 0-based
        String compartmentKey = coach + "-C" + compNum;
        compartmentMap.computeIfAbsent(compartmentKey, k -> new ArrayList<>()).add(seat);
    }

    // Sort compartments: nearly full compartments first
    List<Map.Entry<String, List<Document>>> sortedCompartments = new ArrayList<>(compartmentMap.entrySet());
    sortedCompartments.sort((a, b) -> {
        int aEmpty = a.getValue().size();
        int bEmpty = b.getValue().size();
        // Prefer compartments with fewer free seats (closer to full)
        return Integer.compare(aEmpty, bEmpty);
    });

    Document bestSeat = null;
    int bestScore = -100;
    for (Map.Entry<String, List<Document>> compartment : sortedCompartments) {
        List<Document> seats = compartment.getValue();
        // Try allocating according to preferred berths
        for (String desiredBerth : preferredBerths) {
            for (Document seatDoc : seats) {
                String thisCoach = seatDoc.getString("coach");
                String thisBerth = seatDoc.getString("berth");
                int score = 0;
                if (coachPref != null && !coachPref.isEmpty() && coachPref.equalsIgnoreCase(thisCoach)) score += 2;
                if (berthPref != null && !berthPref.isEmpty() && berthPref.equalsIgnoreCase(thisBerth)) score += 1;
                if (thisBerth.equalsIgnoreCase(desiredBerth)) score += 5;
                // Lower seat number in compartment gets tie
                if ((score > bestScore) || (score == bestScore && bestSeat != null && seatDoc.getInteger("seatNumber") < bestSeat.getInteger("seatNumber"))) {
                    bestScore = score;
                    bestSeat = seatDoc;
                }
            }
            if (bestSeat != null) break; // Found in this berth level
        }
        if (bestSeat != null) break; // Best seat found in this compartment
    }

    if (bestSeat == null) return null;

    // Populate TrainSeat as in your prior code
    int trainNumber = bestSeat.getInteger("trainNumber");
    String trainName = bestSeat.getString("trainName");
    String startStation = (String) ticket.get("from");
    String endStation = (String) ticket.get("to");
    LocalDateTime departureTime = (LocalDateTime) ticket.get("departureTime");
    LocalDateTime arrivalTime = (LocalDateTime) ticket.get("arrivalTime");
    LocalDate date;
    Object dateObj = bestSeat.get("date");
    if (dateObj instanceof LocalDate) {
        date = (LocalDate) dateObj;
    } else if (dateObj instanceof String) {
        date = LocalDate.parse((String) dateObj);
    } else {
        date = LocalDate.now();
    }
    String finalCoach = bestSeat.getString("coach");
    String finalBerth = bestSeat.getString("berth");
    int seatNumber = bestSeat.getInteger("seatNumber");

    return new TrainSeat(trainNumber, trainName, startStation, endStation,
            departureTime, arrivalTime, date, seatClass, finalCoach, finalBerth, seatNumber);
}

private static List<Document> freeSeats(Map<String, Object> details, String seatClass) {
    List<Document> freeSeats = new ArrayList<>();

    int trainNumber = (Integer) details.get("trainNumber");
    String startStation = (String) details.get("startStation");
    String endStation = (String) details.get("endStation");
    LocalDate date = (LocalDate) details.get("date");

    String dateStr = date.toString();
    System.out.println(dateStr);
    System.out.println(date);

    List<Document> seats = seatCollection.find(
            Filters.and(
                    Filters.eq("trainNumber", trainNumber),
                    Filters.eq("seatClass", seatClass),
                    Filters.eq("date", dateStr)
            )
    ).into(new ArrayList<>());

    System.out.println(seats.size());

    for (Document seat : seats) {
        @SuppressWarnings("unchecked")
        List<Document> bookings = (List<Document>) seat.get("bookings");
        boolean available = true;
        boolean inSegment = false;

        for (Document booking : bookings) {
            String from = booking.getString("from");
            String to = booking.getString("to");
            int status = booking.getInteger("status", 0);

            if (!inSegment && from.equals(startStation)) {
                inSegment = true;
            }
            if (inSegment && status == 1) {
                available = false;
            }
            if (inSegment && to.equals(endStation)) {
                break;
            }
        }
        if (available) {
            freeSeats.add(seat);
        }
    }
    System.out.println(freeSeats.size());
    return freeSeats;
}

}