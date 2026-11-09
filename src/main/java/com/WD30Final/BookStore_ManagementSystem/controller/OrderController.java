
package com.WD30Final.BookStore_ManagementSystem.controller;

import com.WD30Final.BookStore_ManagementSystem.entity.Order;
import com.WD30Final.BookStore_ManagementSystem.services.OrderService;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    // Constructor Injection
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // ==========================================
    // READ - View Customer Order History
    // ==========================================

    @GetMapping
    public String viewOrders(
            Principal principal,
            Model model) {

        String email = principal.getName();

        List<Order> orders =
                orderService.getCustomerOrders(email);

        model.addAttribute("orders", orders);

        return "orders";
    }

    // ==========================================
    // READ - View a Specific Order
    // ==========================================

    @GetMapping("/{orderId}")
    public String viewOrderDetails(
            @PathVariable Long orderId,
            Principal principal,
            Model model) {

        try {
            Order order = orderService.getCustomerOrder(
                    orderId,
                    principal.getName()
            );

            model.addAttribute("order", order);
            model.addAttribute("isAdmin", false);

            return "order-details";

        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Order not found"
            );
        }
    }

    // ==========================================
    // UPDATE - Cancel Customer Order
    // ==========================================

    @PostMapping("/{orderId}/cancel")
    public String cancelOrder(
            @PathVariable Long orderId,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        try {
            orderService.cancelOrder(
                    orderId,
                    principal.getName()
            );

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Order cancelled successfully."
            );

        } catch (IllegalStateException ex) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    ex.getMessage()
            );

        } catch (IllegalArgumentException ex) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Order not found."
            );
        }

        return "redirect:/orders";
    }
}
