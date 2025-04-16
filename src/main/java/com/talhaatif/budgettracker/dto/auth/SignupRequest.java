package com.talhaatif.budgettracker.dto.auth;

import jakarta.validation.constraints.*;

import java.util.List;
import java.util.Set;

public record SignupRequest(
        @NotBlank(message = "Email cannot be blank")
        @Email(message = "Email should be valid")
        String email,


        @NotBlank(message = "Username cannot be blank")
        @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
        @Pattern(
                regexp = "^[a-zA-Z0-9](?:[a-zA-Z0-9._-]*[a-zA-Z0-9])?$",
                message = "Username must start and end with alphanumeric characters. Only '.', '_', and '-' are allowed as special characters in between"
        )
        String userName,


        @NotBlank(message = "Password cannot be blank")
        @Size(min = 6, max = 40, message = "Password must be between 6 and 40 characters")
        String password,

        @NotBlank(message = "First name cannot be blank")
        String firstName,

        @NotBlank(message = "Last name cannot be blank")
        String lastName,

        List<String> roles
) {}