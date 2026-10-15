package com.WD30Final.BookStore_ManagementSystem.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 64)
    private String orderNumber;

    @Column(nullable = false)
    private String customerEmail;

    private String customerName;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(nullable = false, length = 50)
    private String status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    public Order(String customerEmail, String customerName) {
        this.orderNumber = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.customerEmail = customerEmail;
        this.customerName = customerName;
        this.status = "COMPLETED";
        this.createdAt = LocalDateTime.now();
        this.totalAmount = BigDecimal.ZERO;
    }

    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
        calculateTotal();
    }

    public void calculateTotal() {
        this.totalAmount = items.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}