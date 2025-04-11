package com.talhaatif.budgettracker.dto.auth;

import jakarta.validation.constraints.*;

public record UpdateRequest(
        @NotBlank(message = "First name cannot be blank")
        String firstName,

        @NotBlank(message = "Last name cannot be blank")
        String lastName,

        String phoneNumber
) {}