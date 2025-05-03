package com.talhaatif.budgettracker.dto.budget;


import com.talhaatif.budgettracker.entities.Budget;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class BudgetResponse {
    private String id;
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal currentBalance;
    private String userId;

    public static BudgetResponse fromBudget(Budget budget) {
        BudgetResponse response = new BudgetResponse();
        response.setId(budget.getId());
        response.setName(budget.getName());
        response.setDescription(budget.getDescription());
        response.setStartDate(budget.getStartDate());
        response.setEndDate(budget.getEndDate());
        response.setCurrentBalance(budget.getCurrentBalance());
        response.setUserId(budget.getUser().getId());
        return response;
    }
}

