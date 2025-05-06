package com.trainbooking.trainticketmanagement;

import java.time.LocalDateTime;

class Ticket extends TrainSeat {
    String name;
    int age;
    char gender;
    LocalDateTime bookTime;
    int pnr;

    Ticket(int trainNumber, String trainName, String startStation, String endStation,
                  LocalDateTime departureTime, LocalDateTime arrivalTime,
                  String seatClass, String coach, String berth, int seatNumber,
                  String name, int age, char gender, LocalDateTime bookTime, int pnr) {

        super(trainNumber, trainName, startStation, endStation,
                departureTime, arrivalTime, seatClass, coach, berth, seatNumber);

        this.name = name;
        this.age = age;
        this.gender = gender;
        this.bookTime = bookTime;
        this.pnr = pnr;
    }
}
