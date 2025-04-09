package com.talhaatif.budgettracker.services.impl;


import com.talhaatif.budgettracker.entities.Transaction;
import com.talhaatif.budgettracker.repositories.TransactionRepository;
import com.talhaatif.budgettracker.services.TransactionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepo;

    public TransactionServiceImpl(TransactionRepository transactionRepo) {
        this.transactionRepo = transactionRepo;
    }

    @Override
    public Transaction createTransaction(Transaction transaction) {
        return transactionRepo.save(transaction);
    }

    @Override
    public Optional<Transaction> getTransactionById(String id) {
        return transactionRepo.findById(id);
    }

    @Override
    public List<Transaction> getAllTransactions() {
        return transactionRepo.findAll();
    }

    @Override
    public Transaction updateTransaction(Transaction transaction) {
        return transactionRepo.save(transaction);
    }

    @Override
    public void deleteTransaction(String id) {
        transactionRepo.deleteById(id);
    }
}

