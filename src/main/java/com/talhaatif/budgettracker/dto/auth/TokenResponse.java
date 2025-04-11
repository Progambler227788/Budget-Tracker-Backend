package com.talhaatif.budgettracker.dto.auth;


public record TokenResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        Long expiresIn
) {}