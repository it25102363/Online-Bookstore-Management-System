
package com.WD30Final.BookStore_ManagementSystem.model;

import java.math.BigDecimal;

public abstract class Book {

    private final String isbn;
    private String title;
    private String author;
    private String category;
    private int quantity;
    private String imageName;
    private BigDecimal price;

    public Book(String isbn, String title, String author,
                String category, int quantity) {

        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.category = category;
        this.quantity = quantity;
        this.imageName = "";
        this.price = BigDecimal.ZERO;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName == null ? "" : imageName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public abstract String getBookType();
}
