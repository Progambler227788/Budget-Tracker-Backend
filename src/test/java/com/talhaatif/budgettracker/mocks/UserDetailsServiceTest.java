package com.talhaatif.budgettracker.mocks;


import com.talhaatif.budgettracker.entities.User;
import com.talhaatif.budgettracker.repositories.UserRepository;
import com.talhaatif.budgettracker.services.impl.UserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Test
    void loadUserByUsername_ExistingUser_ReturnsUserDetails() {
        // Setup test user
        User testUser = new User();
        testUser.setUserName("testuser");
        testUser.setPassword("password");
        testUser.setRoles(Arrays.asList("USER"));

        // Configure mock
        when(userRepository.findByUserName("testuser"))
                .thenReturn(testUser);

        // Call the method
        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

        // Verify results
        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
        assertEquals("password", userDetails.getPassword());
        assertFalse(userDetails.getAuthorities().isEmpty());
    }

    @Test
    void loadUserByUsername_NonExistentUser_ThrowsException() {
        // Configure mock to return null
        when(userRepository.findByUserName("unknown"))
            .thenReturn(null);

        // Verify exception is thrown
        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("unknown");
        });
    }
}
