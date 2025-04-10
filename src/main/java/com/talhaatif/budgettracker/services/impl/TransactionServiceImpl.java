package com.talhaatif.budgettracker.services.impl;

import com.talhaatif.budgettracker.entities.Account;
import com.talhaatif.budgettracker.entities.Transaction;
import com.talhaatif.budgettracker.entities.TransactionSearchCriteria;
import com.talhaatif.budgettracker.repositories.TransactionRepository;
import com.talhaatif.budgettracker.repositories.specifications.TransactionSpecifications;
import com.talhaatif.budgettracker.services.AccountService;
import com.talhaatif.budgettracker.services.TransactionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepo;
    private final AccountService accountService;

    public TransactionServiceImpl(TransactionRepository transactionRepo,
                                  AccountService accountService) {
        this.transactionRepo = transactionRepo;
        this.accountService = accountService;
    }


    @Override
    @Transactional
    public Transaction createTransaction(Transaction transaction) {
        Transaction savedTransaction = transactionRepo.save(transaction);
        updateAccountAggregates(savedTransaction, null);
        return savedTransaction;
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
    @Transactional
    public Transaction updateTransaction(Transaction transaction) {
        // Get the old transaction to calculate differences
        Transaction oldTransaction = transactionRepo.findById(transaction.getId()).orElse(null);
        Transaction updatedTransaction = transactionRepo.save(transaction);
        updateAccountAggregates(updatedTransaction, oldTransaction);
        return updatedTransaction;
    }

    @Override
    @Transactional
    public void deleteTransaction(String id) {
        Transaction transaction = transactionRepo.findById(id).orElse(null);
        if (transaction != null) {
            // Before deleting, we need to reverse its effect on the account
            reverseAccountAggregates(transaction);
            transactionRepo.deleteById(id);
        }
    }

    @Override
    public List<Transaction> searchTransactions(TransactionSearchCriteria criteria) {
        return transactionRepo.findAll(TransactionSpecifications.withCriteria(criteria));
    }

    @Override
    public List<Transaction> searchTransactions(TransactionSearchCriteria criteria, Sort sort) {
        return transactionRepo.findAll(TransactionSpecifications.withCriteria(criteria), sort);
    }

    @Override
    public Page<Transaction> searchTransactions(TransactionSearchCriteria criteria, Pageable pageable) {
        return transactionRepo.findAll(TransactionSpecifications.withCriteria(criteria), pageable);
    }

//     --------------------------------
    // Transaction related stuff
    // add transaction, update transaction
private void updateAccountAggregates(Transaction newTransaction, Transaction oldTransaction) {
    Account account = newTransaction.getAccount();

    if (oldTransaction != null) {
        // For updates, first reverse the old transaction's effect
        reverseTransactionEffect(account, oldTransaction);
    }

    // Apply the new transaction's effect
    applyTransactionEffect(account, newTransaction);

    // Save the updated account
    accountService.updateAccount(account);
}

   // remove transaction, update transaction

    private void reverseAccountAggregates(Transaction transaction) {
        Account account = transaction.getAccount();
        reverseTransactionEffect(account, transaction);
        accountService.updateAccount(account);
    }

    private void applyTransactionEffect(Account account, Transaction transaction) {
        if (transaction.getType() == Transaction.TransactionType.INCOME) {
            account.setTotalIncome(account.getTotalIncome().add(transaction.getAmount()));
        } else if (transaction.getType() == Transaction.TransactionType.EXPENSE) {
            account.setTotalExpense(account.getTotalExpense().add(transaction.getAmount()));
        }
        // Recalculate balance
        account.setBalance(account.getTotalIncome().subtract(account.getTotalExpense()));
    }

    private void reverseTransactionEffect(Account account, Transaction transaction) {
        if (transaction.getType() == Transaction.TransactionType.INCOME) {
            account.setTotalIncome(account.getTotalIncome().subtract(transaction.getAmount()));
        } else if (transaction.getType() == Transaction.TransactionType.EXPENSE) {
            account.setTotalExpense(account.getTotalExpense().subtract(transaction.getAmount()));
        }
        // Recalculate balance
        account.setBalance(account.getTotalIncome().subtract(account.getTotalExpense()));
    }
}