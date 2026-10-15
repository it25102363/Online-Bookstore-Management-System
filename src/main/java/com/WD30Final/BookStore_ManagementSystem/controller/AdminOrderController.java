package com.WD30Final.BookStore_ManagementSystem.controller;

import com.WD30Final.BookStore_ManagementSystem.entity.Order;
import com.WD30Final.BookStore_ManagementSystem.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderRepository orderRepository;

    @GetMapping
    public String listOrders(Model model) {
        List<Order> orders = orderRepository.findAllByOrderByCreatedAtDesc();
        model.addAttribute("orders", orders);
        return "admin/orders";
    }

    @GetMapping("/{id}")
    public String viewOrder(@PathVariable Long id, Model model) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + id));
        model.addAttribute("order", order);
        return "admin/order-details";
    }
}