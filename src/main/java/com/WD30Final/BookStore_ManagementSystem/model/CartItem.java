package com.WD30Final.BookStore_ManagementSystem.model;

import com.WD30Final.BookStore_ManagementSystem.entity.Book;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private Book book;
    private int quantity;

    public BigDecimal getSubtotal() {
        if (book == null || book.getPrice() == null) {
            return BigDecimal.ZERO;
        }
        return book.getPrice().multiply(BigDecimal.valueOf(quantity));
    }
}