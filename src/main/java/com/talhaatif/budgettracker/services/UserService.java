package com.talhaatif.budgettracker.services;

import com.talhaatif.budgettracker.entities.User;

import java.util.List;
import java.util.Optional;


import java.util.Set;

public interface UserService {
    User register(User user);

    User addAdmin(User user);
    Optional<User> findByEmail(String email);
    User findByUserName(String userName);
    Optional<User> findById(String id);
    void deleteUser(String id);
    void assignRoles(String userId, List<String> roles);
    List<String> getUserRoles(String userId);
    boolean isAdmin(String userId);
    boolean existsByEmail(String email);

    User getCurrentUserEntity();

    User updateUser(User user);
}