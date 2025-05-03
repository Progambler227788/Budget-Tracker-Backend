package com.talhaatif.budgettracker.dto.budget;


import com.talhaatif.budgettracker.entities.BudgetCategory;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BudgetCategoryResponse {
    private String id;
    private BigDecimal allocatedAmount;
    private String budgetId;
    private String categoryId;

    public static BudgetCategoryResponse fromBudgetCategory(BudgetCategory bc) {
        BudgetCategoryResponse response = new BudgetCategoryResponse();
        response.setId(bc.getId());
        response.setAllocatedAmount(bc.getAllocatedAmount());
        response.setBudgetId(bc.getBudget().getId());
        response.setCategoryId(bc.getCategory().getId());
        return response;
    }
}
