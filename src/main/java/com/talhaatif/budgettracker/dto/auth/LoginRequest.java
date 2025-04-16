package com.talhaatif.budgettracker.dto.auth;

import jakarta.validation.constraints.*;

public record LoginRequest(

        @NotBlank(message = "Username cannot be blank")
        @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
        @Pattern(
                regexp = "^[a-zA-Z0-9](?:[a-zA-Z0-9._-]*[a-zA-Z0-9])?$",
                message = "Username must start and end with alphanumeric characters. Only '.', '_', and '-' are allowed as special characters in between"
        )
        String userName,
        @NotBlank(message = "Password cannot be blank")
        @Size(min = 6, max = 40, message = "Password must be between 6 and 40 characters")
        String password
) {}