package com.talhaatif.budgettracker.services.impl;

import com.talhaatif.budgettracker.entities.User;
import com.talhaatif.budgettracker.repositories.UserRepository;
import com.talhaatif.budgettracker.services.UserService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepo;

    public UserServiceImpl(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public User register(User user) {
        user.setCreatedAt(LocalDateTime.now());
        user.setCreatedBy("SYSTEM");
        user.setRoles(Set.of("USER"));
        return userRepo.save(user);
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
