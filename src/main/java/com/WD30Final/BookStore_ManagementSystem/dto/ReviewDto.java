package com.WD30Final.BookStore_ManagementSystem.dto;

import jakarta.validation.constraints.*;

/**
 * Carries data between the HTML form and the service.
 * The entity is never exposed directly to the page.
 */
public class ReviewDto {

    private Long id;

    @NotBlank(message = "Book title is required")
    @Size(max = 150, message = "Book title is too long")
    private String bookTitle;

    @Min(value = 1, message = "Pick a rating from 1 to 5")
    @Max(value = 5, message = "Pick a rating from 1 to 5")
    private int rating = 5;

    @NotBlank(message = "Please write a comment")
    @Size(max = 1000, message = "Comment can be at most 1000 characters")
    private String comment;

    // read-only fields shown in lists
    private String customerName;
    private String createdAt;

    public ReviewDto() { }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
