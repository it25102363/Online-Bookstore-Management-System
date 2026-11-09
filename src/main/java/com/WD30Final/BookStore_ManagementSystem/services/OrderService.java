
package com.WD30Final.BookStore_ManagementSystem.services;

import com.WD30Final.BookStore_ManagementSystem.entity.Customer;
import com.WD30Final.BookStore_ManagementSystem.entity.Order;
import com.WD30Final.BookStore_ManagementSystem.entity.OrderItem;
import com.WD30Final.BookStore_ManagementSystem.entity.OrderStatus;
import com.WD30Final.BookStore_ManagementSystem.entity.User;

import com.WD30Final.BookStore_ManagementSystem.repository.OrderRepository;
import com.WD30Final.BookStore_ManagementSystem.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderService {

    // ==========================================
    // ENCAPSULATION - PRIVATE DEPENDENCIES
    // ==========================================

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    // Constructor Injection
    public OrderService(
            OrderRepository orderRepository,
            UserRepository userRepository) {

        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    // ==========================================
    // ABSTRACTION - CUSTOMER VALIDATION
    // ==========================================

    private Customer requireCustomer(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Customer not found"
                        )
                );

        // Uses the existing User -> Customer inheritance
        if (!(user instanceof Customer customer)) {
            throw new IllegalArgumentException(
                    "Only customers can place and view customer orders"
            );
        }

        return customer;
    }

    // ==========================================
    // CREATE - PLACE A NEW ORDER
    // ==========================================

    @Transactional
    public Order placeOrder(
            String customerEmail,
            List<OrderItem> verifiedCartItems) {

        Customer customer = requireCustomer(customerEmail);

        if (verifiedCartItems == null ||
                verifiedCartItems.isEmpty()) {

            throw new IllegalArgumentException(
                    "Cannot place an empty order"
            );
        }

        Order order = new Order();
        order.setCustomer(customer);

        // New orders have PENDING status by default.
        // Order manages its own status and total.

        for (OrderItem source : verifiedCartItems) {

            if (source == null ||
                    source.getBookTitle() == null ||
                    source.getBookTitle().isBlank()) {

                throw new IllegalArgumentException(
                        "Invalid order item"
                );
            }

            // Constructor validates Book ID,
            // Quantity and Unit Price.
            OrderItem item = new OrderItem(
                    source.getBookId(),
                    source.getBookTitle(),
                    source.getQuantity(),
                    source.getUnitPrice()
            );

            // Encapsulation:
            // Order controls the item collection.
            //
            // Abstraction:
            // Order automatically calculates the total.
            order.addItem(item);
        }

        // No manual total calculation is needed.
        // Order.addItem() already updates totalAmount.

        return orderRepository.save(order);
    }

    // ==========================================
    // READ - CUSTOMER ORDER HISTORY
    // ==========================================

    @Transactional(readOnly = true)
    public List<Order> getCustomerOrders(
            String customerEmail) {

        Customer customer = requireCustomer(customerEmail);

        return orderRepository
                .findByCustomer_IdOrderByCreatedAtDesc(
                        customer.getId()
                );
    }

    // ==========================================
    // READ - CUSTOMER ORDER DETAILS
    // ==========================================

    @Transactional(readOnly = true)
    public Order getCustomerOrder(
            Long orderId,
            String customerEmail) {

        Customer customer = requireCustomer(customerEmail);

        // Customer can only access their own orders.
        Order order = orderRepository
                .findByIdAndCustomer_Id(
                        orderId,
                        customer.getId()
                )
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Order not found"
                        )
                );

        // Initialize items while transaction is active.
        order.getItems().size();

        return order;
    }

    // ==========================================
    // READ - ADMIN ALL ORDERS
    // ==========================================

    @Transactional(readOnly = true)
    public List<Order> getAllOrders() {

        List<Order> orders = orderRepository
                .findAllByOrderByCreatedAtDesc();

        for (Order order : orders) {

            // Load related customer information.
            order.getCustomer().getName();

            // Load related order items.
            order.getItems().size();
        }

        return orders;
    }

    // ==========================================
    // READ - ADMIN SINGLE ORDER
    // ==========================================

    @Transactional(readOnly = true)
    public Order getOrderById(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Order not found"
                        )
                );

        order.getCustomer().getName();
        order.getItems().size();

        return order;
    }

    // ==========================================
    // UPDATE - ADMIN ORDER STATUS
    // ==========================================

    @Transactional
    public Order updateOrderStatus(
            Long orderId,
            OrderStatus newStatus) {

        Order order = getOrderById(orderId);

        if (newStatus == null) {
            throw new IllegalArgumentException(
                    "Order status is required"
            );
        }

        // No change required.
        if (order.getStatus() == newStatus) {
            return order;
        }

        // Encapsulation + Information Hiding:
        // Order class validates the transition.
        order.changeStatus(newStatus);

        return orderRepository.save(order);
    }

    // ==========================================
    // UPDATE - CUSTOMER CANCEL ORDER
    // ==========================================

    @Transactional
    public Order cancelOrder(
            Long orderId,
            String customerEmail) {

        // Also checks customer ownership.
        Order order = getCustomerOrder(
                orderId,
                customerEmail
        );

        // Business rule belongs to Order.
        if (!order.canCancel()) {
            throw new IllegalStateException(
                    "This order cannot be cancelled"
            );
        }

        // Controlled status change.
        order.changeStatus(OrderStatus.CANCELLED);

        return orderRepository.save(order);
    }

    // ==========================================
    // DELETE - ADMIN DELETE CANCELLED ORDER
    // ==========================================

    @Transactional
    public void deleteOrder(Long orderId) {

        Order order = getOrderById(orderId);

        // Order controls its deletion rule.
        if (!order.canDelete()) {
            throw new IllegalStateException(
                    "Only cancelled orders can be deleted"
            );
        }

        orderRepository.delete(order);
    }

    // ==========================================
    // READ - TOTAL ORDER COUNT
    // ==========================================

    @Transactional(readOnly = true)
    public long getTotalOrders() {

        return orderRepository.count();
    }
}
