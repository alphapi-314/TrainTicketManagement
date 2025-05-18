package com.trainbooking.trainticketmanagement;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
            return "home";  // Redirect to home page on successful login
        }
        else {
            model.addAttribute("errorMessage", "Invalid username or password. " + "Please register if you are new.");  // Add error flag for failed login
            model.addAttribute("showRegister", true);
            return "login";  // Redirect back to login page
        }
    }

    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }

    @PostMapping("/register")
    public String registerPage(@RequestParam String username, @RequestParam String email, @RequestParam String password) {
        // Call the UserFunctions register method
        UserFunctions.registerUser(username, email, password);
        // Redirect to login page after successful registration
        return "redirect:/login";
    }

    @GetMapping("/cancel")
    public String cancelTicketPage() {
        return "cancel";
    }

    @PostMapping("/cancel")
    public String cancelTicketPage(@RequestParam String pnr) {
        int pnrNumber = Integer.parseInt(pnr);
        List<Object> message = UserFunctions.cancelTicket(pnrNumber);
        return "redirect:/cancel?message=" + URLEncoder.encode((String) message.get(1), StandardCharsets.UTF_8);
    }

    @GetMapping({"/home"})
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

    @PostMapping("/class-select")
    public String bookTicket(@RequestParam String source, @RequestParam String destination, @RequestParam String dot, Model model) {
        model.addAttribute("source", source);
        model.addAttribute("destination", destination);
        model.addAttribute("dot", dot);
        return "train-book";
    }

    @PostMapping("/user-details")
    public String selectClass(@RequestParam String trainClass, Model model) {
        model.addAttribute("trainClass", trainClass);
        return "input-details";  // Redirect to the seat layout page
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