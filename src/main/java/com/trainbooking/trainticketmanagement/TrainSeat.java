package com.trainbooking.trainticketmanagement;

import java.time.LocalDateTime;

class TrainSeat extends Train {
    String seatClass;
    String coach;
    String berth;
    int seatNumber;

    public TrainSeat(int trainNumber, String trainName, String startStation, String endStation,
                     LocalDateTime departureTime, LocalDateTime arrivalTime,
                     String seatClass, String coach, String berth, int seatNumber) {
        super(trainNumber, trainName, startStation, endStation, departureTime, arrivalTime);
        this.seatClass = seatClass;
        this.coach = coach;
        this.berth = berth;
        this.seatNumber = seatNumber;
    }

}
