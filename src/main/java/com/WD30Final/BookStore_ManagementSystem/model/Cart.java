package com.WD30Final.BookStore_ManagementSystem.model;

import com.WD30Final.BookStore_ManagementSystem.entity.Book;
import lombok.Getter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class Cart implements Serializable {
    private static final long serialVersionUID = 1L;

    private final List<CartItem> items = new ArrayList<>();

    public void addItem(Book book, int quantity) {
        if (book == null || quantity <= 0) return;

        for (CartItem item : items) {
            if (item.getBook().getId().equals(book.getId())) {
                int newQty = item.getQuantity() + quantity;
                // Enforce stock ceiling
                if (book.getStock() != null && newQty > book.getStock()) {
                    newQty = book.getStock();
                }
                item.setQuantity(newQty);
                return;
            }
        }
        int initialQty = (book.getStock() != null && quantity > book.getStock()) ? book.getStock() : quantity;
        items.add(new CartItem(book, initialQty));
    }

    public void updateQuantity(Long bookId, int quantity) {
        if (quantity <= 0) {
            removeItem(bookId);
            return;
        }
        for (CartItem item : items) {
            if (item.getBook().getId().equals(bookId)) {
                int maxStock = (item.getBook().getStock() != null) ? item.getBook().getStock() : quantity;
                item.setQuantity(Math.min(quantity, maxStock));
                return;
            }
        }
    }

    public void removeItem(Long bookId) {
        items.removeIf(item -> item.getBook().getId().equals(bookId));
    }

    public void clear() {
        items.clear();
    }

    public int getTotalQuantity() {
        return items.stream().mapToInt(CartItem::getQuantity).sum();
    }

    public BigDecimal getTotalPrice() {
        return items.stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<CartItem> getItems() {
        return Collections.unmodifiableList(items);
    }
}