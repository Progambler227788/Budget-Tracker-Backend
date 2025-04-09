package com.talhaatif.budgettracker.services;

import com.talhaatif.budgettracker.entities.User;
import java.util.Optional;


import java.util.Set;

public interface UserService {
    User register(User user);
    User addAdmin(User user);
    Optional<User> findByEmail(String email);
    Optional<User> findById(String id);
    void deleteUser(String id);
    void assignRoles(String userId, Set<String> roles);
    Set<String> getUserRoles(String userId);
    boolean isAdmin(String userId);
}