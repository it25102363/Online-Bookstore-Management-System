
package com.WD30Final.BookStore_ManagementSystem.model;

public class PrintedBook extends Book {

    public PrintedBook(String isbn, String title, String author,
                       String category, int quantity) {
        super(isbn, title, author, category, quantity);
    }

    @Override
    public String getBookType() {
        return "Printed Book";
    }
}
