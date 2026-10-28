package com.WD30Final.BookStore_ManagementSystem.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * OOP PRINCIPLE 1 - INHERITANCE
 * Common fields (id + audit timestamps) live in one parent class.
 * Review extends this class and reuses everything instead of repeating it.
 *
 * OOP PRINCIPLE 2 - ENCAPSULATION
 * All fields are private; the outside world only uses getters.
 * Timestamps cannot be set from outside - the class manages them itself.
 */
@MappedSuperclass
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
