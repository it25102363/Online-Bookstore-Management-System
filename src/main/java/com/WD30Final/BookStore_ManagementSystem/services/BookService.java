package com.WD30Final.BookStore_ManagementSystem.services;

import com.WD30Final.BookStore_ManagementSystem.dto.BookDto;
import com.WD30Final.BookStore_ManagementSystem.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookService {
    Page<Book> searchBooks(String query, String category, Pageable pageable);
    List<Book> getAllBooks();
    Book getBookById(Long id);
    Book createBook(BookDto bookDto);
    Book updateBook(Long id, BookDto bookDto);
    void deleteBook(Long id);
    void addReviewToBook(Long bookId, int rating, String comment, String reviewerName);
    List<String> getAllCategories();
}