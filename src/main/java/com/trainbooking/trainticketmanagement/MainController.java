package com.trainbooking.trainticketmanagement;

import java.time.LocalDate;
import java.util.*;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@SessionAttributes("ticket")
public class MainController {

    @GetMapping({"/", "/home"})
    public String homePage() {
        return "home";
    }

    @GetMapping("/book")
    public String bookTicketPage() {
        return "book";
    }

    @GetMapping("/show")
    public String pnrTicketPage() {
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

    @PostMapping("/showTicket")
    public String handlePNRSubmission(@RequestParam Integer user_pnr, Model model) {
        Map<String, Object> ticketDetails = UserFunctions.showTicket(user_pnr);
        model.addAttribute("ticket", ticketDetails);
        return "redirect:/ticket-show";
    }

    @GetMapping("/ticket-show")
    public String showTicketInfo(Model model) {
        return "ticket-show";
    }
}
