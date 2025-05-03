package com.talhaatif.budgettracker.services;

import com.talhaatif.budgettracker.dto.transaction.TransactionCreateRequest;
import com.talhaatif.budgettracker.dto.transaction.TransactionResponse;
import com.talhaatif.budgettracker.dto.transaction.TransactionUpdateRequest;
import com.talhaatif.budgettracker.entities.TransactionSearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TransactionService {
    TransactionResponse createTransaction(TransactionCreateRequest request);
    TransactionResponse getTransactionById(String id);
    List<TransactionResponse> getAllTransactions();
    Page<TransactionResponse> searchTransactions(TransactionSearchCriteria criteria, Pageable pageable);
    TransactionResponse updateTransaction(String id, TransactionUpdateRequest request);
    void deleteTransaction(String id);
}