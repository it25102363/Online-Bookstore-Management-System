package com.WD30Final.BookStore_ManagementSystem.services.impl;

import com.WD30Final.BookStore_ManagementSystem.dto.BookDto;
import com.WD30Final.BookStore_ManagementSystem.entity.Book;
import com.WD30Final.BookStore_ManagementSystem.entity.Review;
import com.WD30Final.BookStore_ManagementSystem.repository.BookRepository;
import com.WD30Final.BookStore_ManagementSystem.services.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    /**
     * Helper method to save an uploaded file locally under ./uploads/ directory
     */
    private String saveUploadedImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        try {
            Path uploadPath = Paths.get("uploads");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalName = file.getOriginalFilename();
            String cleanName = (originalName != null) ? Paths.get(originalName).getFileName().toString() : "cover.jpg";
            String uniqueFilename = UUID.randomUUID() + "_" + cleanName;
            Path targetPath = uploadPath.resolve(uniqueFilename);

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
            }
            return "/uploads/" + uniqueFilename;
        } catch (IOException e) {
            throw new RuntimeException("Could not store book cover image: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Book> searchBooks(String query, String category, Pageable pageable) {
        return bookRepository.searchBooks(query, category, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Book getBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Book not found with id: " + id));
    }

    @Override
    @Transactional
    public Book createBook(BookDto bookDto) {
        if (bookRepository.existsByIsbn(bookDto.getIsbn())) {
            throw new IllegalArgumentException("A book with ISBN " + bookDto.getIsbn() + " already exists.");
        }

        // If a file was uploaded, save it and override coverImageUrl
        if (bookDto.getCoverImageFile() != null && !bookDto.getCoverImageFile().isEmpty()) {
            String uploadedUrl = saveUploadedImage(bookDto.getCoverImageFile());
            bookDto.setCoverImageUrl(uploadedUrl);
        }

        return bookRepository.save(bookDto.toEntity());
    }

    @Override
    @Transactional
    public Book updateBook(Long id, BookDto bookDto) {
        Book existing = getBookById(id);
        if (!existing.getIsbn().equals(bookDto.getIsbn()) && bookRepository.existsByIsbn(bookDto.getIsbn())) {
            throw new IllegalArgumentException("ISBN " + bookDto.getIsbn() + " already belongs to another book.");
        }

        existing.setTitle(bookDto.getTitle());
        existing.setAuthor(bookDto.getAuthor());
        existing.setIsbn(bookDto.getIsbn());
        existing.setPrice(bookDto.getPrice());
        existing.setStock(bookDto.getStock());
        existing.setCategory(bookDto.getCategory());
        existing.setDescription(bookDto.getDescription());

        // Check if a new cover file was uploaded
        if (bookDto.getCoverImageFile() != null && !bookDto.getCoverImageFile().isEmpty()) {
            String uploadedUrl = saveUploadedImage(bookDto.getCoverImageFile());
            existing.setCoverImageUrl(uploadedUrl);
        } else if (bookDto.getCoverImageUrl() != null && !bookDto.getCoverImageUrl().trim().isEmpty()) {
            existing.setCoverImageUrl(bookDto.getCoverImageUrl().trim());
        }

        if (bookDto.getRating() != null) {
            existing.setRating(bookDto.getRating());
        }

        return bookRepository.save(existing);
    }

    @Override
    @Transactional
    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new IllegalArgumentException("Cannot delete: Book not found with id: " + id);
        }
        bookRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getAllCategories() {
        return bookRepository.findAllCategories();
    }

    @Override
    @Transactional
    public void addReviewToBook(Long bookId, int rating, String comment, String reviewerName) {
        Book book = getBookById(bookId);
        Review review = new Review(rating, comment, reviewerName, book);
        book.addReview(review); // Calls encapsulated rating calculation in Book entity
        bookRepository.save(book);
    }
}