package com.erp.management.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AppUserRepository users;
    private final PasswordEncoder passwordEncoder;

    public AppUser create(String username, String password, Role role) {
        if (users.findByUsernameIgnoreCase(username).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }
        return users.save(AppUser.builder()
                .username(username.trim())
                .passwordHash(passwordEncoder.encode(password))
                .role(role)
                .enabled(true)
                .build());
    }
}
