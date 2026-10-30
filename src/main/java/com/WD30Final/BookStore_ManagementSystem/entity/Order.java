
package com.WD30Final.BookStore_ManagementSystem.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "orders")
public class Order {

    // ======================================
    // ENCAPSULATION - PRIVATE DATA MEMBERS
    // ======================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many Orders belong to one Customer
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User customer;

    // One Order contains many OrderItems
    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<OrderItem> items = new ArrayList<>();

    @Column(
            name = "total_amount",
            precision = 38,
            scale = 2,
            nullable = false
    )
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status = OrderStatus.PENDING;

    // Existing MySQL column name
    @Column(
            name = "order_date",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    // ======================================
    // DEFAULT CONSTRUCTOR - JPA
    // ======================================

    public Order() {
    }

    // ======================================
    // INITIALIZE NEW ORDER
    // ======================================

    @PrePersist
    public void onCreate() {

        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }

        if (status == null) {
            status = OrderStatus.PENDING;
        }
    }

    // ======================================
    // CONTROLLED ITEM MANAGEMENT
    // ======================================

    public void addItem(OrderItem item) {

        Objects.requireNonNull(
                item,
                "Order item cannot be null"
        );

        if (item.getQuantity() <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be greater than zero"
            );
        }

        if (item.getUnitPrice() == null ||
                item.getUnitPrice().signum() < 0) {

            throw new IllegalArgumentException(
                    "Invalid order item price"
            );
        }

        if (item.getOrder() != null &&
                item.getOrder() != this) {

            throw new IllegalArgumentException(
                    "Item already belongs to another order"
            );
        }

        if (items.contains(item)) {
            throw new IllegalArgumentException(
                    "Item is already in this order"
            );
        }

        items.add(item);
        item.setOrder(this);

        recalculateTotalAmount();
    }

    public void removeItem(OrderItem item) {

        if (item != null && items.remove(item)) {

            item.setOrder(null);

            recalculateTotalAmount();
        }
    }

    // ======================================
    // ABSTRACTION + INFORMATION HIDING
    // AUTOMATIC TOTAL CALCULATION
    // ======================================

    private void recalculateTotalAmount() {

        BigDecimal total = BigDecimal.ZERO;

        for (OrderItem item : items) {

            total = total.add(
                    item.getSubtotal()
            );
        }

        this.totalAmount = total;
    }

    // ======================================
    // CONTROLLED ORDER STATUS UPDATE
    // ======================================

    public void changeStatus(OrderStatus newStatus) {

        if (newStatus == null) {
            throw new IllegalArgumentException(
                    "Order status cannot be null"
            );
        }

        if (!isValidStatusTransition(newStatus)) {
            throw new IllegalStateException(
                    "Invalid order status transition: "
                            + status + " to " + newStatus
            );
        }

        this.status = newStatus;
    }

    // ======================================
    // PRIVATE STATUS VALIDATION LOGIC
    // ======================================


    private boolean isValidStatusTransition(
            OrderStatus newStatus) {

        return status != null
                && status.canTransitionTo(newStatus);
    }

    // ======================================
    // ORDER BUSINESS RULES
    // ======================================

    public boolean canCancel() {

        return status == OrderStatus.PENDING
                || status == OrderStatus.CONFIRMED;
    }

    public boolean canDelete() {

        return status == OrderStatus.CANCELLED;
    }

    // ======================================
    // GETTERS
    // ======================================

    public Long getId() {
        return id;
    }

    public User getCustomer() {
        return customer;
    }

    public List<OrderItem> getItems() {

        return Collections.unmodifiableList(items);
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // ======================================
    // CONTROLLED SETTERS
    // ======================================

    public void setCustomer(User customer) {

        this.customer = Objects.requireNonNull(
                customer,
                "Customer cannot be null"
        );
    }

    public void setTotalAmount(BigDecimal totalAmount) {

        if (totalAmount == null ||
                totalAmount.signum() < 0) {

            throw new IllegalArgumentException(
                    "Total amount cannot be negative"
            );
        }

        this.totalAmount = totalAmount;
    }

    // Kept temporarily for compatibility with
    // existing OrderService and unit tests.
    // The service will later use changeStatus().
    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}
