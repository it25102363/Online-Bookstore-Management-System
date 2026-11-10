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

    // Show Login Page
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    // Show Registration Page
    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new Customer());
        return "register";
    }

    // Register new Customer
    @PostMapping("/register")
    public String register(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String password) {

        Customer customer = new Customer();

        customer.setName(name);
        customer.setEmail(email);
        customer.setPassword(password);
        customer.setLoyaltyPoints(50);

        // UserService encrypts the password and saves the user
        userService.saveUser(customer);

        return "redirect:/login?registered";
    }
}