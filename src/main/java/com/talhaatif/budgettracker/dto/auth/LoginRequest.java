package com.talhaatif.budgettracker.dto.auth;

import jakarta.validation.constraints.*;

public record LoginRequest(

        @NotBlank(message = "UserName cannot be blank")
        String userName,

        @NotBlank(message = "Password cannot be blank")
        @Size(min = 6, max = 40, message = "Password must be between 6 and 40 characters")
        String password
) {}