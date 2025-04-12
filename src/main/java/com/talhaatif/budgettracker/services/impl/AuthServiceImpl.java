package com.talhaatif.budgettracker.services.impl;

import com.talhaatif.budgettracker.dto.auth.*;
import com.talhaatif.budgettracker.entities.RefreshToken;
import com.talhaatif.budgettracker.entities.User;
import com.talhaatif.budgettracker.exceptions.TokenRefreshException;
import com.talhaatif.budgettracker.security.JwtUtil;
import com.talhaatif.budgettracker.services.AuthService;
import com.talhaatif.budgettracker.services.RefreshTokenService;
import com.talhaatif.budgettracker.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.Token;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;

    @Override
    public ResponseEntity<TokenResponse> authenticateUser(LoginRequest loginRequest) {
        try {
            // Log the attempt
            log.info("Attempting authentication for user: {}", loginRequest.userName());

            // 1. First try authentication
            Authentication authentication;
            try {
                authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                loginRequest.userName(),
                                loginRequest.password()
                        )
                );
                log.info("Basic authentication successful for user: {}", loginRequest.userName());
            } catch (Exception e) {
                log.error("Authentication failed for user: {}", loginRequest.userName(), e);
                throw new RuntimeException("Authentication failed: " + e.getMessage(), e);
            }

            // 2. Verify the principal
            User user;
            try {
                user = (User) authentication.getPrincipal();
                log.info("User principal retrieved: {}", user.getUsername());
            } catch (ClassCastException e) {
                log.error("Principal is not of type User", e);
                throw new RuntimeException("Invalid user principal type", e);
            } catch (Exception e) {
                log.error("Failed to get user principal", e);
                throw new RuntimeException("Failed to retrieve user details", e);
            }

            // 3. Check authorities/roles
            String role;
            try {
                Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
                log.info("User authorities: {}", authorities);

                if (authorities == null || authorities.isEmpty()) {
                    log.error("No authorities found for user: {}", user.getUsername());
                    throw new RuntimeException("User has no roles assigned");
                }

                role = authorities.stream()
                        .findFirst()
                        .orElseThrow(() -> {
                            log.error("No roles found in authorities");
                            return new RuntimeException("No roles found");
                        })
                        .getAuthority();

                log.info("Extracted role: {}", role);
            } catch (Exception e) {
                log.error("Failed to process user roles", e);
                throw new RuntimeException("Role processing failed: " + e.getMessage(), e);
            }

            // 4. Token generation
            try {
                String accessToken = jwtUtil.generateAccessToken(user.getUsername(), role);
                RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

                log.info("Successfully generated tokens for user: {}", user.getUsername());

                return ResponseEntity.ok(new TokenResponse(
                        accessToken,
                        refreshToken.getToken(),
                        "Bearer",
                        jwtUtil.getAccessTokenExpiration()
                ));
            } catch (Exception e) {
                log.error("Token generation failed", e);
                throw new RuntimeException("Token generation failed: " + e.getMessage(), e);
            }

        } catch (RuntimeException e) {
            // This will catch all our custom exceptions
            log.error("Authentication process failed completely", e);
            throw e;
        }
    }
    @Override
    public ResponseEntity<TokenResponse> refreshToken(String refreshToken) {
        return refreshTokenService.findByToken(refreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String role = user.getRoles().stream()
                            .findFirst()
                            .orElse("USER");

                    String newAccessToken = jwtUtil.generateAccessToken(user.getUsername(), role);
                    return ResponseEntity.ok(new TokenResponse(
                            newAccessToken,
                            refreshToken,
                            "Bearer",
                            jwtUtil.getAccessTokenExpiration()
                    ));
                })
                .orElseThrow(() -> new TokenRefreshException(refreshToken, "Refresh token is not in database"));
    }


    @Override
    public ResponseEntity<UserResponse> registerUser(SignupRequest signupRequest) {

        if (userService.existsByEmail(signupRequest.email())) {
            throw new IllegalArgumentException("Email already in use");
        }

        // Convert SignupRequest to User entity manually
        User user = new User();
        user.setUserName(signupRequest.userName());
        user.setEmail(signupRequest.email());
        user.setPassword(passwordEncoder.encode(signupRequest.password()));
        user.setFirstName(signupRequest.firstName());
        user.setLastName(signupRequest.lastName());

        // Set default role if none provided
        Set<String> roles = signupRequest.roles();
        if (roles == null || roles.isEmpty()) {
            roles = Collections.singleton("ROLE_USER");
        }
        user.setRoles(roles);

        User savedUser = userService.register(user);

        // Convert User to UserResponse manually
        UserResponse response = new UserResponse(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getUsername(),
                savedUser.getFirstName(),
                savedUser.getLastName(),
                savedUser.getRoles(),
                savedUser.getCreatedAt(),
                savedUser.getUpdatedAt()
        );

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<UserResponse> updateUserProfile(UpdateRequest updateRequest) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Update user fields manually
        user.setFirstName(updateRequest.firstName());
        user.setLastName(updateRequest.lastName());
        // Update other fields as needed

        User updatedUser = userService.register(user);

        // Convert to response
        UserResponse response = new UserResponse(
                updatedUser.getId(),
                updatedUser.getEmail(),
                updatedUser.getUsername(),
                updatedUser.getFirstName(),
                updatedUser.getLastName(),
                updatedUser.getRoles(),
                updatedUser.getCreatedAt(),
                updatedUser.getUpdatedAt()
        );

        return ResponseEntity.ok(response);
    }

    @Override
    @Transactional
    public void logoutUser(String email) {
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Delete all refresh tokens for this user
        refreshTokenService.deleteByUserId(user.getId());

        // i can use it for as well
        // - Tracking logout events
        // - Clearing any cached user data
        // - Publishing a logout event
    }
}