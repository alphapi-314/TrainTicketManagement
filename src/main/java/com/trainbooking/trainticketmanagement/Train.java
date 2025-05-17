package com.trainbooking.trainticketmanagement;

import java.time.*;
import java.util.*;
import com.mongodb.client.model.*;
import org.bson.Document;
import org.bson.conversions.Bson;

class Train implements DbConnection {
    int trainNumber;
    String trainName;
    String startStation;
    String endStation;
    LocalDateTime departureTime;
    LocalDateTime arrivalTime;
    LocalDate date;

    Train(int trainNo, String name, String start, String end, LocalDateTime departure, LocalDateTime arrival, LocalDate date) {
        this.trainNumber = trainNo;
        this.trainName = name;
        this.startStation = start;
        this.endStation = end;
        this.departureTime = departure;
        this.arrivalTime = arrival;
    }

    Document toDocument() {
        return new Document("trainNumber", trainNumber)
                .append("trainName", trainName)
                .append("startStation", startStation)
                .append("endStation", endStation)
                .append("departureTime", departureTime)
                .append("arrivalTime", arrivalTime)
                .append("date", date);
    }

    private static List<Document> fetchTrains(String startStation, String endStation, LocalDate date) {
        Bson matchDate = Filters.eq("date", date);

        Bson projection = new Document()
                .append("trainNumber", 1)
                .append("trainName", 1)
                .append("date", 1)
                .append("seatClasses", 1)
                .append("startIndex", new Document("$indexOfArray", Arrays.asList("$route.stationName", startStation)))
                .append("endIndex", new Document("$indexOfArray", Arrays.asList("$route.stationName", endStation)))
                .append("startStationData", new Document("$arrayElemAt", Arrays.asList(
                        "$route", new Document("$indexOfArray", Arrays.asList("$route.stationName", startStation))
                )))
                .append("endStationData", new Document("$arrayElemAt", Arrays.asList(
                        "$route", new Document("$indexOfArray", Arrays.asList("$route.stationName", endStation))
                )));

        Bson matchStationsInOrder = new Document("$expr", new Document("$and", Arrays.asList(
                new Document("$ne", Arrays.asList("$startIndex", -1)),
                new Document("$ne", Arrays.asList("$endIndex", -1)),
                new Document("$lt", Arrays.asList("$startIndex", "$endIndex"))
        )));

        List<Bson> pipeline = Arrays.asList(
                Aggregates.match(matchDate),
                Aggregates.project(projection),
                Aggregates.match(matchStationsInOrder)
        );

        return trainCollection.aggregate(pipeline).into(new ArrayList<>());
    }

    static List<Map<String, Object>> getTrains(String startStation, String endStation, LocalDate date) {
        List<Document> trains = fetchTrains(startStation, endStation, date);
        List<Map<String, Object>> result = new ArrayList<>();

        for (Document doc : trains) {
            Document start = doc.get("startStationData", Document.class);
            Document end = doc.get("endStationData", Document.class);

            if (start != null && end != null) {
                Map<String, Object> trainMap = new LinkedHashMap<>();
                trainMap.put("trainNumber", doc.getInteger("trainNumber"));
                trainMap.put("trainName", doc.getString("trainName"));
                trainMap.put("date", doc.get("date"));
                trainMap.put("seatClasses", doc.get("seatClasses"));
                trainMap.put("startStation", start.getString("stationName"));
                trainMap.put("departureTime", start.getDate("departureTime"));
                trainMap.put("endStation", end.getString("stationName"));
                trainMap.put("arrivalTime", end.getDate("arrivalTime"));

                result.add(trainMap);
            }
        }
        return result;
    }
}
