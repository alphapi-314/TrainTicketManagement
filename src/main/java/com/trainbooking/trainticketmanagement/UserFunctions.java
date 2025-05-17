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

    String cancelTicket(int pnr){
        return Ticket.cancelTicket(pnr);
    }

    void book(){

    }

    void reschedule(int pnr, LocalDate date){
        Map<String,Object> ticket = Ticket.getTicket(pnr);
    }

    void upgrade(int pnr, String seatClass, String coach, String berth) {
        Map<String,Object> ticket = Ticket.getTicket(pnr);
        if (ticket == null){
            System.out.println("No ticket found");
            return;
        }

    }

}
