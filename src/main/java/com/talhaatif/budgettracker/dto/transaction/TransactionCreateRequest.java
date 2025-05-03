package com.talhaatif.budgettracker.dto.transaction;


import com.talhaatif.budgettracker.entities.Transaction;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class TransactionCreateRequest {
    @NotNull
    @Positive
    private BigDecimal amount;

    @NotNull
    private LocalDate date;

    private String description;

    @NotNull
    private Transaction.TransactionType type;

    @NotBlank
    private String accountId;

    @NotBlank
    private String categoryId;

    private String budgetId;
}
