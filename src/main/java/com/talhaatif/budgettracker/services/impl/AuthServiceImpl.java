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
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;

    @Override
    public ResponseEntity<?> authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.email(),
                        loginRequest.password()
                )
        );

        User user = (User) authentication.getPrincipal();
        String role = user.getAuthorities().stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Role not found"))
                .getAuthority();

        String accessToken = jwtUtil.generateAccessToken(user.getEmail(), role);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return ResponseEntity.ok(new TokenResponse(
                accessToken,
                refreshToken.getToken(),
                "Bearer",
                jwtUtil.getAccessTokenExpiration()
        ));
    }

    @Override
    public ResponseEntity<?> refreshToken(String refreshToken) {
        return refreshTokenService.findByToken(refreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String role = user.getRoles().stream()
                            .findFirst()
                            .orElse("USER");

                    String newAccessToken = jwtUtil.generateAccessToken(user.getEmail(), role);
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
                updatedUser.getFirstName(),
                updatedUser.getLastName(),
                updatedUser.getRoles(),
                updatedUser.getCreatedAt(),
                updatedUser.getUpdatedAt()
        );

        return ResponseEntity.ok(response);
    }
}