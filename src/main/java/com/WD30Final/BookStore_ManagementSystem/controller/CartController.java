package com.WD30Final.BookStore_ManagementSystem.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CartController {

    @GetMapping("/cart")
    public String viewCart(Model model) {
        model.addAttribute("title", "Shopping Cart");
        return "cart"; // This looks directly for cart.html inside templates/
    }
}