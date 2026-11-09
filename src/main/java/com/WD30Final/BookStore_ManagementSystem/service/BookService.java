
package com.WD30Final.BookStore_ManagementSystem.service;

import com.WD30Final.BookStore_ManagementSystem.model.Book;
import com.WD30Final.BookStore_ManagementSystem.model.PrintedBook;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Service
public class BookService {

    private final JdbcTemplate jdbcTemplate;

    public BookService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private Book mapBook(java.sql.ResultSet rs, int rowNum)
            throws java.sql.SQLException {

        Book book = new PrintedBook(
                rs.getString("isbn"),
                rs.getString("title"),
                rs.getString("author"),
                rs.getString("category"),
                rs.getInt("quantity")
        );

        book.setPrice(rs.getBigDecimal("price"));

        String imageUrl = rs.getString("cover_image_url");

        if (imageUrl != null && !imageUrl.isBlank()) {
            String prefix = "/book-covers/";

            if (imageUrl.startsWith(prefix)) {
                book.setImageName(
                        imageUrl.substring(prefix.length())
                );
            } else if (!imageUrl.contains("/")) {
                book.setImageName(imageUrl);
            }
        }

        return book;
    }

    public List<Book> viewBooks() {

        String sql = """
                SELECT b.isbn, b.title, b.author,
                       b.category, b.quantity,
                       b.cover_image_url, i.price
                FROM books b
                JOIN items i ON b.id = i.id
                ORDER BY b.id
                """;

        return jdbcTemplate.query(sql, this::mapBook);
    }

    public Book searchBook(String isbn) {

        String sql = """
                SELECT b.isbn, b.title, b.author,
                       b.category, b.quantity,
                       b.cover_image_url, i.price
                FROM books b
                JOIN items i ON b.id = i.id
                WHERE b.isbn = ?
                """;

        List<Book> results = jdbcTemplate.query(
                sql, this::mapBook, isbn
        );

        return results.isEmpty() ? null : results.get(0);
    }

    @Transactional
    public void addBook(Book book) {

        validate(book);

        if (searchBook(book.getIsbn()) != null) {
            throw new IllegalArgumentException(
                    "ISBN already exists."
            );
        }

        String itemSql = """
                INSERT INTO items (price, stock, item_type)
                VALUES (?, ?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {

            PreparedStatement ps =
                    connection.prepareStatement(
                            itemSql,
                            Statement.RETURN_GENERATED_KEYS
                    );

            ps.setBigDecimal(1, book.getPrice());
            ps.setInt(2, book.getQuantity());
            ps.setString(3, "BOOK");

            return ps;

        }, keyHolder);

        Number generatedId = keyHolder.getKey();

        if (generatedId == null) {
            throw new IllegalStateException(
                    "Failed to generate item ID."
            );
        }

        String bookSql = """
                INSERT INTO books
                (id, isbn, title, author, category,
                 quantity, cover_image_url)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        jdbcTemplate.update(
                bookSql,
                generatedId.longValue(),
                book.getIsbn(),
                book.getTitle(),
                book.getAuthor(),
                book.getCategory(),
                book.getQuantity(),
                getImageUrl(book)
        );
    }

    @Transactional
    public void updateBook(Book book) {

        validate(book);

        Book existing = searchBook(book.getIsbn());

        if (existing == null) {
            throw new IllegalArgumentException(
                    "Book not found."
            );
        }

        if (book.getImageName() == null ||
                book.getImageName().isBlank()) {
            book.setImageName(existing.getImageName());
        }

        String bookSql = """
                UPDATE books
                SET title = ?,
                    author = ?,
                    category = ?,
                    quantity = ?,
                    cover_image_url = ?
                WHERE isbn = ?
                """;

        jdbcTemplate.update(
                bookSql,
                book.getTitle(),
                book.getAuthor(),
                book.getCategory(),
                book.getQuantity(),
                getImageUrl(book),
                book.getIsbn()
        );

        String itemSql = """
                UPDATE items
                SET price = ?, stock = ?
                WHERE id = (
                    SELECT id FROM books WHERE isbn = ?
                )
                """;

        int rows = jdbcTemplate.update(
                itemSql,
                book.getPrice(),
                book.getQuantity(),
                book.getIsbn()
        );

        if (rows == 0) {
            throw new IllegalStateException(
                    "Matching item not found."
            );
        }
    }

    @Transactional
    public void deleteBook(String isbn) {

        // Keep the parent item if another module
        // depends on it. Delete only the book record.
        jdbcTemplate.update(
                "DELETE FROM books WHERE isbn = ?",
                isbn
        );
    }

    private String getImageUrl(Book book) {

        String imageName = book.getImageName();

        if (imageName == null || imageName.isBlank()) {
            return null;
        }

        return "/book-covers/" + imageName;
    }

    private void validate(Book book) {

        if (book.getIsbn() == null ||
                book.getIsbn().isBlank()) {
            throw new IllegalArgumentException(
                    "ISBN is required."
            );
        }

        if (book.getTitle() == null ||
                book.getTitle().isBlank()) {
            throw new IllegalArgumentException(
                    "Title is required."
            );
        }

        if (book.getAuthor() == null ||
                book.getAuthor().isBlank()) {
            throw new IllegalArgumentException(
                    "Author is required."
            );
        }

        if (book.getCategory() == null ||
                book.getCategory().isBlank()) {
            throw new IllegalArgumentException(
                    "Category is required."
            );
        }

        if (book.getQuantity() < 0) {
            throw new IllegalArgumentException(
                    "Quantity cannot be negative."
            );
        }

        if (book.getPrice() == null ||
                book.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Price cannot be negative."
            );
        }
    }
}
