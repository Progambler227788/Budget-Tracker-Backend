package com.talhaatif.budgettracker.repositories;

import com.talhaatif.budgettracker.entities.Account;
import com.talhaatif.budgettracker.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {
    Optional<Account> findByUserIdAndAccountName(String userId, String accountName);
    Optional<Account> findFirstByUserId(String userId);
}

