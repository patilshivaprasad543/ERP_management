package com.erp.management.auth;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class LoginController {
    private final AuthenticationManager authenticationManager;
    private final AppUserRepository users;

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(request.username(), request.password()));
        AppUser user = users.findByUsernameIgnoreCase(authentication.getName()).orElseThrow();
        return new LoginResponse(user.getUsername(), user.getRole().name(), "Authenticated session; JWT can be layered on this boundary.");
    }

    public record LoginRequest(@NotBlank String username, @NotBlank String password) {}
    public record LoginResponse(String username, String role, String message) {}
}
