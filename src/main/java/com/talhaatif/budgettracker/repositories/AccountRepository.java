package com.talhaatif.budgettracker.repositories;

import com.talhaatif.budgettracker.entities.Account;
import com.talhaatif.budgettracker.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, String> {
    Optional<User> findByEmail(String email);
}

