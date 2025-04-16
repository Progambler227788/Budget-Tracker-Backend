package com.talhaatif.budgettracker.mocks;

import com.talhaatif.budgettracker.dto.auth.*;
import com.talhaatif.budgettracker.entities.RefreshToken;
import com.talhaatif.budgettracker.entities.User;
import com.talhaatif.budgettracker.exceptions.TokenRefreshException;
import com.talhaatif.budgettracker.security.JwtUtil;
import com.talhaatif.budgettracker.services.RefreshTokenService;
import com.talhaatif.budgettracker.services.UserService;
import com.talhaatif.budgettracker.services.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    // Mock all dependencies that AuthServiceImpl needs
    @Mock private AuthenticationManager authenticationManager;
    @Mock private UserService userService;
    @Mock private JwtUtil jwtUtil;
    @Mock private RefreshTokenService refreshTokenService;
    @Mock private PasswordEncoder passwordEncoder;

    // The service we're testing, with mocks injected
    @InjectMocks private AuthServiceImpl authService;

    // Test data that will be reused
    private User testUser;
    private RefreshToken testRefreshToken;

    @BeforeEach
    void setUp() {
        // Setup a test user with minimal required fields
        testUser = new User();
        testUser.setUserName("testuser");
        testUser.setPassword("encodedPassword");
        testUser.setRoles(Arrays.asList("USER"));

        // Setup a test refresh token
        testRefreshToken = new RefreshToken();
        testRefreshToken.setToken("testRefreshToken");
        testRefreshToken.setUser(testUser);
    }

    // Test successful user authentication
    @Test
    void authenticateUser_WithValidCredentials_ReturnsTokens() throws Exception {
        // Setup test data
        LoginRequest request = new LoginRequest("testuser", "password");
        Authentication mockAuth = mock(Authentication.class);

        // Configure mock behaviors
        when(authenticationManager.authenticate(any()))
                .thenReturn(mockAuth);
        when(mockAuth.getPrincipal())
                .thenReturn(testUser);
        when(jwtUtil.generateAccessToken(any(), any()))
                .thenReturn("testAccessToken");
        when(refreshTokenService.createRefreshToken(any()))
                .thenReturn(testRefreshToken);

        // Call the method being tested
        ResponseEntity<TokenResponse> response = authService.authenticateUser(request);

        // Verify the results
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("testAccessToken", response.getBody().accessToken());
        assertEquals("testRefreshToken", response.getBody().refreshToken());

        // Verify mocks were called as expected
        verify(authenticationManager).authenticate(any());
        verify(refreshTokenService).createRefreshToken(testUser);
    }

    // Test authentication with invalid credentials
    @Test
    void authenticateUser_WithInvalidCredentials_ThrowsException() {
        // Setup test with bad credentials
        LoginRequest request = new LoginRequest("wronguser", "wrongpass");

        // Make authentication fail
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // Verify exception is thrown
        assertThrows(RuntimeException.class, () -> {
            authService.authenticateUser(request);
        });
    }

    // Test successful token refresh
    @Test
    void refreshToken_WithValidToken_ReturnsNewAccessToken() {
        // Setup test refresh token
        String validToken = "validRefreshToken";
        RefreshToken mockRefreshToken = new RefreshToken();
        mockRefreshToken.setToken(validToken);
        mockRefreshToken.setUser(testUser);

        // Configure mocks
        when(refreshTokenService.findByToken(validToken))
                .thenReturn(Optional.of(mockRefreshToken));
        when(refreshTokenService.verifyExpiration(mockRefreshToken))
                .thenReturn(mockRefreshToken); // Important: mock the verification step
        when(jwtUtil.generateAccessToken(any(), any()))
                .thenReturn("newAccessToken");

        // Call the method
        ResponseEntity<TokenResponse> response = authService.refreshToken(validToken);

        // Verify results
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("newAccessToken", response.getBody().accessToken());
        assertEquals(validToken, response.getBody().refreshToken());
    }

    // Test token refresh with invalid token
    @Test
    void refreshToken_WithInvalidToken_ThrowsException() {
        // Setup invalid token scenario
        String invalidToken = "invalidToken";

        // Make token lookup fail
        when(refreshTokenService.findByToken(invalidToken))
                .thenReturn(Optional.empty());

        // Verify exception is thrown
        assertThrows(TokenRefreshException.class, () -> {
            authService.refreshToken(invalidToken);
        });
    }

    // Test successful user registration
    @Test
    void registerUser_WithNewEmail_CreatesUser() {
        // Setup registration request
        SignupRequest request = new SignupRequest(
                "new@example.com", "newuser", "password",
                "New", "User", null);

        // Configure mocks
        when(userService.existsByEmail("new@example.com"))
                .thenReturn(false);
        when(passwordEncoder.encode("password"))
                .thenReturn("encodedPassword");
        when(userService.register(any()))
                .thenReturn(testUser);

        // Call registration
        ResponseEntity<UserResponse> response = authService.registerUser(request);

        // Verify results
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("testuser", response.getBody().userName());

        // Verify user was created with encoded password
        verify(userService).register(argThat(user ->
                "encodedPassword".equals(user.getPassword())));
    }

    // Test registration with duplicate email
    @Test
    void registerUser_WithExistingEmail_ThrowsException() {
        // Setup duplicate email scenario
        SignupRequest request = new SignupRequest(
                "existing@example.com", "newuser", "password",
                "New", "User", null);

        // Make email check return true
        when(userService.existsByEmail("existing@example.com"))
                .thenReturn(true);

        // Verify exception is thrown
        assertThrows(IllegalArgumentException.class, () -> {
            authService.registerUser(request);
        });
    }

    // Test successful user profile update
    @Test
    void updateUserProfile_AuthenticatedUser_UpdatesProfile() {
        // Setup test data
        UpdateRequest request = new UpdateRequest("Updated", "Name", "123556");
        testUser.setFirstName("Old");
        testUser.setLastName("Name");

        // Configure mocks
        when(userService.findByUserName("testuser"))
                .thenReturn(testUser);
        when(userService.updateUser(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Need to mock security context for this test
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("testuser", null));

        // Call update
        ResponseEntity<UserResponse> response = authService.updateUserProfile(request);

        // Verify results
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Updated", response.getBody().firstName());
        assertEquals("Name", response.getBody().lastName());
    }

    // Test user logout
    @Test
    void logoutUser_ValidUser_DeletesRefreshTokens() {
        // Setup test user
        testUser.setId("user123");

        // Configure mock
        when(userService.findByUserName("testuser"))
                .thenReturn(testUser);

        // Call logout
        authService.logoutUser("testuser");

        // Verify refresh tokens were deleted
        verify(refreshTokenService).deleteByUserId("user123");
    }
}