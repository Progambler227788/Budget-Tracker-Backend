package com.talhaatif.budgettracker.controllers;

import com.talhaatif.budgettracker.dto.auth.*;
import com.talhaatif.budgettracker.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Slf4j
@Tag(name = "Authentication", description = "Authentication API endpoints")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate user", description = "Authenticate user and return JWT tokens")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Authentication request received for user: {}", request.userName());
        return authService.authenticateUser(request);
    }

    @PostMapping("/signup")
    @Operation(summary = "Register new user", description = "Create a new user account")
    public ResponseEntity<UserResponse> signup(@Valid @RequestBody SignupRequest request) {
        return authService.registerUser(request);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh token", description = "Get new access token using refresh token")
    public ResponseEntity<TokenResponse> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        return authService.refreshToken(request.refreshToken());
    }

    @PutMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Update profile", description = "Update authenticated user's profile")
    public ResponseEntity<UserResponse> updateProfile(@Valid @RequestBody UpdateRequest request) {
        return authService.updateUserProfile(request);
    }

    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Logout user", description = "Invalidate refresh token")
    public ResponseEntity<Void> logout() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        authService.logoutUser(email);
        return ResponseEntity.noContent().build();
    }
}