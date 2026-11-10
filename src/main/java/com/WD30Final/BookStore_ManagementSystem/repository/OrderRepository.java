package com.WD30Final.BookStore_ManagementSystem.repository;

import com.WD30Final.BookStore_ManagementSystem.entity.BookOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<BookOrder, Long> {
    List<BookOrder> findByUserId(Long userId);
}