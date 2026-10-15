package com.WD30Final.BookStore_ManagementSystem.services;

import com.WD30Final.BookStore_ManagementSystem.entity.Book;
import com.WD30Final.BookStore_ManagementSystem.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DemoDataService {

    private final BookRepository bookRepository;

    public int loadPresentationData() {
        List<Book> showcaseBooks = List.of(
                // Software & Backend Engineering
                createBook(
                        "Clean Code: A Handbook of Agile Software Craftsmanship",
                        "Robert C. Martin",
                        "978-0132350884",
                        "Software Engineering",
                        "Best practices for writing readable, maintainable Java code, agile principles, and refactoring techniques.",
                        BigDecimal.valueOf(4800.00),
                        25,
                        4.8,
                        "https://images.unsplash.com/photo-1532012164546-f432f2e3edd4?auto=format&fit=crop&w=400&q=80"
                ),
                createBook(
                        "Spring Boot in Action",
                        "Craig Walls",
                        "978-1617292545",
                        "Software Engineering",
                        "Comprehensive guide to modern backend architecture, Spring MVC, REST APIs, and microservices in enterprise Java.",
                        BigDecimal.valueOf(5200.00),
                        18,
                        4.7,
                        "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?auto=format&fit=crop&w=400&q=80"
                ),
                createBook(
                        "Designing Data-Intensive Applications",
                        "Martin Kleppmann",
                        "978-1449373320",
                        "Software Engineering",
                        "Deep dive into distributed systems, database internals, consistency protocols, transactions, and scalability.",
                        BigDecimal.valueOf(6500.00),
                        12,
                        4.9,
                        "https://images.unsplash.com/photo-1512820790803-83ca734da794?auto=format&fit=crop&w=400&q=80"
                ),

                // Fantasy & Epic Adventure
                createBook(
                        "The Hobbit",
                        "J.R.R. Tolkien",
                        "978-0547928227",
                        "Fantasy",
                        "Classic high fantasy tale following Bilbo Baggins across Middle-earth to face dragons, magic, and epic quests.",
                        BigDecimal.valueOf(2950.00),
                        30,
                        4.8,
                        "https://images.unsplash.com/photo-1621351183012-e2f9972dd9bf?auto=format&fit=crop&w=400&q=80"
                ),
                createBook(
                        "The Name of the Wind",
                        "Patrick Rothfuss",
                        "978-0756404741",
                        "Fantasy",
                        "Heroic fantasy chronicle detailing the life of Kvothe, arcane magic academies, legendary adventures, and myth.",
                        BigDecimal.valueOf(3400.00),
                        15,
                        4.6,
                        "https://images.unsplash.com/photo-1516979187457-637abb4f9353?auto=format&fit=crop&w=400&q=80"
                ),

                // Mystery & Thriller
                createBook(
                        "The Silent Patient",
                        "Alex Michaelides",
                        "978-1250301696",
                        "Mystery",
                        "Shocking psychological thriller about a woman's act of violence against her husband and her refusal to speak.",
                        BigDecimal.valueOf(2800.00),
                        20,
                        4.5,
                        "https://images.unsplash.com/photo-1589829085413-56de8ae18c73?auto=format&fit=crop&w=400&q=80"
                ),
                createBook(
                        "The Da Vinci Code",
                        "Dan Brown",
                        "978-0307474278",
                        "Mystery",
                        "High-speed mystery and detective conspiracy thriller deciphering hidden codes within famous historical artworks.",
                        BigDecimal.valueOf(2450.00),
                        22,
                        4.4,
                        "https://images.unsplash.com/photo-1497633762265-9d179a990aa6?auto=format&fit=crop&w=400&q=80"
                ),

                // Self-Improvement & Productivity
                createBook(
                        "Atomic Habits",
                        "James Clear",
                        "978-0735211292",
                        "Self Improvement",
                        "Proven framework for improving every day through small changes, routine systems, mindset, and behavioral psychology.",
                        BigDecimal.valueOf(3200.00),
                        40,
                        4.9,
                        "https://images.unsplash.com/photo-1506880018603-83d5b814b5a6?auto=format&fit=crop&w=400&q=80"
                ),
                createBook(
                        "Deep Work: Rules for Focused Success",
                        "Cal Newport",
                        "978-1455586691",
                        "Self Improvement",
                        "Actionable guide to cultivating intense focus, eliminating digital distractions, and mastering hard skills quickly.",
                        BigDecimal.valueOf(3100.00),
                        17,
                        4.7,
                        "https://images.unsplash.com/photo-1499750310107-5fef28a66643?auto=format&fit=crop&w=400&q=80"
                )
        );

        bookRepository.saveAll(showcaseBooks);
        return showcaseBooks.size();
    }

    private Book createBook(String title, String author, String isbn, String category,
                            String description, BigDecimal price, int stock, double rating, String coverUrl) {
        Book book = new Book();
        book.setTitle(title);
        book.setAuthor(author);
        book.setIsbn(isbn);
        book.setCategory(category);
        book.setDescription(description);
        book.setPrice(price);
        book.setStock(stock);
        book.setRating(rating);
        book.setCoverImageUrl(coverUrl);
        return book;
    }
}