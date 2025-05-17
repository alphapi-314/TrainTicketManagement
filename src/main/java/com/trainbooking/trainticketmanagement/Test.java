package com.trainbooking.trainticketmanagement;

import org.bson.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

import java.util.*;


public class Test implements DbConnection {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);


        String start,end;
        LocalDate date;
        date = LocalDate.parse("2025-05-08");
        System.out.println("Enter start and end ");
        start = sc.nextLine();
        end = sc.nextLine();
        List<Map<String, Object>> trains = UserFunctions.showTrains(start, end, date);
        System.out.println(trains);

        String user = "ShivrajSinghNegi";
        String pass = "12345678";

        boolean data = UserFunctions.login(user, pass);
        System.out.println(data);

        int pnr = 34523;
        Map<String,Object> d = UserFunctions.showTicket(34523);
        System.out.println(d);

    }

    void ab(){
        LocalDateTime bookTime = LocalDateTime.now();
        LocalDateTime dep = LocalDateTime.parse("2025-05-06T23:30:00");
        LocalDateTime arr = LocalDateTime.parse("2025-05-07T04:07:00");
        LocalDate date = LocalDate.parse("2025-05-08");

        Ticket t = new Ticket(14120, "DDN KGM EXP", "DEHRADUN", "MORADABAD",
                dep,arr, date, "3AC", "B2", "Upper", 12,
                "Gaurav", 20, "male", bookTime, 34523, 1 );
        t.addTicket();
        System.out.println("Ticket add success");
    }

}
