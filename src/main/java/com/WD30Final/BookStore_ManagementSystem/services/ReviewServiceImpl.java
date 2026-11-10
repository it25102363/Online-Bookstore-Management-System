package com.WD30Final.BookStore_ManagementSystem.services;

import com.WD30Final.BookStore_ManagementSystem.dto.ReviewDto;
import com.WD30Final.BookStore_ManagementSystem.entity.Review;
import com.WD30Final.BookStore_ManagementSystem.repository.ReviewRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Transactional
public class ReviewServiceImpl implements ReviewService {

    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");

    private final ReviewRepository reviewRepository;

    public ReviewServiceImpl(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    // ---------------- CREATE ----------------
    @Override
    public ReviewDto create(ReviewDto dto, String customerEmail, String customerName) {
        Review review = new Review(dto.getBookTitle(), dto.getRating(), dto.getComment(),
                customerEmail, customerName);
        return toDto(reviewRepository.save(review));
    }

    // ---------------- READ ----------------
    @Override
    @Transactional(readOnly = true)
    public List<ReviewDto> getMyReviews(String customerEmail) {
        return reviewRepository.findByCustomerEmailIgnoreCaseOrderByCreatedAtDesc(customerEmail)
                .stream().map(this::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewDto getMyReview(Long id, String customerEmail) {
        return toDto(findOwned(id, customerEmail));
    }

    @Override
    @Transactional(readOnly = true)
    public long countMine(String customerEmail) {
        return reviewRepository.countByCustomerEmailIgnoreCase(customerEmail);
    }

    // ---------------- UPDATE ----------------
    @Override
    public ReviewDto update(Long id, ReviewDto dto, String customerEmail) {
        Review review = findOwned(id, customerEmail);
        review.edit(dto.getBookTitle(), dto.getRating(), dto.getComment());
        return toDto(reviewRepository.save(review));
    }

    // ---------------- DELETE ----------------
    @Override
    public void deleteMine(Long id, String customerEmail) {
        reviewRepository.delete(findOwned(id, customerEmail));
    }

    // ---------------- ADMIN ----------------
    @Override
    @Transactional(readOnly = true)
    public List<ReviewDto> getAllReviews() {
        return reviewRepository.findAllByOrderByCreatedAtDesc()
                .stream().map(this::toDto).toList();
    }

    @Override
    public void deleteAny(Long id) {
        if (!reviewRepository.existsById(id)) {
            throw new IllegalArgumentException("Review not found");
        }
        reviewRepository.deleteById(id);
    }

    // ---------------- helpers ----------------
    /** Customers may only touch their own reviews. */
    private Review findOwned(Long id, String customerEmail) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));
        if (!review.isOwnedBy(customerEmail)) {
            throw new AccessDeniedException("You can only change your own reviews");
        }
        return review;
    }

    private ReviewDto toDto(Review r) {
        ReviewDto dto = new ReviewDto();
        dto.setId(r.getId());
        dto.setBookTitle(r.getBookTitle());
        dto.setRating(r.getRating());
        dto.setComment(r.getComment());
        dto.setCustomerName(r.getCustomerName());
        dto.setCreatedAt(r.getCreatedAt() != null ? r.getCreatedAt().format(FORMAT) : "");
        return dto;
    }
}
