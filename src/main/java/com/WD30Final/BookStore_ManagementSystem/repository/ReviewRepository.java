package com.WD30Final.BookStore_ManagementSystem.repository;

import com.WD30Final.BookStore_ManagementSystem.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByCustomerEmailIgnoreCaseOrderByCreatedAtDesc(String customerEmail);

    List<Review> findAllByOrderByCreatedAtDesc();

    long countByCustomerEmailIgnoreCase(String customerEmail);
}
