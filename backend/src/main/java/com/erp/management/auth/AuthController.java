package com.erp.management.auth;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService service;

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse create(@Valid @RequestBody CreateUser request) {
        AppUser user = service.create(request.username(), request.password(), request.role());
        return new UserResponse(user.getId(), user.getUsername(), user.getRole(), user.isEnabled());
    }

    public record CreateUser(
            @NotBlank @Size(max = 80) String username,
            @NotBlank @Size(min = 8, max = 100) String password,
            @NotNull Role role) {}

    public record UserResponse(Long id, String username, Role role, boolean enabled) {}
}
