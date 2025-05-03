package com.talhaatif.budgettracker.dto.budget;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class BudgetCategoryRequest {
    private BigDecimal allocatedAmount;
    private String budgetId;
    private String categoryId;
}

