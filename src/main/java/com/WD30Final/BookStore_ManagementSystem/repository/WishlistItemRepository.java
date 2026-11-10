package com.WD30Final.BookStore_ManagementSystem.repository;

import com.WD30Final.BookStore_ManagementSystem.entity.Book;
import com.WD30Final.BookStore_ManagementSystem.entity.User;
import com.WD30Final.BookStore_ManagementSystem.entity.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WishlistItemRepository extends JpaRepository<WishlistItem, Long> {
    List<WishlistItem> findByUser(User user);
    Optional<WishlistItem> findByUserAndBook(User user, Book book);
    Optional<WishlistItem> findByIdAndUser(Long id, User user);
    long countByUser(User user);
    void deleteByUser(User user);
}
