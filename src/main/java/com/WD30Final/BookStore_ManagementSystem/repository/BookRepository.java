package com.WD30Final.BookStore_ManagementSystem.repository;

import com.WD30Final.BookStore_ManagementSystem.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    boolean existsByIsbn(String isbn);

    @Query("SELECT DISTINCT b.category FROM Book b WHERE b.category IS NOT NULL AND b.category != ''")
    List<String> findAllCategories();

    @Query("SELECT b FROM Book b WHERE " +
            "(:query IS NULL OR :query = '' OR " +
            " LOWER(b.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            " LOWER(b.author) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            " LOWER(b.isbn) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
            "(:category IS NULL OR :category = '' OR b.category = :category)")
    Page<Book> searchBooks(@Param("query") String query,
                           @Param("category") String category,
                           Pageable pageable);
}