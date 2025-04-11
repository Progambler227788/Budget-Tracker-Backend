package com.talhaatif.budgettracker.services;



import com.talhaatif.budgettracker.dto.auth.*;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface AuthService {

    ResponseEntity<?> refreshToken(String refreshToken);


    ResponseEntity<TokenResponse> authenticateUser(LoginRequest loginRequest);
    ResponseEntity<UserResponse> registerUser(SignupRequest signupRequest);
    ResponseEntity<UserResponse> updateUserProfile(UpdateRequest updateRequest);
}
