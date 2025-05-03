package com.talhaatif.budgettracker.dto.transaction;


import com.talhaatif.budgettracker.entities.Transaction;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class TransactionResponse {
    private String id;
    private BigDecimal amount;
    private LocalDate date;
    private String description;
    private Transaction.TransactionType type;
    private String accountId;
    private String categoryId;
    private String budgetId;
    private String userId;

    public static TransactionResponse fromTransaction(Transaction transaction) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .amount(transaction.getAmount())
                .date(transaction.getDate())
                .description(transaction.getDescription())
                .type(transaction.getType())
                .accountId(transaction.getAccount().getId())
                .categoryId(transaction.getCategory().getId())
                .budgetId(transaction.getBudget() != null ? transaction.getBudget().getId() : null)
                .userId(transaction.getUser().getId())
                .build();
    }
}
