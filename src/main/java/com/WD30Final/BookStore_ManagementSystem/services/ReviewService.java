package com.WD30Final.BookStore_ManagementSystem.services;

import com.WD30Final.BookStore_ManagementSystem.dto.ReviewDto;

import java.util.List;

/**
 * OOP PRINCIPLE 3 - ABSTRACTION
 * Controllers depend on this interface (WHAT can be done),
 * not on ReviewServiceImpl (HOW it is done).
 * The implementation can change without touching any controller.
 */
public interface ReviewService {

    // ---- customer side ----
    ReviewDto create(ReviewDto dto, String customerEmail, String customerName);

    List<ReviewDto> getMyReviews(String customerEmail);

    ReviewDto getMyReview(Long id, String customerEmail);

    ReviewDto update(Long id, ReviewDto dto, String customerEmail);

    void deleteMine(Long id, String customerEmail);

    long countMine(String customerEmail);

    // ---- admin side ----
    List<ReviewDto> getAllReviews();

    void deleteAny(Long id);
}
