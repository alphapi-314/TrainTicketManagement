package com.trainbooking.trainticketmanagement;
import java.util.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;



@Controller
public class MainController {
    @GetMapping({"/" , "/home"})
    public String homePage() {
        return "home";
    }

    @GetMapping("/book")
    public String bookTicketPage(Model model) {
        // Pass today's date to the Thymeleaf template
        model.addAttribute("today", LocalDate.now());
        return "book";
    }

    @GetMapping("/show")
    public String pnrTicketPage() {
        return "show";
    }

    @GetMapping("/reschedule")
    public String rescheduleTicketPage(Model model) {
        model.addAttribute("today", LocalDate.now());
        return "res";
    }

    @GetMapping("/upgrade")
    public String upgradeTicketPage() {
        return "upgrade";
    }

    @GetMapping("/cancel")
    public String cancelTicketPage() {
        return "cancel";
    }

    @PostMapping("/class-select")
    public String bookTicket(@RequestParam("user_source") String source,
                             @RequestParam("user_destination") String destination,
                             @RequestParam("user_dot") String dot,
                             Model model) {
        model.addAttribute("source", source);
        model.addAttribute("destination", destination);
        model.addAttribute("dot", dot);
        return "train-book";
    }

    @PostMapping("/user-details")
    public String selectClass(@RequestParam("class") String trainClass, Model model) {
        model.addAttribute("trainClass", trainClass);
        return "input-details";  // Redirect to the seat layout page
    }

    @PostMapping("/submit-show")
    public String handlePNRSubmission(@RequestParam Integer user_pnr, Model model) {
        Map<String, Object> ticketDetails = UserFunctions.showTicket(user_pnr);
        model.addAttribute("ticket", ticketDetails);
        return "ticket-show";
    }
}