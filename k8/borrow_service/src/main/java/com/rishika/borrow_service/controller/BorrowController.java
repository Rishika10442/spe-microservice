package com.rishika.borrow_service.controller;


import com.rishika.borrow_service.service.BorrowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/borrow")
public class BorrowController {
    @Autowired
    private BorrowService borrowService;

    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addBorrow(
            @RequestBody Map<String, Long> request,
            @RequestHeader("Authorization") String token) {

        Long userId = request.get("userId");
        Long bookId = request.get("bookId");

        if (userId == null || bookId == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "userId and bookId are required");
            return ResponseEntity.badRequest().body(error);
        }

        Map<String, Object> result = borrowService.addBorrow(userId, bookId, token);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getBorrowsByUser(
            @PathVariable Long userId,
            @RequestHeader("Authorization") String jwtToken) {
        Map<String, Object> result = borrowService.getBorrowsByUser(userId, jwtToken);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/return")
    public ResponseEntity<Map<String, Object>> returnBook(@RequestBody Map<String, Long> request) {
        Long userId = request.get("userId");
        Long bookId = request.get("bookId");

        Map<String, Object> response = borrowService.returnBook(userId, bookId);
        if ((boolean) response.get("success")) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/test")
    public String test() {
        return "Borrow Service is Working!";
    }
}
