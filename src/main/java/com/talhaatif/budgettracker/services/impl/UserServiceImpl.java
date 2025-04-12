package com.talhaatif.budgettracker.services.impl;

import com.talhaatif.budgettracker.entities.Account;
import com.talhaatif.budgettracker.entities.User;
import com.talhaatif.budgettracker.repositories.UserRepository;
import com.talhaatif.budgettracker.services.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepo;
    private final AccountServiceImpl accountService;

    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepo, AccountServiceImpl accountService, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.accountService = accountService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public User register(User user) {
        // Check if email already exists
        if (existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }

        // Rest of your existing register method...
        user.setCreatedAt(LocalDateTime.now());
        user.setCreatedBy("SYSTEM");
        // Ensure roles are properly set

        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            user.setRoles(Set.of("USER"));
        }

        else {
            // Clean roles - remove any existing ROLE_ prefix
            Set<String> cleanedRoles = user.getRoles().stream()
                    .map(role -> role.startsWith("ROLE_") ? role.substring(5) : role)
                    .collect(Collectors.toSet());
            user.setRoles(cleanedRoles);
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // save user first as account needs user and it is eager to save user first
        User savedUser = userRepo.save(user);

        Account account = Account.builder()
                .name(user.getFirstName() + " " + user.getLastName() + "'s Account")
                .description("Primary account")
                .balance(BigDecimal.ZERO)
                .totalIncome(BigDecimal.ZERO)
                .totalExpense(BigDecimal.ZERO)
                .type(Account.AccountType.CASH)
                .user(savedUser)
                .build();

        accountService.createAccount(account);

//        savedUser.setAccounts(Set.of(savedAccount));

        // no saving again cascade will handle it for me
        return savedUser;
    }


    @Override
    public boolean existsByEmail(String email) {
        return userRepo.existsByEmail(email);
    }


    @Override
    public User addAdmin(User user) {
        user.setCreatedAt(LocalDateTime.now());
        user.setCreatedBy("SYSTEM");
        user.setRoles(Set.of("ADMIN"));
        return userRepo.save(user);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepo.findByEmail(email);
    }

    @Override
    public Optional<User> findById(String id) {
        return userRepo.findById(id);
    }

    @Override
    public void deleteUser(String id) {
        userRepo.deleteById(id);
    }

    @Override
    public void assignRoles(String userId, Set<String> roles) {
        userRepo.findById(userId).ifPresent(user -> {
            user.setRoles(roles);
            userRepo.save(user);
        });
    }

    @Override
    public Set<String> getUserRoles(String userId) {
        return userRepo.findById(userId)
                .map(User::getRoles)
                .orElse(Collections.emptySet());
    }


    @Override
    public boolean isAdmin(String userId) {
        return userRepo.findById(userId)
                .map(user -> user.getRoles().contains("ADMIN"))
                .orElse(false);
    }

}