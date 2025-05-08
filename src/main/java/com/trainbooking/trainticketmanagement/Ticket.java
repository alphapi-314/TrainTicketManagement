package com.trainbooking.trainticketmanagement;

import org.bson.Document;

import java.time.LocalDateTime;

class Ticket extends TrainSeat {
    String name;
    int age;
    char gender;
    LocalDateTime bookTime;
    int pnr;
    int status;

    Ticket(int trainNumber, String trainName, String startStation, String endStation,
                  LocalDateTime departureTime, LocalDateTime arrivalTime,
                  String seatClass, String coach, String berth, int seatNumber,
                  String name, int age, char gender, LocalDateTime bookTime, int pnr, int status) {

        super(trainNumber, trainName, startStation, endStation,
                departureTime, arrivalTime, seatClass, coach, berth, seatNumber);

        this.name = name;
        this.age = age;
        this.gender = gender;
        this.bookTime = bookTime;
        this.pnr = pnr;
        this.status = status;
    }

    Ticket(Ticket ticket) {
        super(ticket.trainNumber, ticket.trainName, ticket.startStation, ticket.endStation, ticket.departureTime,
                ticket.arrivalTime, ticket.seatClass, ticket.coach, ticket.berth, ticket.seatNumber);
        this.name = ticket.name;
        this.age = ticket.age;
        this.gender = ticket.gender;
        this.bookTime = ticket.bookTime;
        this.pnr = ticket.pnr;
        this.status = ticket.status;
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




}
