
package com.WD30Final.BookStore_ManagementSystem.controller;

import com.WD30Final.BookStore_ManagementSystem.model.Book;
import com.WD30Final.BookStore_ManagementSystem.model.PrintedBook;
import com.WD30Final.BookStore_ManagementSystem.service.BookService;
import com.WD30Final.BookStore_ManagementSystem.service.BookImageService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.AnonymousAuthenticationToken;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Controller
public class BookController {

    private final BookService bookService;
    private final BookImageService imageService;

    public BookController(
            BookService bookService,
            BookImageService imageService) {

        this.bookService = bookService;
        this.imageService = imageService;
    }

//LOGIN



    //======================
    //HOME

    @GetMapping("/")
    public String homepage() {
        return "redirect:/books";
    }

    // =========================================
    // 1. VIEW ALL BOOKS - ADMIN
    // =========================================

    @GetMapping({"/admin/books", "/admin/inventory"})
    public String listBooks(Model model) {

        model.addAttribute(
                "books",
                bookService.viewBooks()
        );

        return "book-list";
    }

    // =========================================
    // 2. SHOW ADD BOOK FORM
    // =========================================

    @GetMapping("/admin/books/add")
    public String showAddForm() {

        return "add-book";
    }

    // =========================================
    // 3. ADD NEW BOOK WITH PRICE AND IMAGE
    // =========================================

    @PostMapping("/admin/books/add")
    public String addBook(
            @RequestParam("isbn") String isbn,
            @RequestParam("title") String title,
            @RequestParam("author") String author,
            @RequestParam("category") String category,
            @RequestParam("quantity") int quantity,
            @RequestParam("price") BigDecimal price,
            @RequestParam(
                    value = "image",
                    required = false
            ) MultipartFile image) {

        Book book = new PrintedBook(
                isbn,
                title,
                author,
                category,
                quantity
        );

        book.setPrice(price);

        if (image != null && !image.isEmpty()) {

            String imageName = imageService.save(image);

            book.setImageName(imageName);
        }

        bookService.addBook(book);

        return "redirect:/admin/books";
    }

    // =========================================
    // 4. SHOW EDIT BOOK FORM
    // =========================================

    @GetMapping("/admin/books/edit/{isbn}")
    public String showEditForm(
            @PathVariable("isbn") String isbn,
            Model model) {

        Book book = bookService.searchBook(isbn);

        if (book == null) {

            return "redirect:/admin/books";
        }

        model.addAttribute("book", book);

        return "edit-book";
    }

    // =========================================
    // 5. UPDATE BOOK WITH PRICE AND IMAGE
    // =========================================

    @PostMapping("/admin/books/edit/{isbn}")
    public String updateBook(
            @PathVariable("isbn") String isbn,
            @RequestParam("title") String title,
            @RequestParam("author") String author,
            @RequestParam("category") String category,
            @RequestParam("quantity") int quantity,
            @RequestParam("price") BigDecimal price,
            @RequestParam(
                    value = "image",
                    required = false
            ) MultipartFile image) {

        Book book = new PrintedBook(
                isbn,
                title,
                author,
                category,
                quantity
        );

        book.setPrice(price);

        // Only replace image if a new one is uploaded.
        // BookService preserves the existing image
        // when imageName is empty.

        if (image != null && !image.isEmpty()) {

            String imageName = imageService.save(image);

            book.setImageName(imageName);
        }

        bookService.updateBook(book);

        return "redirect:/admin/books";
    }

    // =========================================
    // 6. DELETE BOOK
    // =========================================

    @PostMapping("/admin/books/delete/{isbn}")
    public String deleteBook(
            @PathVariable("isbn") String isbn) {

        bookService.deleteBook(isbn);

        return "redirect:/admin/books";
    }

    // =========================================
    // 7. CUSTOMER BOOK CATALOG
    // =========================================

    @GetMapping("/books")
    public String customerBooks(
            Model model,
            Authentication authentication) {

        model.addAttribute("books", bookService.viewBooks());

        boolean loggedIn =
                authentication != null
                        && authentication.isAuthenticated()
                        && !(authentication.getPrincipal()
                        instanceof org.springframework.security
                        .authentication.AnonymousAuthenticationToken);

        model.addAttribute("loggedIn", loggedIn);

        if (loggedIn) {
            model.addAttribute(
                    "username",
                    authentication.getName()
            );
        }

        return "customer-books";
    }
    @GetMapping("/books/{isbn}")
    public String bookDetails(
            @PathVariable String isbn,
            Model model) {

        Book book = bookService.searchBook(isbn);

        if (book == null) {
            return "redirect:/books";
        }

        model.addAttribute("book", book);

        return "book-details";
    }

package com.WD30Final.BookStore_ManagementSystem.controller;

import com.WD30Final.BookStore_ManagementSystem.entity.Book;
import com.WD30Final.BookStore_ManagementSystem.repository.BookRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Controller
@RequestMapping("/books")
public class BookController {

    private final BookRepository bookRepository;

    public BookController(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("books", bookRepository.findAll());
        return "books";
    }

    @GetMapping("/view/{id}")
    public String view(@PathVariable Long id, Model model) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Book not found"));
        model.addAttribute("book", book);
        return "book-details";
    }
}
