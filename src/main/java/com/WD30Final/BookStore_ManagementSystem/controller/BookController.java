package com.WD30Final.BookStore_ManagementSystem.controller;

import com.WD30Final.BookStore_ManagementSystem.entity.Book;
import com.WD30Final.BookStore_ManagementSystem.services.AiRecommendationService;
import com.WD30Final.BookStore_ManagementSystem.services.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    private final AiRecommendationService aiRecommendationService;

    @GetMapping
    public String viewStorefront(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "sort", defaultValue = "newest") String sort,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "12") int size,
            Model model) {

        Sort sortOrder = switch (sort) {
            case "priceAsc" -> Sort.by(Sort.Direction.ASC, "price");
            case "priceDesc" -> Sort.by(Sort.Direction.DESC, "price");
            default -> Sort.by(Sort.Direction.DESC, "id");
        };

        Page<Book> bookPage = bookService.searchBooks(query, category, PageRequest.of(page, size, sortOrder));

        model.addAttribute("books", bookPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", bookPage.getTotalPages());
        model.addAttribute("totalBooks", bookPage.getTotalElements());
        model.addAttribute("query", query);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("selectedSort", sort);
        model.addAttribute("categories", bookService.getAllCategories());

        return "books/store";
    }

    @GetMapping("/ai-recommendations")
    public String getAiRecommendations(@RequestParam(value = "prompt", required = false) String prompt,
                                       Model model) {
        if (prompt != null && !prompt.trim().isEmpty()) {
            List<Book> recommendedBooks = aiRecommendationService.getRecommendations(prompt.trim());
            model.addAttribute("books", recommendedBooks);
            model.addAttribute("userPrompt", prompt);
        }
        model.addAttribute("categories", bookService.getAllCategories());
        return "books/ai-recommend";
    }

    @GetMapping("/{id}")
    public String viewBookDetails(@PathVariable Long id, Model model) {
        Book book = bookService.getBookById(id);
        model.addAttribute("book", book);
        return "books/details";
    }

    @PostMapping({"/{id}/review", "/{id}/reviews"})
    public String submitReview(@PathVariable Long id,
                               @RequestParam int rating,
                               @RequestParam String comment,
                               Principal principal,
                               RedirectAttributes redirectAttributes) {
        try {
            String reviewerName = (principal != null) ? principal.getName() : "Anonymous Reader";
            bookService.addReviewToBook(id, rating, comment, reviewerName);
            redirectAttributes.addFlashAttribute("reviewSuccess", "Thank you! Your review has been submitted.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("reviewError", e.getMessage());
        }
        return "redirect:/books/" + id;
    }
}