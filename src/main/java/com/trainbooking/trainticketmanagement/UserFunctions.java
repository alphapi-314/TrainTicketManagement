package com.trainbooking.trainticketmanagement;

import java.time.*;
import java.util.*;

class UserFunctions {

    private UserFunctions() {}

    static Map<String,Object> showTicket(int pnr){
        return Ticket.getTicket(pnr);
    }

    static List<Map<String,Object>> showTrains(String startStation, String endStation, LocalDate date){
        return Train.getTrains(startStation, endStation, date);
    }

    static boolean login(String userName, String password){
        return User.getUser(userName, password);
    }

    static void registerUser(String userName, String emailID, String password){
        User user = new User(userName, emailID, password);
        user.addUser();
    }

    static List<Object> cancelTicket(int pnr){
        return Ticket.cancelTicket(pnr);
    }

    static List<Object> bookTicket(String startStation, String endStation, LocalDate date, int trainNumber,
                                   LocalDateTime departureTime, LocalDateTime arrivalTime, String trainName,
                                   String seatClass, String name, int age, String gender, String coach, String berth){
        return Ticket.bookTicket(startStation, endStation, date, trainNumber, departureTime, arrivalTime,
                trainName, seatClass, name, age, gender, coach, berth);
    }

    static List<Object> rescheduleTicket(int pnr, LocalDate date, String seatClass, String coach, String berth){
        return Ticket.rescheduleTicket(pnr,date,seatClass,coach,berth);
    }

    static List<Object> upgradeTicket(int pnr, String seatClass, String coach, String berth) {
        return Ticket.upgradeTicket(pnr,seatClass,coach,berth);
    }
}
