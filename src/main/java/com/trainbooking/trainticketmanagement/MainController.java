package com.trainbooking.trainticketmanagement;
import java.time.LocalDateTime;
import java.util.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;



@Controller
public class MainController {
    @GetMapping("/")
    public String redirectToLogin() {
        return "redirect:/login";
    }

    @GetMapping( "/login")
    public String loginPage(Model model) {
        model.addAttribute("errorMessage", null);
        return "login";
    }

    @PostMapping("/login")
    public String loginPage(@RequestParam String username, @RequestParam String password, Model model) {
        if (UserFunctions.login(username, password)) {
            return "home";
        }
        else {
            model.addAttribute("errorMessage", "Invalid username or password. " + "Please register if you are new.");  // Add error flag for failed login
            model.addAttribute("showRegister", true);
            return "login";
        }
    }

    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }

    @PostMapping("/register")
    public String registerPage(@RequestParam String username, @RequestParam String email, @RequestParam String password) {
        UserFunctions.registerUser(username, email, password);
        return "redirect:/login";
    }

    @GetMapping("/cancel")
    public String cancelTicketPage() {
        return "cancel";
    }

    @PostMapping("/cancel")
    public String cancelTicketPage(@RequestParam Integer pnr, Model model) {
        Map<String, Object> ticketDetails = UserFunctions.showTicket(pnr);
        if (ticketDetails == null) {
            model.addAttribute("invalidPNR", true);
            return "cancel";
        }
        model.addAttribute("found", true);
        model.addAttribute("ticket", ticketDetails);
        return "cancel-ticket";
    }

    @GetMapping("/upgrade")
    public String upgradeTicketPage() {
        return "upgrade";
    }

    @PostMapping("/upgrade")
    public String upgradeTicketPage(@RequestParam Integer pnr, Model model) {
        Map<String, Object> ticketDetails = UserFunctions.showTicket(pnr);
        if (ticketDetails == null) {
            model.addAttribute("invalidPNR", true);
            return "upgrade";
        }
        model.addAttribute("found", true);
        model.addAttribute("ticket", ticketDetails);
        return "upgrade-ticket";
    }

    @PostMapping("ticket-upgrade")
    public String upgradeTicket(@RequestParam int pnr,
                                @RequestParam int trainNumber,
                                @RequestParam String trainName,
                                @RequestParam String startStation,
                                @RequestParam String endStation,
                                @RequestParam String departureTime,
                                @RequestParam String arrivalTime,
                                Model model) {
        model.addAttribute("pnr", pnr);
        model.addAttribute("trainNumber", trainNumber);
        model.addAttribute("trainName", trainName);
        model.addAttribute("source", startStation);
        model.addAttribute("destination", endStation);
        model.addAttribute("departure", departureTime);
        model.addAttribute("arrival", arrivalTime);
        return "ticket-upgrade-class";
    }

    @PostMapping("/upgrade-input-details")
    public String upgradeInputDetails(@RequestParam int pnr,
                                      @RequestParam int trainNumber,
                                      @RequestParam String trainName,
                                      @RequestParam String source,
                                      @RequestParam String destination,
                                      @RequestParam String departure,
                                      @RequestParam String arrival,
                                      @RequestParam String seat_class,
                                      Model model) {
        model.addAttribute("pnr", pnr);
        model.addAttribute("seatClass", seat_class);
        model.addAttribute("source", source);
        model.addAttribute("destination", destination);
        model.addAttribute("trainNumber", trainNumber);
        model.addAttribute("trainName", trainName);
        model.addAttribute("departure", departure);
        model.addAttribute("arrival", arrival);
        return "upgrade-input-details";
    }

    @PostMapping("/upgrade-final")
    public String upgradeFinal(@RequestParam int pnr,
                               @RequestParam int trainNumber,
                               @RequestParam String trainName,
                               @RequestParam String source,
                               @RequestParam String destination,
                               @RequestParam String departure,
                               @RequestParam String arrival,
                               @RequestParam String seat_class,
                               @RequestParam String coach_type,
                               @RequestParam String berth_type,
                               Model model) {
        model.addAttribute("pnr", pnr);
        model.addAttribute("seatClass", seat_class);
        model.addAttribute("source", source);
        model.addAttribute("destination", destination);
        model.addAttribute("trainNumber", trainNumber);
        model.addAttribute("trainName", trainName);
        model.addAttribute("departure", departure);
        model.addAttribute("arrival", arrival);

        List<Object> result = UserFunctions.upgradeTicket(pnr, seat_class, coach_type, berth_type);
        String responseMessage = (String) result.get(1);
        model.addAttribute("message", responseMessage);

        Map<String, Object> ticketDetails = UserFunctions.showTicket(pnr);
        model.addAttribute("found", true);
        model.addAttribute("ticket", ticketDetails);
        return "upgraded-ticket-show";
    }

    @GetMapping("/reschedule")
    public String rescheduleTicketPage(Model model) {
        return "reschedule";
    }

    @PostMapping("/reschedule")
    public String rescheduleTicketPage(@RequestParam Integer pnr,Model model) {
        Map<String, Object> ticketDetails = UserFunctions.showTicket(pnr);
        if (ticketDetails == null) {
            model.addAttribute("invalidPNR", true);
            return "reschedule";
        }
        model.addAttribute("found", true);
        model.addAttribute("ticket", ticketDetails);
        return "reschedule-ticket";
    }

    @PostMapping("/ticket-reschedule")
    public String rescheduleTicket(@RequestParam int pnr,Model model) {
        model.addAttribute("pnr", pnr);
        model.addAttribute("today", LocalDate.now());
        return "ticket-reschedule-date";
    }

    @PostMapping("/ticket-reschedule-berth")
    public String rescheduleTicket(@RequestParam int pnr,
                                   @RequestParam String dot,
                                   @RequestParam String seatClass,
                                   Model model) {
        model.addAttribute("pnr", pnr);
        model.addAttribute("dot", dot);
        model.addAttribute("seatClass", seatClass);
        return "ticket-reschedule-berth";
    }

    @PostMapping("/reschedule-final")
    public String rescheduleFinal(@RequestParam int pnr,
                                  @RequestParam String dot,
                                  @RequestParam String seatClass,
                                  @RequestParam String coach_type,
                                  @RequestParam String berth_type,
                                  Model model) {
        model.addAttribute("pnr", pnr);
        model.addAttribute("dot", dot);
        model.addAttribute("seatClass", seatClass);
        model.addAttribute("coach_type", coach_type);
        model.addAttribute("berth_type", berth_type);
        LocalDate travelDate = LocalDate.parse(dot);
        List<Object> result = UserFunctions.rescheduleTicket(pnr, travelDate, seatClass, coach_type, berth_type);
        String responseMessage = (String) result.get(1);
        model.addAttribute("message", responseMessage);

        Map<String, Object> ticketDetails = UserFunctions.showTicket(pnr);
        model.addAttribute("found", true);
        model.addAttribute("ticket", ticketDetails);
        return "reschedule-ticket-show";
    }

    @PostMapping("/ticket-cancel")
    public String cancelTicket(@RequestParam Integer pnr, Model model) {
        List<Object> cancelResult = UserFunctions.cancelTicket(pnr);
        String response = (String) cancelResult.get(1);
        model.addAttribute("message", response);

        Map<String, Object> ticketDetails = UserFunctions.showTicket(pnr);
        model.addAttribute("found", true);
        model.addAttribute("ticket", ticketDetails);
        return "cancel-ticket-show";
    }

    @GetMapping({"/home"})
    public String homePage() {
        return "home";
    }

    @GetMapping("/show")
    public String pnrTicketPage() {
        return "show";
    }

    @GetMapping("/book")
    public String bookTicketPage(Model model) {
        model.addAttribute("today", LocalDate.now());
        return "book";
    }

    @PostMapping("/class-select")
    public String bookTicket(@RequestParam String source, @RequestParam String destination, @RequestParam String dot, Model model) {
        String normalizedSource = source.toUpperCase();
        String normalizedDestination = destination.toUpperCase();
        model.addAttribute("source", normalizedSource);
        model.addAttribute("destination", normalizedDestination);
        model.addAttribute("dot", dot);

        LocalDate travelDate = LocalDate.parse(dot);

        List<Map<String, Object>> trainDoc = Train.getTrains(normalizedSource, normalizedDestination, travelDate);
        if (trainDoc.isEmpty()) {
            model.addAttribute("errorMessage", "No trains available for the selected route and date.");
            model.addAttribute("today", LocalDate.now());
            return "book";
        }

        Map<String, Object> selectedTrain = trainDoc.get(0);

        model.addAttribute("trainNumber", selectedTrain.get("trainNumber")); // or key name as in your Map
        model.addAttribute("trainName", selectedTrain.get("trainName"));
        model.addAttribute("departure", selectedTrain.get("departureTime"));
        model.addAttribute("arrival", selectedTrain.get("arrivalTime"));
        return "class-select";
    }

    @PostMapping("/user-details")
    public String selectClass(@RequestParam String seat_class,
                              @RequestParam String source,
                              @RequestParam String destination,
                              @RequestParam String dot,
                              @RequestParam int trainNumber,
                              @RequestParam String trainName,
                              @RequestParam String departure,
                              @RequestParam String arrival,
                              Model model) {
        model.addAttribute("seatClass", seat_class);
        model.addAttribute("source", source);
        model.addAttribute("destination", destination);
        model.addAttribute("dot", dot);
        model.addAttribute("trainNumber", trainNumber);
        model.addAttribute("trainName", trainName);
        model.addAttribute("departure", departure);
        model.addAttribute("arrival", arrival);
        return "input-details";
    }

    @PostMapping("/book-final")
    public String bookFinal(@RequestParam String user_name,
                            @RequestParam int user_age,
                            @RequestParam String user_gender,
                            @RequestParam String coach_type,
                            @RequestParam String berth_type,
                            @RequestParam String source,
                            @RequestParam String destination,
                            @RequestParam String dot,
                            @RequestParam String seat_class,
                            @RequestParam int trainNumber,
                            @RequestParam String trainName,
                            @RequestParam String departure,
                            @RequestParam String arrival,
                            Model model) {
        if (user_age <= 0 || user_age >= 100) {
            model.addAttribute("errorMessage", "Enter age between 1-99");
            model.addAttribute("source", source);
            model.addAttribute("destination", destination);
            model.addAttribute("dot", dot);
            model.addAttribute("seatClass", seat_class);
            return "input-details";
        }
        LocalDate travelDate = LocalDate.parse(dot);
        LocalDateTime departureTime = LocalDateTime.parse(departure);
        LocalDateTime arrivalTime = LocalDateTime.parse(arrival);
        List<Object> ticketInfo = UserFunctions.bookTicket(
                source,
                destination,
                travelDate,
                trainNumber,
                departureTime,
                arrivalTime,
                trainName,
                seat_class,
                user_name,
                user_age,
                user_gender,
                coach_type,
                berth_type
        );
        int pnr=(int)ticketInfo.get(0);
        String statusMessage=(String)ticketInfo.get(1);
        int statusCode;
        if (statusMessage.contains("Your Ticket is Confirmed")) {
            statusCode = 1;
        } else if (statusMessage.contains("Your Ticket is Waitlisted")) {
            statusCode = 0;
        } else {
            statusCode = -1;
        }
        Map<String, Object> ticketDetails = UserFunctions.showTicket(pnr);
        boolean found = ticketDetails != null;
        model.addAttribute("found", found);
        model.addAttribute("ticket", ticketDetails);
//        Map<String, Object> ticket = new HashMap<>();
//        ticket.put("pnr", pnr);
//        ticket.put("name", user_name);
//        ticket.put("age", user_age);
//        ticket.put("gender", user_gender);
//        ticket.put("bookTime", LocalDateTime.now().toString());
//        ticket.put("trainNumber", trainNumber);
//        ticket.put("trainName", trainName);
//        ticket.put("startStation", source);
//        ticket.put("endStation", destination);
//        ticket.put("departureTime", departure);
//        ticket.put("arrivalTime", arrival);
//        ticket.put("seatClass", seat_class);
//        ticket.put("coach", coach_type);
//        ticket.put("berth", berth_type);
//        ticket.put("seatNumber", "");  // set if you have seat number info
//        ticket.put("status", statusCode);
//        model.addAttribute("ticket", ticket);
//        model.addAttribute("found", true);
        return "train-book-complete";
    }

    @PostMapping("/submit-show")
    public String handlePNRSubmission(@RequestParam Integer user_pnr, Model model) {
        Map<String, Object> ticketDetails = UserFunctions.showTicket(user_pnr);
        boolean found = ticketDetails != null;
        model.addAttribute("found", found);
        model.addAttribute("ticket", ticketDetails);
        return "ticket-show";
    }
}