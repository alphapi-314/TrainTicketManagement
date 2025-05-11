package com.trainbooking.trainticketmanagement;

import com.mongodb.client.MongoClient;
import org.bson.Document;
import java.time.*;
import java.util.*;

class UserFunctions extends Server {

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

    void cancel(int pnr){
        boolean flag = cancelTicket(pnr);
        if (flag) {
            System.out.println("Your ticket got cancelled");
        }
        else{
            System.out.println("No ticket got cancelled");
        }
    }

    void book(){

    }

    void reschedule(int pnr, LocalDate date){
        Document ticket = findTicket(pnr);
        if (ticket == null){
            System.out.println("No ticket found");
            return;
        }

    }

    void upgrade(int pnr, String seatClass, String coach, String berth) {
        Document ticket = findTicket(pnr);
        if (ticket == null){
            System.out.println("No ticket found");
            return;
        }

    }

}
