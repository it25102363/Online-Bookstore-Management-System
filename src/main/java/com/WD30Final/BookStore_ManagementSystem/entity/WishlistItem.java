package com.WD30Final.BookStore_ManagementSystem.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "wishlist_items",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "book_id"}))
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "item_type", discriminatorType = DiscriminatorType.STRING)
public abstract class WishlistItem {

    private static final int MAX_NOTE_LENGTH = 255;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(length = MAX_NOTE_LENGTH)
    private String note;

    @Column(name = "added_at", nullable = false, updatable = false)
    private LocalDateTime addedAt;

    @PrePersist
    protected void onCreate() {
        if (addedAt == null) {
            addedAt = LocalDateTime.now();
        }
    }

    public abstract String getTypeCode();
    public abstract String getTypeLabel();
    public abstract int getPriorityLevel();
    public abstract String getHighlight();
    public abstract void applyDetails(BigDecimal targetPrice, String recipientName);

    public String getSummary() {
        return getTypeLabel() + " - " + book.getTitle();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Book getBook() { return book; }
    public void setBook(Book book) { this.book = book; }
    public String getNote() { return note; }
    public LocalDateTime getAddedAt() { return addedAt; }
    public void setAddedAt(LocalDateTime addedAt) { this.addedAt = addedAt; }

    public void setNote(String note) {
        if (note == null || note.isBlank()) {
            this.note = null;
            return;
        }
        String cleaned = note.trim();
        if (cleaned.length() > MAX_NOTE_LENGTH) {
            throw new IllegalArgumentException("Note must be " + MAX_NOTE_LENGTH + " characters or fewer.");
        }
        this.note = cleaned;
    }
}
