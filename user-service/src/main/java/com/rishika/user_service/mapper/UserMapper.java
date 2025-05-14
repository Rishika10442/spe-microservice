package com.rishika.user_service.mapper;

import com.rishika.user_service.dto.UserRequest;
import com.rishika.user_service.entity.User;
import org.springframework.stereotype.Service;

@Service
public class UserMapper {
    public User toEntity(UserRequest request, String encryptedPassword) {
        return User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .password(request.password())
                .encryptedpassword(encryptedPassword)
                .build();


    }
}