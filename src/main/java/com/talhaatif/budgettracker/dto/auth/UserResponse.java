package com.talhaatif.budgettracker.dto.auth;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public record UserResponse(
        String id,
        String email,
        String userName,
        String firstName,
        String lastName,
        List<String> roles,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}