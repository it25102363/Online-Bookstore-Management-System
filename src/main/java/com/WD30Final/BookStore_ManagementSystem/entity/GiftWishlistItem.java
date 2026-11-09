package com.WD30Final.BookStore_ManagementSystem.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.math.BigDecimal;

@Entity
@DiscriminatorValue("GIFT")
public class GiftWishlistItem extends WishlistItem {

    private static final int MAX_NAME_LENGTH = 100;

    @Column(name = "recipient_name", length = MAX_NAME_LENGTH)
    private String recipientName;

    public String getRecipientName() { return recipientName; }

    public void setRecipientName(String recipientName) {
        if (recipientName == null || recipientName.isBlank()) {
            this.recipientName = null;
            return;
        }
        String cleaned = recipientName.trim();
        if (cleaned.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException("Recipient name must be " + MAX_NAME_LENGTH + " characters or fewer.");
        }
        this.recipientName = cleaned;
    }

    @Override public String getTypeCode() { return "GIFT"; }
    @Override public String getTypeLabel() { return "Gift idea"; }
    @Override public int getPriorityLevel() { return 2; }
    @Override public String getHighlight() {
        return recipientName == null ? "A gift for someone special" : "Gift for " + recipientName;
    }
    @Override public void applyDetails(BigDecimal targetPrice, String recipientName) {
        setRecipientName(recipientName);
    }
}
