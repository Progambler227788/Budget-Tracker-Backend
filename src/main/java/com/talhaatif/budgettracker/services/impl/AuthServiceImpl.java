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
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

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
            log.info("Attempting authentication for user: {}", loginRequest.userName());

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.userName(),
                            loginRequest.password()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            User user = (User) authentication.getPrincipal();
            log.info("User authenticated: {}", user.getUsername());

            // Handle multiple roles
            String roles = user.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(",")); // Join multiple roles with comma

            String accessToken = jwtUtil.generateAccessToken(user.getUsername(), roles);
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

            return ResponseEntity.ok(new TokenResponse(
                    accessToken,
                    refreshToken.getToken(),
                    "Bearer",
                    jwtUtil.getAccessTokenExpiration()
            ));

        } catch (Exception e) {
            log.error("Authentication failed for user: {}", loginRequest.userName(), e);
            throw new RuntimeException("Authentication failed: " + e.getMessage(), e);
        }
    }

    @Override
    public ResponseEntity<TokenResponse> refreshToken(String refreshToken) {
        return refreshTokenService.findByToken(refreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String roles = user.getRoles().stream()
                            .collect(Collectors.joining(","));

                    String newAccessToken = jwtUtil.generateAccessToken(user.getUsername(), roles);
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
        List<String> roles = signupRequest.roles();
        if (roles == null || roles.isEmpty()) {
            user.setRoles(Arrays.asList("USER"));
        }
        else {
            user.setRoles(roles);
        }

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
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUserName(userName);


        // Update user fields
        user.setFirstName(updateRequest.firstName());
        user.setLastName(updateRequest.lastName());

        User updatedUser = userService.updateUser(user);

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
    public void logoutUser(String username) {
        User user = userService.findByUserName(username);

        // Delete all refresh tokens for this user
        refreshTokenService.deleteByUserId(user.getId());

        // i can use it for as well
        // - Tracking logout events
        // - Clearing any cached user data
        // - Publishing a logout event
    }
}