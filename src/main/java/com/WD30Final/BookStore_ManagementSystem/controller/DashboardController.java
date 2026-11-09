
package com.WD30Final.BookStore_ManagementSystem.controller;

import com.WD30Final.BookStore_ManagementSystem.entity.Admin;
import com.WD30Final.BookStore_ManagementSystem.entity.Customer;
import com.WD30Final.BookStore_ManagementSystem.entity.User;

import com.WD30Final.BookStore_ManagementSystem.services.UserService;
import com.WD30Final.BookStore_ManagementSystem.services.OrderService;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class DashboardController {

    private final UserService userService;
    private final OrderService orderService;

    // Constructor Injection
    public DashboardController(
            UserService userService,
            OrderService orderService) {

        this.userService = userService;
        this.orderService = orderService;
    }

    // ==========================================
    // ADMIN DASHBOARD
    // ==========================================

    @GetMapping("/admin/dashboard")
    public String adminDashboard(
            Authentication authentication,
            Model model) {

        User adminUser = userService.findByEmail(
                authentication.getName()
        );

        model.addAttribute("user", adminUser);

        // Total registered users
        model.addAttribute(
                "totalUsers",
                userService.getAllUsers().size()
        );

        // Temporary book count
        model.addAttribute("totalBooks", 10);

        // Total orders from database
        model.addAttribute(
                "totalOrders",
                orderService.getTotalOrders()
        );

        // Registered users for admin table
        model.addAttribute(
                "users",
                userService.getAllUsers()
        );

        return "admin-dashboard";
    }

    // ==========================================
    // CUSTOMER DASHBOARD
    // ==========================================

    @GetMapping("/customer/dashboard")
    public String customerDashboard(
            Authentication authentication,
            Model model) {

        User user = userService.findByEmail(
                authentication.getName()
        );

        model.addAttribute("user", user);

        // Customer loyalty points
        if (user instanceof Customer customer) {

            model.addAttribute(
                    "loyaltyPoints",
                    customer.getLoyaltyPoints()
            );
        }

        // Temporary book count
        model.addAttribute("totalBooks", 10);

        // Temporary wishlist and cart counts
        model.addAttribute("wishlistCount", 0);
        model.addAttribute("cartCount", 0);

        return "customer-dashboard";
    }

    // ==========================================
    // USER MANAGEMENT - READ ALL
    // ==========================================

    @GetMapping("/users")
    public String manageUsers(Model model) {

        model.addAttribute(
                "users",
                userService.getAllUsers()
        );

        return "users";
    }

    // ==========================================
    // PROFILE PAGE
    // ==========================================

    @GetMapping("/profile")
    public String profilePage(
            Authentication authentication,
            Model model) {

        User user = userService.findByEmail(
                authentication.getName()
        );

        model.addAttribute("user", user);

        return "profile";
    }

    // ==========================================
    // PROFILE UPDATE
    // ==========================================

    @PostMapping("/profile/update")
    public String updateProfile(
            @RequestParam String name,
            @RequestParam String email,
            Authentication authentication) {

        User user = userService.findByEmail(
                authentication.getName()
        );

        User updated = userService.updateProfile(
                user.getId(),
                name,
                email
        );

        if (updated instanceof Admin) {
            return "redirect:/admin/dashboard";
        }

        return "redirect:/customer/dashboard";
    }

    // ==========================================
    // ADMIN - DELETE USER
    // ==========================================

    @PostMapping("/admin/users/delete/{id}")
    public String deleteUser(
            @PathVariable Long id) {

        userService.deleteUser(id);

        return "redirect:/admin/dashboard";
    }

    // ==========================================
    // ADMIN - VIEW USER DETAILS
    // ==========================================

    @GetMapping("/admin/users/{id}")
    public String viewUserDetails(
            @PathVariable Long id,
            Model model) {

        User targetUser = userService.getUserById(id);

        model.addAttribute(
                "targetUser",
                targetUser
        );

        return "admin-user-details";
    }
}
