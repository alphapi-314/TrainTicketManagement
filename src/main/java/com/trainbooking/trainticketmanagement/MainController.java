package com.trainbooking.trainticketmanagement;
import org.bson.Document;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.stereotype.Controller;


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

    User user=new User();
    @PostMapping("/show")
    public String handlePNRSubmission(@RequestParam Integer user_pnr, Model model) {  /* according to name int HTML file */
        Document ticketDetails = user.show(user_pnr);
        model.addAttribute("ticket", ticketDetails);
        return "redirect:/ticket-show";
    }
    @GetMapping("/ticket-show")
    public String showTicketInfo() {
        return "ticket-show";
    }
}