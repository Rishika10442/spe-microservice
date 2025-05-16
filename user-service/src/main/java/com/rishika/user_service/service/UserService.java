package com.rishika.user_service.service;

import com.rishika.user_service.dto.LoginRequest;
import com.rishika.user_service.dto.UserRequest;
import com.rishika.user_service.entity.User;
import com.rishika.user_service.filter.JWTFilter;
import com.rishika.user_service.helper.CustomUserDetails;
import com.rishika.user_service.helper.JWTHelper;
import com.rishika.user_service.mapper.UserMapper;
import com.rishika.user_service.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepo repo;
    private final UserMapper mapper;
    @Autowired
    private final BCryptPasswordEncoder passwordEncoder;
    private static final Logger logger = LoggerFactory.getLogger(JWTFilter.class);
    private final JWTHelper jwtHelper;

    public Map<String, Object> createUser(UserRequest request) {
        // Check if user already exists
        if (repo.findByEmail(request.email()).isPresent()) {
            return Map.of(
                    "success", false,
                    "message", "User already registered. Please log in."
            );
        }

        // Encrypt the password
        String encryptedPassword = passwordEncoder.encode(request.password());

        // Create and save the user
        User user = mapper.toEntity(request, encryptedPassword);
        repo.save(user);

        // Generate JWT token after signup
        String token = jwtHelper.generateToken(request.email());
        int userId = user.getUserId();

        // Return JSON response with success flag and token
        return Map.of(
                "success", true,
                "token", token,
                "userId",userId,
                "message", "User created and logged in successfully."
        );
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.debug("Loading user details for username: {}", username);
        User user = repo.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
        return new CustomUserDetails(user); // Wrapping Customer in CustomUserDetails
    }

    public Map<String, Object> login(LoginRequest request) {
        User user =repo.findByEmail(request.email())
                .orElseThrow(() -> new UsernameNotFoundException("Employee not found with email: " + request.email()));
        boolean matches = passwordEncoder.matches(request.password(), user.getEncryptedpassword());

        if(!matches){
            return Map.of(
                    "success", false,
                    "message", "Wrong email or password."
            );
        }
        String token =jwtHelper.generateToken(request.email());
        int userId=user.getUserId();

        return Map.of(
                "success", true,
                "token", token,
                "userId",userId,
                "message", "User created and logged in successfully."
        );
    }
}


