package com.talhaatif.budgettracker.repositories;


import com.talhaatif.budgettracker.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, String> {
}

