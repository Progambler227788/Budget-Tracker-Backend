package com.talhaatif.budgettracker.services;

import com.talhaatif.budgettracker.dto.budget.BudgetRequest;
import com.talhaatif.budgettracker.dto.budget.BudgetResponse;
import com.talhaatif.budgettracker.entities.Budget;

import java.util.List;
import java.util.Optional;

public interface BudgetService {

    BudgetResponse createBudget(BudgetRequest request);

    BudgetResponse getBudget(String id);

    Optional<Budget> getBudgetById(String id);

    List<BudgetResponse> getAllBudgets();

    BudgetResponse updateBudget(String id, BudgetRequest request);

    void deleteBudget(String id);
}
