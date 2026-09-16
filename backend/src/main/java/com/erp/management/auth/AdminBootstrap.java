package com.erp.management.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AdminBootstrap implements CommandLineRunner {
    private final AppUserRepository users;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.security.bootstrap.enabled:false}")
    private boolean enabled;

    @Value("${app.security.bootstrap.username:}")
    private String username;

    @Value("${app.security.bootstrap.password:}")
    private String password;

    @Override
    @Transactional
    public void run(String... args) {
        if (!enabled) {
            return;
        }
        if (users.count() > 0) {
            return;
        }
        if (username.isBlank() || password.isBlank()) {
            throw new IllegalStateException("Admin bootstrap is enabled but username/password are not configured");
        }
        if (password.length() < 12) {
            throw new IllegalStateException("Bootstrap admin password must contain at least 12 characters");
        }

        users.save(AppUser.builder()
                .username(username.trim())
                .passwordHash(passwordEncoder.encode(password))
                .role(Role.SUPER_ADMIN)
                .enabled(true)
                .build());
    }
}
