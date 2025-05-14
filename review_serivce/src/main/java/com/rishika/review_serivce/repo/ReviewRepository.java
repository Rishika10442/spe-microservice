package com.rishika.review_serivce.repo;


import com.rishika.review_serivce.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Optional<Review> findByUserIdAndBookId(Long userId, Long bookId);
    List<Review> findByBookId(Long bookId);

    // You can add custom queries here if needed, e.g., for fetching reviews by book or user
}
