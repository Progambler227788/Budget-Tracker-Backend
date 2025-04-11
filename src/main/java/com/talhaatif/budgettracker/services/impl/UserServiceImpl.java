package com.talhaatif.budgettracker.services.impl;

import com.talhaatif.budgettracker.entities.Account;
import com.talhaatif.budgettracker.entities.User;
import com.talhaatif.budgettracker.repositories.UserRepository;
import com.talhaatif.budgettracker.services.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepo;
    private final AccountServiceImpl accountService;

    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserServiceImpl(UserRepository userRepo, AccountServiceImpl accountService) {
        this.userRepo = userRepo;
        this.accountService = accountService;
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
        user.setRoles(Set.of("USER"));
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        Account account = Account.builder()
                .name(user.getFirstName() + " " + user.getLastName() + "'s Account")
                .description("Primary account")
                .balance(BigDecimal.ZERO)
                .totalIncome(BigDecimal.ZERO)
                .totalExpense(BigDecimal.ZERO)
                .type(Account.AccountType.CASH)
                .user(user)
                .build();

        Account savedAccount = accountService.createAccount(account);
        user.setAccounts(Set.of(savedAccount));

        return userRepo.save(user);
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