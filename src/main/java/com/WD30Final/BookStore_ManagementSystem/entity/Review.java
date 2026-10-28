package com.WD30Final.BookStore_ManagementSystem.entity;

import jakarta.persistence.*;

/**
 * Review extends BaseEntity  ->  INHERITANCE (id, createdAt, updatedAt come from the parent).
 * Fields are private with getters/setters  ->  ENCAPSULATION.
 * The setters validate the data so an invalid Review can never exist.
 */
@Entity
@Table(name = "reviews")
public class Review extends BaseEntity {

    @Column(name = "book_title", nullable = false, length = 150)
    private String bookTitle;

    @Column(nullable = false)
    private int rating;

    @Column(nullable = false, length = 1000)
    private String comment;

    @Column(name = "customer_email", nullable = false, length = 150)
    private String customerEmail;

    @Column(name = "customer_name", nullable = false, length = 100)
    private String customerName;

    protected Review() { }   // required by JPA

    public Review(String bookTitle, int rating, String comment, String customerEmail, String customerName) {
        setBookTitle(bookTitle);
        setRating(rating);
        setComment(comment);
        this.customerEmail = customerEmail;
        this.customerName = customerName;
    }

    // ---- behaviour that belongs to the object ----
    public boolean isOwnedBy(String email) {
        return customerEmail != null && customerEmail.equalsIgnoreCase(email);
    }

    public void edit(String bookTitle, int rating, String comment) {
        setBookTitle(bookTitle);
        setRating(rating);
        setComment(comment);
    }

    // ---- getters / validated setters ----
    public String getBookTitle() { return bookTitle; }

    public void setBookTitle(String bookTitle) {
        if (bookTitle == null || bookTitle.isBlank()) {
            throw new IllegalArgumentException("Book title is required");
        }
        this.bookTitle = bookTitle.trim();
    }

    public int getRating() { return rating; }

    public void setRating(int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        this.rating = rating;
    }

    public String getComment() { return comment; }

    public void setComment(String comment) {
        if (comment == null || comment.isBlank()) {
            throw new IllegalArgumentException("Comment is required");
        }
        this.comment = comment.trim();
    }

    public String getCustomerEmail() { return customerEmail; }

    public String getCustomerName() { return customerName; }
}
