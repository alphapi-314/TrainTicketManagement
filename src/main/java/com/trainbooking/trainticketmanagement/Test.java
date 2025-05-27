package com.trainbooking.trainticketmanagement;

import org.bson.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

import java.util.*;


public class Test implements DbConnection {
    public static void main(String[] args) {
        test();
    }

    void c(){
        Scanner sc = new Scanner(System.in);

        String start,end;
        LocalDate date;
        date = LocalDate.parse("2025-05-28");
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
    
    static void addSeats() {
        int trainNumber = 14120;
        String trainName = "DDN KGM EXP";
        String date = "2025-05-29";
        List<String> stations = Arrays.asList(
            "DEHRADUN", "HARIDWAR", "NAJIBABAD",
            "MORADABAD", "RAMPUR", "RUDRAPUR", "HALDWANI", "KATHGODAM");
        List<String[]> stationSegments = new ArrayList<>();
        for (int i = 0; i < stations.size() - 1; i++) {
            stationSegments.add(new String[]{stations.get(i), stations.get(i + 1)});
        }

        class Config {
            String seatClass;
            String[] coaches;
            int compartmentSize;
            int seatsPerCoach;
            Config(String seatClass, String[] coaches, int compartmentSize, int seatsPerCoach) {
                this.seatClass = seatClass;
                this.coaches = coaches;
                this.compartmentSize = compartmentSize;
                this.seatsPerCoach = seatsPerCoach;
            }
        }

        Config[] configs = {
            new Config("1AC", new String[]{"HA1"}, 4, 12),                 // 3 compartments per coach
            new Config("2AC", new String[]{"A1", "A2"}, 6, 18),            // 3 compartments per coach
            new Config("3AC", new String[]{"B1", "B2", "B3"}, 8, 24),      // 3 compartments per coach
            new Config("Sleeper", new String[]{"D1", "D2", "D3", "D4"}, 8, 24) // 3 compartments per coach
        };

        Random rand = new Random();

        for (Config config : configs) {
            for (String coach : config.coaches) {
                for (int s = 0; s < config.seatsPerCoach; s++) {
                    int seatNumber = s + 1;
                    int inComp = s % config.compartmentSize;
                    String berth;

                    if (config.seatClass.equals("1AC")) {
                        if (inComp == 0 || inComp == 2) berth = "Lower";
                        else berth = "Upper";
                    } else if (config.seatClass.equals("2AC")) {
                        if (inComp == 0 || inComp == 2) berth = "Lower";
                        else if (inComp == 1 || inComp == 3) berth = "Upper";
                        else if (inComp == 4) berth = "Side Lower";
                        else berth = "Side Upper";
                    } else {
                        if (inComp == 0 || inComp == 3) berth = "Lower";
                        else if (inComp == 1 || inComp == 4) berth = "Middle";
                        else if (inComp == 2 || inComp == 5) berth = "Upper";
                        else if (inComp == 6) berth = "Side Lower";
                        else berth = "Side Upper";
                    }

                    int type = rand.nextInt(3);
                    List<Document> bookings = new ArrayList<>();
                    if (type == 2) {
                        for (String[] seg : stationSegments)
                            bookings.add(new Document("from", seg[0]).append("to", seg[1]).append("status", 1));
                    } else if (type == 1) {
                        boolean booked = rand.nextBoolean();
                        for (String[] seg : stationSegments) {
                            bookings.add(new Document("from", seg[0]).append("to", seg[1]).append("status", booked ? 1 : 0));
                            if (rand.nextInt(3) == 0) booked = !booked;
                        }
                    } else {
                        for (String[] seg : stationSegments)
                            bookings.add(new Document("from", seg[0]).append("to", seg[1]).append("status", 0));
                    }

                    Document doc = new Document("trainNumber", trainNumber)
                        .append("trainName", trainName)
                        .append("date", date)
                        .append("seatClass", config.seatClass)
                        .append("coach", coach)
                        .append("berth", berth)
                        .append("seatNumber", seatNumber)
                        .append("bookings", bookings);

                    seatCollection.insertOne(doc);
                }
            }
        }
        System.out.println("Seats inserted following specified compartment-wise berth layout.");
    }

// Add this method in your Test.java class
public static void test() {
    String startStation = "DEHRADUN";
    String endStation = "RAMPUR";
    LocalDate date = LocalDate.parse("2025-05-29");
    int trainNumber = 14120;
    LocalDateTime departureTime = LocalDateTime.parse("2025-05-29T22:00:00");
    LocalDateTime arrivalTime = LocalDateTime.parse("2025-05-30T03:15:00");
    String trainName = "DDN KGM EXP";
    String seatClass = "3AC";
    String name = "TestUser";
    int age = 30;
    String gender = "male";
    String coach = "B2";
    String berth = "Upper";

    // Call the method to book ticket
    List<Object> bookingResult = UserFunctions.bookTicket(
        startStation,
        endStation,
        date,
        trainNumber,
        departureTime,
        arrivalTime,
        trainName,
        seatClass,
        name,
        age,
        gender,
        coach,
        berth
    );

    // Print the result for verification
    System.out.println("Booking Result: " + bookingResult);
}
}