package com.talhaatif.budgettracker.services;


import com.talhaatif.budgettracker.entities.Budget;
import java.util.List;
import java.util.Optional;

public interface BudgetService {
    Budget createBudget(Budget budget);
    Optional<Budget> getBudgetById(String id);
    List<Budget> getAllBudgets();
    Budget updateBudget(Budget budget);
    void deleteBudget(String id);
}

