package com.WD30Final.BookStore_ManagementSystem.services;

import com.WD30Final.BookStore_ManagementSystem.entity.Book;
import com.WD30Final.BookStore_ManagementSystem.entity.Order;
import com.WD30Final.BookStore_ManagementSystem.entity.OrderItem;
import com.WD30Final.BookStore_ManagementSystem.model.Cart;
import com.WD30Final.BookStore_ManagementSystem.repository.BookRepository;
import com.WD30Final.BookStore_ManagementSystem.repository.OrderRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CartService {

    public static final String CART_SESSION_KEY = "SHOPPING_CART";
    private final BookRepository bookRepository;
    private final OrderRepository orderRepository;

    public Cart getCart(HttpSession session) {
        Cart cart = (Cart) session.getAttribute(CART_SESSION_KEY);
        if (cart == null) {
            cart = new Cart();
            session.setAttribute(CART_SESSION_KEY, cart);
        }
        return cart;
    }

    public void addBookToCart(HttpSession session, Long bookId, int quantity) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found with ID: " + bookId));
        if (book.isOutOfStock()) {
            throw new IllegalStateException("Book is currently out of stock!");
        }
        Cart cart = getCart(session);
        cart.addItem(book, quantity);
    }

    public void updateCartItem(HttpSession session, Long bookId, int quantity) {
        Cart cart = getCart(session);
        cart.updateQuantity(bookId, quantity);
    }

    public void removeCartItem(HttpSession session, Long bookId) {
        Cart cart = getCart(session);
        cart.removeItem(bookId);
    }

    @Transactional
    public Order checkout(HttpSession session, String customerEmail, String customerName) {
        Cart cart = getCart(session);
        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("Your cart is empty.");
        }

        Order order = new Order(customerEmail, customerName);

        for (var item : cart.getItems()) {
            Book book = bookRepository.findById(item.getBook().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Book not found with ID: " + item.getBook().getId()));

            if (book.getStock() < item.getQuantity()) {
                throw new IllegalStateException("Not enough stock available for: " + book.getTitle());
            }

            book.setStock(book.getStock() - item.getQuantity());
            bookRepository.save(book);

            OrderItem orderItem = new OrderItem(order, book, item.getQuantity(), book.getPrice());
            order.addItem(orderItem);
        }

        Order savedOrder = orderRepository.save(order);
        cart.clear();
        return savedOrder;
    }
}