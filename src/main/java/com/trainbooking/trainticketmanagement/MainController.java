package com.trainbooking.trainticketmanagement;


import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class MainController {
    @GetMapping({"/" , "/home"})
    public String homePage() {
        return "home";
    }
    @GetMapping("/book")
    public String bookTicketPage() {
        return "book";
    }
    @GetMapping("/show")
    public String showTicketPage() {
        return "show";
    }  
    @GetMapping("/reschedule")
    public String rescheduleTicketPage() {
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
    @PostMapping("/book")
    public String handleBooking(@RequestParam String user_source,
                                @RequestParam String user_destination,
                                @RequestParam String user_dot) {
        return "redirect:/ticket-info";
    }
    @GetMapping("/ticket-info")
    public String showTicketInfo() {
        return "ticket-info";  // Make sure there's a ticket-info.html in the templates folder
    }     
}
