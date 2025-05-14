package com.rishika.borrow_service.repo;

import com.rishika.borrow_service.entity.Borrow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BorrowRepository extends JpaRepository<Borrow, Long> {
    List<Borrow> findByUserId(Long userId);
    Optional<Borrow> findByUserIdAndBookIdAndIsReturnedFalse(Long userId, Long bookId);
}

