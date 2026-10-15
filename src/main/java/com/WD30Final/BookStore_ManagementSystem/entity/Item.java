package com.WD30Final.BookStore_ManagementSystem.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "items")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer stock = 0;

    @Column(name = "item_type", nullable = false)
    private String itemType;

    public boolean isOutOfStock() {
        return this.stock == null || this.stock <= 0;
    }

    public boolean isLowStock(int threshold) {
        return this.stock != null && this.stock > 0 && this.stock <= threshold;
    }

    public abstract BigDecimal calculateDiscountedPrice(double discountPercentage);

    public Item(BigDecimal price, Integer stock, String itemType) {
        this.price = price;
        this.stock = stock != null ? stock : 0;
        this.itemType = itemType;
    }



}