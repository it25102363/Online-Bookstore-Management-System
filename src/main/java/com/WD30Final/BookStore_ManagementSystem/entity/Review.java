package com.WD30Final.BookStore_ManagementSystem.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "book_reviews")
@Getter
@Setter
@NoArgsConstructor
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Min(1)
    @Max(5)
    @Column(nullable = false)
    private int rating;

    @Column(length = 1000, nullable = false)
    private String comment;

    @Column(nullable = false)
    private String reviewerName;

    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    public Review(int rating, String comment, String reviewerName, Book book) {
        this.rating = rating;
        this.comment = comment;
        this.reviewerName = reviewerName;
        this.book = book;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}