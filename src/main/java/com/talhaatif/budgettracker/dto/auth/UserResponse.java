package com.talhaatif.budgettracker.dto.auth;

import java.time.LocalDateTime;
import java.util.Set;

public record UserResponse(
        String id,
        String email,
        String firstName,
        String lastName,
        Set<String> roles,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}