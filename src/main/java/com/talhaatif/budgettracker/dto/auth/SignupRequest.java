package com.talhaatif.budgettracker.dto.auth;

import jakarta.validation.constraints.*;

import java.util.List;
import java.util.Set;

public record SignupRequest(
        @NotBlank(message = "Email cannot be blank")
        @Email(message = "Email should be valid")
        String email,


        @NotBlank(message = "UserName cannot be blank")
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