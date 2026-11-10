package com.WD30Final.BookStore_ManagementSystem.controller;

import com.WD30Final.BookStore_ManagementSystem.dto.ReviewDto;
import com.WD30Final.BookStore_ManagementSystem.services.ReviewService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Customer side: create / read / update / delete OWN reviews. */
@Controller
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    // READ (list + empty create form)
    @GetMapping
    public String list(Authentication auth, Model model) {
        model.addAttribute("reviews", reviewService.getMyReviews(auth.getName()));
        model.addAttribute("reviewDto", new ReviewDto());
        model.addAttribute("editing", false);
        return "customer/reviews";
    }

    // READ one -> fill the form for editing
    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Authentication auth, Model model) {
        model.addAttribute("reviews", reviewService.getMyReviews(auth.getName()));
        model.addAttribute("reviewDto", reviewService.getMyReview(id, auth.getName()));
        model.addAttribute("editing", true);
        return "customer/reviews";
    }

    // CREATE
    @PostMapping
    public String create(@Valid @ModelAttribute("reviewDto") ReviewDto dto, BindingResult result,
                         Authentication auth, Model model, RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("reviews", reviewService.getMyReviews(auth.getName()));
            model.addAttribute("editing", false);
            return "customer/reviews";
        }
        // display name: use your own User/Customer name here if you load it; email is the safe fallback
        reviewService.create(dto, auth.getName(), auth.getName());
        ra.addFlashAttribute("success", "Review added");
        return "redirect:/reviews";
    }

    // UPDATE
    @PostMapping("/{id}/update")
    public String update(@PathVariable Long id, @Valid @ModelAttribute("reviewDto") ReviewDto dto,
                         BindingResult result, Authentication auth, Model model, RedirectAttributes ra) {
        if (result.hasErrors()) {
            dto.setId(id);
            model.addAttribute("reviews", reviewService.getMyReviews(auth.getName()));
            model.addAttribute("editing", true);
            return "customer/reviews";
        }
        reviewService.update(id, dto, auth.getName());
        ra.addFlashAttribute("success", "Review updated");
        return "redirect:/reviews";
    }

    // DELETE
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, Authentication auth, RedirectAttributes ra) {
        reviewService.deleteMine(id, auth.getName());
        ra.addFlashAttribute("success", "Review deleted");
        return "redirect:/reviews";
    }
}
