
package com.WD30Final.BookStore_ManagementSystem.repository;

import com.WD30Final.BookStore_ManagementSystem.entity.Order;
import com.WD30Final.BookStore_ManagementSystem.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // Get all orders belonging to a customer
    List<Order> findByCustomer_IdOrderByCreatedAtDesc(Long customerId);

    // Find an order belonging to a specific customer
    Optional<Order> findByIdAndCustomer_Id(Long orderId, Long customerId);

    // Get all orders (newest first)
    List<Order> findAllByOrderByCreatedAtDesc();

    // Count orders by status
    long countByStatus(OrderStatus status);
}
