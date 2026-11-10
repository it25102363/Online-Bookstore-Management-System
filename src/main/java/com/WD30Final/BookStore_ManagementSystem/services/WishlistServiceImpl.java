package com.WD30Final.BookStore_ManagementSystem.services;

import com.WD30Final.BookStore_ManagementSystem.dto.WishlistItemForm;
import com.WD30Final.BookStore_ManagementSystem.entity.Book;
import com.WD30Final.BookStore_ManagementSystem.entity.User;
import com.WD30Final.BookStore_ManagementSystem.entity.WishlistItem;
import com.WD30Final.BookStore_ManagementSystem.repository.BookRepository;
import com.WD30Final.BookStore_ManagementSystem.repository.UserRepository;
import com.WD30Final.BookStore_ManagementSystem.repository.WishlistItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class WishlistServiceImpl implements WishlistService {
    private final WishlistItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    public WishlistServiceImpl(WishlistItemRepository itemRepository,
                               UserRepository userRepository,
                               BookRepository bookRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<WishlistItem> getWishlist(String email, String type, String sort) {
        User user = findUser(email);
        boolean allTypes = type == null || type.isBlank() || type.equalsIgnoreCase("ALL");
        return itemRepository.findByUser(user).stream()
                .filter(item -> allTypes || item.getTypeCode().equalsIgnoreCase(type))
                .sorted(comparatorFor(sort))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public WishlistItem getItem(String email, Long itemId) {
        return findOwnedItem(findUser(email), itemId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Book> getBooksNotInWishlist(String email) {
        User user = findUser(email);
        Set<Long> savedBookIds = itemRepository.findByUser(user).stream()
                .map(item -> item.getBook().getId())
                .collect(Collectors.toSet());
        return bookRepository.findAll().stream()
                .filter(book -> !savedBookIds.contains(book.getId()))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public long count(String email) {
        return itemRepository.countByUser(findUser(email));
    }

    @Override
    @Transactional
    public WishlistItem addItem(String email, WishlistItemForm form) {
        User user = findUser(email);
        if (form.getBookId() == null) throw new IllegalArgumentException("Please choose a book.");
        Book book = bookRepository.findById(form.getBookId())
                .orElseThrow(() -> new IllegalArgumentException("Book not found."));
        if (itemRepository.findByUserAndBook(user, book).isPresent()) {
            throw new IllegalArgumentException("This book is already in your wishlist.");
        }
        WishlistItem item = WishlistItemFactory.create(form.getType());
        item.setUser(user);
        item.setBook(book);
        item.setNote(form.getNote());
        item.applyDetails(form.getTargetPrice(), form.getRecipientName());
        return itemRepository.save(item);
    }

    @Override
    @Transactional
    public boolean toggle(String email, Long bookId) {
        User user = findUser(email);
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found."));
        return itemRepository.findByUserAndBook(user, book)
                .map(existing -> { itemRepository.delete(existing); return false; })
                .orElseGet(() -> {
                    WishlistItem item = WishlistItemFactory.create("STANDARD");
                    item.setUser(user);
                    item.setBook(book);
                    itemRepository.save(item);
                    return true;
                });
    }

    @Override
    @Transactional
    public WishlistItem updateItem(String email, Long itemId, WishlistItemForm form) {
        WishlistItem item = findOwnedItem(findUser(email), itemId);
        item.setNote(form.getNote());
        item.applyDetails(form.getTargetPrice(), form.getRecipientName());
        return itemRepository.save(item);
    }

    @Override
    @Transactional
    public void removeItem(String email, Long itemId) {
        itemRepository.delete(findOwnedItem(findUser(email), itemId));
    }

    @Override
    @Transactional
    public void clear(String email) {
        itemRepository.deleteByUser(findUser(email));
    }

    private User findUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
    }

    private WishlistItem findOwnedItem(User user, Long itemId) {
        return itemRepository.findByIdAndUser(itemId, user)
                .orElseThrow(() -> new IllegalArgumentException("Wishlist item not found."));
    }

    private Comparator<WishlistItem> comparatorFor(String sort) {
        Comparator<WishlistItem> newestFirst = Comparator.comparing(
                WishlistItem::getAddedAt,
                Comparator.nullsLast(Comparator.<LocalDateTime>reverseOrder()));
        String key = sort == null ? "" : sort;
        return switch (key) {
            case "newest" -> newestFirst;
            case "title" -> Comparator.comparing(
                    item -> item.getBook().getTitle() == null ? "" : item.getBook().getTitle(),
                    String.CASE_INSENSITIVE_ORDER);
            case "price_asc" -> Comparator.comparing(
                    item -> item.getBook().getPrice(),
                    Comparator.nullsLast(Comparator.<BigDecimal>naturalOrder()));
            case "price_desc" -> Comparator.comparing(
                    item -> item.getBook().getPrice(),
                    Comparator.nullsLast(Comparator.<BigDecimal>reverseOrder()));
            default -> Comparator.comparingInt(WishlistItem::getPriorityLevel)
                    .reversed().thenComparing(newestFirst);
        };
    }
}
