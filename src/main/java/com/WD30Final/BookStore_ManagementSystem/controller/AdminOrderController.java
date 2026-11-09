
package com.WD30Final.BookStore_ManagementSystem.controller;

import com.WD30Final.BookStore_ManagementSystem.entity.Order;
import com.WD30Final.BookStore_ManagementSystem.entity.OrderStatus;
import com.WD30Final.BookStore_ManagementSystem.services.OrderService;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/orders")
public class AdminOrderController {

    private final OrderService orderService;

    // Constructor Injection
    public AdminOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // ==========================================
    // READ - View All Orders
    // ==========================================

    @GetMapping
    public String viewAllOrders(Model model) {

        List<Order> orders =
                orderService.getAllOrders();

        model.addAttribute("orders", orders);
        model.addAttribute(
                "statuses",
                OrderStatus.values()
        );

        return "admin-orders";
    }

    // ==========================================
    // READ - View Order Details
    // ==========================================

    @GetMapping("/{orderId}")
    public String viewOrderDetails(
            @PathVariable Long orderId,
            Model model) {

        try {
            Order order =
                    orderService.getOrderById(orderId);

            model.addAttribute("order", order);
            model.addAttribute("isAdmin", true);
            model.addAttribute(
                    "statuses",
                    OrderStatus.values()
            );

            return "order-details";

        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Order not found"
            );
        }
    }

    // ==========================================
    // UPDATE - Change Order Status
    // ==========================================

    @PostMapping("/{orderId}/status")
    public String updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam("status") OrderStatus status,
            RedirectAttributes redirectAttributes) {

        try {
            orderService.updateOrderStatus(
                    orderId,
                    status
            );

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Order status updated successfully."
            );

        } catch (IllegalArgumentException |
                 IllegalStateException ex) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    ex.getMessage()
            );
        }

        return "redirect:/admin/orders";
    }

    // ==========================================
    // DELETE - Delete Cancelled Order
    // ==========================================

    @PostMapping("/{orderId}/delete")
    public String deleteOrder(
            @PathVariable Long orderId,
            RedirectAttributes redirectAttributes) {

        try {
            orderService.deleteOrder(orderId);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Order deleted successfully."
            );

        } catch (IllegalArgumentException |
                 IllegalStateException ex) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    ex.getMessage()
            );
        }

        return "redirect:/admin/orders";
    }
}
