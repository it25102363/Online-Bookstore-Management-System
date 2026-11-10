package com.WD30Final.BookStore_ManagementSystem.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class BookOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Double totalPrice;

    @Column(nullable = false)
    private String paymentCardUsed;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private LocalDateTime orderDate;

    public BookOrder() {
        this.orderDate = LocalDateTime.now();
        this.status = "COMPLETED";
    }

    public BookOrder(Long userId, Double totalPrice, String paymentCardUsed) {
        this.userId = userId;
        this.totalPrice = totalPrice;
        this.paymentCardUsed = paymentCardUsed;
        this.orderDate = LocalDateTime.now();
        this.status = "COMPLETED";
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(Double totalPrice) { this.totalPrice = totalPrice; }

    public String getPaymentCardUsed() { return paymentCardUsed; }
    public void setPaymentCardUsed(String paymentCardUsed) { this.paymentCardUsed = paymentCardUsed; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }
}