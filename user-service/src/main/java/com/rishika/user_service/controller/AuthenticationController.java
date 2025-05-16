package com.rishika.user_service.controller;

import com.rishika.user_service.dto.LoginRequest;
import com.rishika.user_service.dto.UserRequest;
import com.rishika.user_service.entity.User;
import com.rishika.user_service.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final UserService userService;

    @GetMapping("/health")
    public String checkHealth() {
        return "Server is up and running!";
    }

    @PostMapping("/signup")
    public ResponseEntity<Map<String, Object>> signup(@Valid @RequestBody UserRequest userRequest) {
        Map<String, Object> response = userService.createUser(userRequest);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody @Valid LoginRequest request) {
        Map<String, Object> response =userService.login(request);
        return ResponseEntity.ok(response);
    }
}