package com.trainbooking.trainticketmanagement;

import java.time.LocalDateTime;

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


}
