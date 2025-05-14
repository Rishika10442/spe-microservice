package com.rishika.user_service.helper;

import com.rishika.user_service.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public String getUsername() {
        return user.getEmail(); // Spring uses this as the username
    }

    @Override
    public String getPassword() {
        return user.getPassword(); // Already encrypted if using bcrypt
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Implement roles if needed, or return an empty list
        // return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
}