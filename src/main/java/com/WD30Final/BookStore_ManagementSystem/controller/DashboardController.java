package com.WD30Final.BookStore_ManagementSystem.controller;

import com.WD30Final.BookStore_ManagementSystem.entity.Admin;
import com.WD30Final.BookStore_ManagementSystem.entity.Customer;
import com.WD30Final.BookStore_ManagementSystem.entity.User;
import com.WD30Final.BookStore_ManagementSystem.repository.BookRepository;
import com.WD30Final.BookStore_ManagementSystem.repository.OrderRepository;
import com.WD30Final.BookStore_ManagementSystem.services.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class DashboardController {

    private final UserService userService;
    private final BookRepository bookRepository;
    private final OrderRepository orderRepository;

    public DashboardController(UserService userService,
                               BookRepository bookRepository,
                               OrderRepository orderRepository) {
        this.userService = userService;
        this.bookRepository = bookRepository;
        this.orderRepository = orderRepository;
    }

    // Unified Admin Dashboard (Displays real-time live stats and user list table)
    @GetMapping("/admin/dashboard")
    public String adminDashboard(Authentication authentication, Model model) {
        User adminUser = (authentication != null) ? userService.findByEmail(authentication.getName()) : null;

        long userCount = userService.getAllUsers().size();
        long bookCount = bookRepository.count();
        long orderCount = orderRepository.count();

        model.addAttribute("user", adminUser);
        model.addAttribute("totalUsers", userCount);
        model.addAttribute("totalBooks", bookCount);
        model.addAttribute("totalOrders", orderCount);
        model.addAttribute("users", userService.getAllUsers());

        return "admin-dashboard";
    }

    // Customer Dashboard
    @GetMapping("/customer/dashboard")
    public String customerDashboard(Authentication authentication, Model model) {
        User user = (authentication != null) ? userService.findByEmail(authentication.getName()) : null;
        model.addAttribute("user", user);

        // Loyalty points check for Customer entity
        if (user instanceof Customer customer) {
            model.addAttribute("loyaltyPoints", customer.getLoyaltyPoints());
        }

        model.addAttribute("totalBooks", bookRepository.count());
        model.addAttribute("wishlistCount", 0);
        model.addAttribute("cartCount", 0);

        return "customer-dashboard";
    }

    // CRUD Operations UI for User Management table
    @GetMapping("/users")
    public String manageUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "users";
    }

    // Profile Page View
    @GetMapping("/profile")
    public String profilePage(Authentication authentication, Model model) {
        User user = userService.findByEmail(authentication.getName());
        model.addAttribute("user", user);
        return "profile";
    }

    // Profile Update Action
    @PostMapping("/profile/update")
    public String updateProfile(@RequestParam String name, @RequestParam String email, Authentication authentication) {
        User user = userService.findByEmail(authentication.getName());
        User updated = userService.updateProfile(user.getId(), name, email);
        if (updated instanceof Admin) {
            return "redirect:/admin/dashboard";
        }
        return "redirect:/customer/dashboard";
    }

    // Admin Action: Delete User
    @PostMapping("/admin/users/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/admin/dashboard";
    }

    // Admin Action: View Single User Details
    @GetMapping("/admin/users/{id}")
    public String viewUserDetails(@PathVariable Long id, Model model) {
        User targetUser = userService.getUserById(id);
        model.addAttribute("targetUser", targetUser);
        return "admin-user-details";
    }
}