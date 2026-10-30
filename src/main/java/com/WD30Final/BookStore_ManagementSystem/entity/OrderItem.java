
package com.WD30Final.BookStore_ManagementSystem.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
public class OrderItem {

    // ========================================
    // ENCAPSULATION - PRIVATE ATTRIBUTES
    // ========================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many OrderItems belong to one Order
    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "order_id",
            nullable = false
    )
    private Order order;

    @Column(name = "book_id", nullable = false)
    private Long bookId;

    // Book title is not stored in order_items
    @Transient
    private String bookTitle;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    // Actual database column is "price"
    @Column(
            name = "price",
            nullable = false,
            precision = 38,
            scale = 2
    )
    private BigDecimal unitPrice;

    // ========================================
    // CONSTRUCTORS
    // ========================================

    // Required by JPA
    public OrderItem() {
    }

    // Parameterized Constructor
    public OrderItem(
            Long bookId,
            String bookTitle,
            int quantity,
            BigDecimal unitPrice) {

        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.quantity = quantity;
        this.unitPrice = unitPrice;

        validateForOrder();
    }

    // ========================================
    // ABSTRACTION - VALIDATION
    // ========================================

    public void validateForOrder() {

        if (bookId == null || bookId <= 0) {
            throw new IllegalArgumentException(
                    "Valid Book ID is required"
            );
        }

        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be greater than zero"
            );
        }

        if (unitPrice == null ||
                unitPrice.signum() < 0) {

            throw new IllegalArgumentException(
                    "Unit price cannot be negative"
            );
        }
    }

    // ========================================
    // ABSTRACTION - SUBTOTAL CALCULATION
    // ========================================

    @Transient
    public BigDecimal getSubtotal() {

        if (unitPrice == null) {
            return BigDecimal.ZERO;
        }

        return unitPrice.multiply(
                BigDecimal.valueOf(quantity)
        );
    }

    // ========================================
    // INFORMATION HIDING - READ METHODS
    // ========================================

    public Long getId() {
        return id;
    }

    public Order getOrder() {
        return order;
    }

    public Long getBookId() {
        return bookId;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    // ========================================
    // SETTERS - KEPT FOR COMPATIBILITY
    // ========================================

    public void setOrder(Order order) {
        this.order = order;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }
}
