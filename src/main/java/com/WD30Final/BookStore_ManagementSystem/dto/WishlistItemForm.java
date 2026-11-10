package com.WD30Final.BookStore_ManagementSystem.dto;

import java.math.BigDecimal;

public class WishlistItemForm {
    private Long bookId;
    private String type = "STANDARD";
    private String note;
    private BigDecimal targetPrice;
    private String recipientName;

    public Long getBookId() { return bookId; }
    public void setBookId(Long bookId) { this.bookId = bookId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public BigDecimal getTargetPrice() { return targetPrice; }
    public void setTargetPrice(BigDecimal targetPrice) { this.targetPrice = targetPrice; }
    public String getRecipientName() { return recipientName; }
    public void setRecipientName(String recipientName) { this.recipientName = recipientName; }
}
