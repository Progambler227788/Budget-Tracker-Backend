package com.talhaatif.budgettracker.dto.transaction;

import com.talhaatif.budgettracker.entities.Transaction;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class TransactionUpdateRequest {
    @Positive
    private BigDecimal amount;

    private LocalDate date;

    private String description;

    private Transaction.TransactionType type;

    private String accountId;

    private String categoryId;

    private String budgetId;
}
