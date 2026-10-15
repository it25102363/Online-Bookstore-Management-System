package com.WD30Final.BookStore_ManagementSystem.controller;

import com.WD30Final.BookStore_ManagementSystem.entity.Book;
import com.WD30Final.BookStore_ManagementSystem.entity.Review;
import com.WD30Final.BookStore_ManagementSystem.repository.BookRepository;
import com.WD30Final.BookStore_ManagementSystem.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/feedback")
@RequiredArgsConstructor
public class AdminFeedbackController {

    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;

    @GetMapping
    public String listAllFeedback(Model model) {
        List<Review> reviews = reviewRepository.findAllByOrderByIdDesc();
        model.addAttribute("reviews", reviews);
        return "admin/feedback";
    }

    @PostMapping("/delete/{id}")
    public String deleteFeedback(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Review review = reviewRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Review not found with ID: " + id));

            Book book = review.getBook();
            if (book != null) {
                book.getReviews().remove(review);
                // Recalculate average rating if helper method exists
                if (!book.getReviews().isEmpty()) {
                    double avg = book.getReviews().stream().mapToInt(Review::getRating).average().orElse(0.0);
                    book.setRating(avg);
                } else {
                    book.setRating(0.0);
                }
                bookRepository.save(book);
            }

            reviewRepository.delete(review);
            redirectAttributes.addFlashAttribute("successMessage", "Feedback removed successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting feedback: " + e.getMessage());
        }
        return "redirect:/admin/feedback";
    }
}