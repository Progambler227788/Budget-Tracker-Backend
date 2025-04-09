package com.talhaatif.budgettracker.services.impl;

import com.talhaatif.budgettracker.entities.Budget;
import com.talhaatif.budgettracker.repositories.BudgetRepository;
import com.talhaatif.budgettracker.services.BudgetService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BudgetServiceImpl implements BudgetService {

    private final BudgetRepository budgetRepo;

    public BudgetServiceImpl(BudgetRepository budgetRepo) {
        this.budgetRepo = budgetRepo;
    }

    @Override
    public Budget createBudget(Budget budget) {
        return budgetRepo.save(budget);
    }

    @Override
    public Optional<Budget> getBudgetById(String id) {
        return budgetRepo.findById(id);
    }

    @Override
    public List<Budget> getAllBudgets() {
        return budgetRepo.findAll();
    }

    @Override
    public Budget updateBudget(Budget budget) {
        return budgetRepo.save(budget);
    }

    @Override
    public void deleteBudget(String id) {
        budgetRepo.deleteById(id);
    }
}

