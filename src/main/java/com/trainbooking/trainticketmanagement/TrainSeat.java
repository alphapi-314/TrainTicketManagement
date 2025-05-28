package com.trainbooking.trainticketmanagement;

import com.mongodb.client.model.*;
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

    Document toDocument() {
        return super.toDocument()
                .append("seatClass", seatClass)
                .append("coach", coach)
                .append("berth", berth)
                .append("seatNumber", seatNumber);
    }
    
protected static void bookSeat(TrainSeat seat){
    // convert the LocalDate into the same String format you stored in Mongo
    String dateStr = seat.date.toString();

    Bson filter = Filters.and(
        Filters.eq("trainNumber", seat.trainNumber),
        Filters.eq("date", dateStr),
        Filters.eq("seatClass", seat.seatClass),
        Filters.eq("coach", seat.coach),
        Filters.eq("seatNumber", seat.seatNumber)
    );

    Document doc = seatCollection.find(filter).first();
    if (doc != null) {
        @SuppressWarnings("unchecked")
        List<Document> bookings = (List<Document>) doc.get("bookings");
        boolean flag = false;
        for (Document booking : bookings) {
            String from = booking.getString("from");
            String to   = booking.getString("to");

            if (from.equalsIgnoreCase(seat.startStation)) flag = true;
            if (flag) booking.put("status", 1);
            if (to.equalsIgnoreCase(seat.endStation)) break;
        }

        Bson update = Updates.set("bookings", bookings);
        seatCollection.updateOne(filter, update);
        System.out.println(bookings);
        System.out.println("Booked seat: " + seat.coach + "-" + seat.berth + "-" + seat.seatNumber);
    }
}

protected static void cancelSeat(TrainSeat seat) {
    String dateStr = seat.date.toString();

    Bson filter = Filters.and(
        Filters.eq("trainNumber", seat.trainNumber),
        Filters.eq("date", dateStr),
        Filters.eq("seatClass", seat.seatClass),
        Filters.eq("coach", seat.coach),
        Filters.eq("seatNumber", seat.seatNumber)
    );

    Document doc = seatCollection.find(filter).first();
    if (doc != null) {
        @SuppressWarnings("unchecked")
        List<Document> bookings = (List<Document>) doc.get("bookings");
        boolean flag = false;
        for (Document booking : bookings) {
            String from = booking.getString("from");
            String to   = booking.getString("to");

            if (from.equalsIgnoreCase(seat.startStation)) flag = true;
            if (flag) booking.put("status", 0);
            if (to.equalsIgnoreCase(seat.endStation)) break;
        }

        Bson update = Updates.set("bookings", bookings);
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
    String startStation = (String) ticket.get("startStation");
    String endStation = (String) ticket.get("endStation");
    LocalDateTime departureTime = (LocalDateTime) ticket.get("departureTime");
    LocalDateTime arrivalTime = (LocalDateTime) ticket.get("arrivalTime");
    String dateStr = (String) ticket.get("date");
    LocalDate date = LocalDate.parse(dateStr);
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
    String date = (String) details.get("date");

    System.out.println(startStation + " " + endStation + " " + date + " " + trainNumber + " " + seatClass);

    List<Document> seats = seatCollection.find(
            Filters.and(
                    Filters.eq("trainNumber", trainNumber),
                    Filters.eq("seatClass", seatClass),
                    Filters.eq("date", date)
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