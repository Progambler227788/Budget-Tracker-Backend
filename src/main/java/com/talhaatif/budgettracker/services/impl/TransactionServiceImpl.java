package com.talhaatif.budgettracker.services.impl;

import com.talhaatif.budgettracker.dto.transaction.*;
import com.talhaatif.budgettracker.entities.*;
import com.talhaatif.budgettracker.repositories.TransactionRepository;
import com.talhaatif.budgettracker.repositories.specifications.TransactionSpecifications;
import com.talhaatif.budgettracker.services.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepo;
    private final AccountService accountService;

    private final CategoryService categoryService;
    private final BudgetService budgetService;
    private final UserService userService;

    public TransactionServiceImpl(TransactionRepository transactionRepo, AccountService accountService, CategoryService categoryService, BudgetService budgetService, UserService userService) {
        this.transactionRepo = transactionRepo;
        this.accountService = accountService;
        this.categoryService = categoryService;
        this.budgetService = budgetService;
        this.userService = userService;
    }

    @Override
    @Transactional
    public TransactionResponse createTransaction(TransactionCreateRequest request) {
        Transaction transaction = new Transaction();
        mapRequestToTransaction(request, transaction);

        Transaction savedTransaction = transactionRepo.save(transaction);
        updateAccountAggregates(savedTransaction, null);
        updateBudgetBalance(savedTransaction, null);  // 👈 This line will update budget balance

        return TransactionResponse.fromTransaction(savedTransaction);
    }

    @Override
    public TransactionResponse getTransactionById(String id) {
        return transactionRepo.findById(id)
                .map(TransactionResponse::fromTransaction)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
    }

    @Override
    public List<TransactionResponse> getAllTransactions() {
        return transactionRepo.findAll().stream()
                .map(TransactionResponse::fromTransaction)
                .collect(Collectors.toList());
    }

    @Override
    public Page<TransactionResponse> searchTransactions(TransactionSearchCriteria criteria, Pageable pageable) {
        Page<Transaction> transactions = transactionRepo.findAll(
                TransactionSpecifications.withCriteria(criteria), pageable);

        List<TransactionResponse> content = transactions.getContent().stream()
                .map(TransactionResponse::fromTransaction)
                .collect(Collectors.toList());

        return new PageImpl<>(content, pageable, transactions.getTotalElements());
    }

    @Override
    @Transactional
    public TransactionResponse updateTransaction(String id, TransactionUpdateRequest request) {
        Transaction transaction = transactionRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        Transaction oldTransaction = new Transaction();
        copyTransactionProperties(transaction, oldTransaction);

        mapUpdateRequestToTransaction(request, transaction);
        Transaction updatedTransaction = transactionRepo.save(transaction);
        updateAccountAggregates(updatedTransaction, oldTransaction);

        return TransactionResponse.fromTransaction(updatedTransaction);
    }

    @Override
    @Transactional
    public void deleteTransaction(String id) {
        Transaction transaction = transactionRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        reverseAccountAggregates(transaction);
        transactionRepo.deleteById(id);
    }

    private void mapRequestToTransaction(TransactionCreateRequest request, Transaction transaction) {
        transaction.setAmount(request.getAmount());
        transaction.setDate(request.getDate());
        transaction.setDescription(request.getDescription());
        transaction.setType(request.getType());

        Account account = accountService.getAccountById(request.getAccountId())
                .orElseThrow(() -> new RuntimeException("Account not found"));
        transaction.setAccount(account);

        Category category = categoryService.getCategoryById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        transaction.setCategory(category);

        if (request.getBudgetId() != null) {
            Budget budget = budgetService.getBudgetById(request.getBudgetId())
                    .orElseThrow(() -> new RuntimeException("Budget not found"));
            transaction.setBudget(budget);
        }

        // Set current user as the transaction owner

        User currentUser = userService.getCurrentUserEntity();
        transaction.setUser(currentUser);
    }
    private void mapUpdateRequestToTransaction(TransactionUpdateRequest request, Transaction transaction) {
        if (request.getAmount() != null) {
            transaction.setAmount(request.getAmount());
        }
        if (request.getDate() != null) {
            transaction.setDate(request.getDate());
        }
        if (request.getDescription() != null) {
            transaction.setDescription(request.getDescription());
        }
        if (request.getType() != null) {
            transaction.setType(request.getType());
        }
        if (request.getAccountId() != null) {
            Account account = accountService.getAccountById(request.getAccountId())
                    .orElseThrow(() -> new RuntimeException("Account not found"));
            transaction.setAccount(account);
        }
        if (request.getCategoryId() != null) {
            Category category = categoryService.getCategoryById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            transaction.setCategory(category);
        }
        if (request.getBudgetId() != null) {
            Budget budget = budgetService.getBudgetById(request.getBudgetId())
                    .orElseThrow(() -> new RuntimeException("Budget not found"));
            transaction.setBudget(budget);
        }
    }

    private void copyTransactionProperties(Transaction source, Transaction target) {
        target.setAmount(source.getAmount());
        target.setType(source.getType());
        target.setAccount(source.getAccount());
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
    // Budget Related stuff

    private void updateBudgetBalance(Transaction newTransaction, Transaction oldTransaction) {
        Budget budget = newTransaction.getBudget();  // This assumes transaction has a budget linked
        if (budget == null) {
            return; // No budget associated
        }

        BigDecimal balanceChange = BigDecimal.ZERO;

        // If it's an EXPENSE, deduct. If INCOME, add.
        if (newTransaction.getType() == Transaction.TransactionType.EXPENSE) {
            balanceChange = newTransaction.getAmount().negate(); // Subtract
        } else if (newTransaction.getType() == Transaction.TransactionType.INCOME) {
            balanceChange = newTransaction.getAmount(); // Add
        }

        // If it's an update and there's an old transaction, reverse old amount first
        if (oldTransaction != null) {
            if (oldTransaction.getType() == Transaction.TransactionType.EXPENSE) {
                balanceChange = balanceChange.add(oldTransaction.getAmount()); // Add back old expense
            } else if (oldTransaction.getType() == Transaction.TransactionType.INCOME) {
                balanceChange = balanceChange.subtract(oldTransaction.getAmount()); // Subtract old income
            }
        }

        budget.setCurrentBalance(budget.getCurrentBalance().add(balanceChange));
    }

}