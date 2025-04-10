package com.talhaatif.budgettracker.entities;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionSearchCriteria {
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;
    private Transaction.TransactionType type;
    private String accountId;
    private String categoryId;
    private String userId;
    private String budgetId;
}
