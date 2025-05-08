package com.trainbooking.trainticketmanagement;

import org.bson.Document;
import java.time.*;
import java.util.*;

public class UserFunctions extends Server {

    Document show(int pnr){
        return findTicket(pnr);
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

    boolean login(String userName, String password){
        Document user = loginAuthentication(userName, password);
        return user != null;
    }
}
