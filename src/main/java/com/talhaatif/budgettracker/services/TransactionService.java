package com.talhaatif.budgettracker.services;


import com.talhaatif.budgettracker.entities.Transaction;
import com.talhaatif.budgettracker.entities.TransactionSearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

public interface TransactionService {
    Transaction createTransaction(Transaction transaction);
    Optional<Transaction> getTransactionById(String id);
    List<Transaction> getAllTransactions();
    Transaction updateTransaction(Transaction transaction);
    void deleteTransaction(String id);



    // New methods for searching and sorting
    List<Transaction> searchTransactions(TransactionSearchCriteria criteria);
    List<Transaction> searchTransactions(TransactionSearchCriteria criteria, Sort sort);
    Page<Transaction> searchTransactions(TransactionSearchCriteria criteria, Pageable pageable);
}

