package com.WD30Final.BookStore_ManagementSystem.services;

import com.WD30Final.BookStore_ManagementSystem.dto.WishlistItemForm;
import com.WD30Final.BookStore_ManagementSystem.entity.Book;
import com.WD30Final.BookStore_ManagementSystem.entity.WishlistItem;

import java.util.List;

public interface WishlistService {
    List<WishlistItem> getWishlist(String email, String type, String sort);
    WishlistItem getItem(String email, Long itemId);
    List<Book> getBooksNotInWishlist(String email);
    long count(String email);
    WishlistItem addItem(String email, WishlistItemForm form);
    boolean toggle(String email, Long bookId);
    WishlistItem updateItem(String email, Long itemId, WishlistItemForm form);
    void removeItem(String email, Long itemId);
    void clear(String email);
}
