package com.talhaatif.budgettracker.services;


import com.talhaatif.budgettracker.entities.Transaction;
import java.util.List;
import java.util.Optional;

public interface TransactionService {
    Transaction createTransaction(Transaction transaction);
    Optional<Transaction> getTransactionById(String id);
    List<Transaction> getAllTransactions();
    Transaction updateTransaction(Transaction transaction);
    void deleteTransaction(String id);
}

