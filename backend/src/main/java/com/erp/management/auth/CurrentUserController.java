package com.erp.management.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class CurrentUserController {
    private final AppUserRepository users;

    @GetMapping("/me")
    public CurrentUserResponse me(Authentication authentication) {
        AppUser user = users.findByUsernameIgnoreCase(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Authenticated user no longer exists"));
        return new CurrentUserResponse(user.getId(), user.getUsername(), user.getRole().name(), user.isEnabled());
    }

    public record CurrentUserResponse(Long id, String username, String role, boolean enabled) {}
}
