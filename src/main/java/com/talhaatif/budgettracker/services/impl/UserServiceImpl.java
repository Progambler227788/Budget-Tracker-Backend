package com.talhaatif.budgettracker.services.impl;

import com.talhaatif.budgettracker.entities.Account;
import com.talhaatif.budgettracker.entities.User;
import com.talhaatif.budgettracker.repositories.UserRepository;
import com.talhaatif.budgettracker.services.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

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
    public User getCurrentUserEntity() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepo.findByUserName(username);
    }

    @Override
    @Transactional
    public User register(User user) {
        // Check if email already exists
        if (existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }


        user.setCreatedAt(LocalDateTime.now());
        user.setCreatedBy("SYSTEM");


        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            user.setRoles(Arrays.asList("USER"));
        }



        // save user first as account needs user and it is eager to save user first
        User savedUser = userRepo.save(user);

        Account account = Account.builder()
                .accountName(user.getFirstName() + " " + user.getLastName() + "'s Account")
                .description("Primary account")
                .balance(BigDecimal.ZERO)
                .totalIncome(BigDecimal.ZERO)
                .totalExpense(BigDecimal.ZERO)
                .type(Account.AccountType.CASH)
                .user(savedUser)
                .build();

        accountService.createAccount(account);


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
        user.setRoles(Arrays.asList("ADMIN"));
        return userRepo.save(user);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepo.findByEmail(email);
    }

    @Override
    public User findByUserName(String userName) {
        return userRepo.findByUserName(userName);
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
    public void assignRoles(String userId, List<String> roles) {
        userRepo.findById(userId).ifPresent(user -> {
            user.setRoles(roles);
            userRepo.save(user);
        });
    }
    @Override
    public User updateUser(User user) {
        return userRepo.save(user);
    }

    @Override
    public List<String> getUserRoles(String userId) {
        return userRepo.findById(userId)
                .map(User::getRoles)
                .orElse(Collections.emptyList());
    }


    @Override
    public boolean isAdmin(String userId) {
        return userRepo.findById(userId)
                .map(user -> user.getRoles().contains("ADMIN"))
                .orElse(false);
    }



}