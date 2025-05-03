package com.talhaatif.budgettracker.dto.ai;

import lombok.Data;
import com.talhaatif.budgettracker.entities.Transaction.TransactionType;

@Data
public class GeminiParsedResponse {
    private double amount;
    private String categoryName;
    private String accountName; // Optional
    private String description; // Optional
    private TransactionType transactionType; // EXPENSE or INCOME
}

