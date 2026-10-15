package com.WD30Final.BookStore_ManagementSystem.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "books")
@PrimaryKeyJoinColumn(name = "id")
@Getter
@Setter
@NoArgsConstructor
public class Book extends Item {

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false, unique = true)
    private String isbn;

    private String category;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "cover_image_url")
    private String coverImageUrl;

    private Double rating = 0.0;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Review> reviews = new ArrayList<>();

    public Book(String title, String author, String isbn, BigDecimal price, Integer stock,
                String category, String description, String coverImageUrl) {
        super(price, stock, "BOOK");
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.category = category;
        this.description = description;
        this.coverImageUrl = coverImageUrl;
        this.rating = 0.0;
    }

    @PrePersist
    public void setupType() {
        setItemType("BOOK");
    }

    public void addReview(Review review) {
        if (this.reviews == null) {
            this.reviews = new ArrayList<>();
        }
        this.reviews.add(review);
        review.setBook(this);
        recalculateRating();
    }

    public void removeReview(Review review) {
        if (this.reviews != null) {
            this.reviews.remove(review);
            review.setBook(null);
            recalculateRating();
        }
    }

    private void recalculateRating() {
        if (this.reviews == null || this.reviews.isEmpty()) {
            this.rating = 0.0;
            return;
        }
        double avg = this.reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);
        this.rating = BigDecimal.valueOf(avg)
                .setScale(1, RoundingMode.HALF_UP)
                .doubleValue();
    }

    @Override
    public String getItemType() {
        return "BOOK";
    }

    @Override
    public BigDecimal calculateDiscountedPrice(double discountPercentage) {
        if (discountPercentage <= 0) {
            return getPrice();
        }
        double effectiveDiscount = Math.min(discountPercentage, 50.0);
        BigDecimal factor = BigDecimal.valueOf(1 - (effectiveDiscount / 100.0));
        return getPrice().multiply(factor).setScale(2, RoundingMode.HALF_UP);
    }
}