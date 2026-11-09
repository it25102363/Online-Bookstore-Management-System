package com.WD30Final.BookStore_ManagementSystem.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.math.BigDecimal;

@Entity
@DiscriminatorValue("STANDARD")
public class StandardWishlistItem extends WishlistItem {
    @Override public String getTypeCode() { return "STANDARD"; }
    @Override public String getTypeLabel() { return "Saved"; }
    @Override public int getPriorityLevel() { return 1; }
    @Override public String getHighlight() { return "On your reading list"; }
    @Override public void applyDetails(BigDecimal targetPrice, String recipientName) { }
}
