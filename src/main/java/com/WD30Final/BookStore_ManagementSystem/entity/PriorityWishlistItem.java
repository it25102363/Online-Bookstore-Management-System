package com.WD30Final.BookStore_ManagementSystem.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@DiscriminatorValue("PRIORITY")
public class PriorityWishlistItem extends WishlistItem {

    @Column(name = "target_price", precision = 10, scale = 2)
    private BigDecimal targetPrice;

    public BigDecimal getTargetPrice() { return targetPrice; }

    public void setTargetPrice(BigDecimal targetPrice) {
        if (targetPrice != null && targetPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Target price cannot be negative.");
        }
        this.targetPrice = targetPrice;
    }

    public boolean isTargetReached() {
        BigDecimal price = getBook().getPrice();
        return targetPrice != null && price != null && price.compareTo(targetPrice) <= 0;
    }

    @Override public String getTypeCode() { return "PRIORITY"; }
    @Override public String getTypeLabel() { return "Must read"; }
    @Override public int getPriorityLevel() { return 3; }

    @Override
    public String getHighlight() {
        if (targetPrice == null) return "Top of your reading list";
        if (isTargetReached()) return "Price target reached";
        return "Waiting for $" + targetPrice.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public void applyDetails(BigDecimal targetPrice, String recipientName) {
        setTargetPrice(targetPrice);
    }
}
