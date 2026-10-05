package com.WD30Final.BookStore_ManagementSystem.controller;

import com.WD30Final.BookStore_ManagementSystem.entity.Customer;
import com.WD30Final.BookStore_ManagementSystem.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // Render the Login page
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    // Render the Register page and provide an empty Customer object for form binding
    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new Customer());
        return "register";
    }

    // Process new user registrations (automatically saved as a Customer)
    @PostMapping("/register")
    public String register(@RequestParam String name,
                           @RequestParam String email,
                           @RequestParam String password) {

        Customer customer = new Customer();
        customer.setName(name);
        customer.setEmail(email);
        customer.setLoyaltyPoints(50); // Default welcome points

        userService.saveUser(customer, password);

        return "redirect:/login?registered";
    }
}