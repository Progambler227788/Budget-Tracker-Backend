package com.talhaatif.budgettracker.dto.auth;


import jakarta.validation.constraints.NotBlank;

public record TokenRefreshRequest(
        @NotBlank(message = "Refresh token cannot be blank")
        String refreshToken
) {}
