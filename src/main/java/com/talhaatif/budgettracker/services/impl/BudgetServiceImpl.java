package com.talhaatif.budgettracker.services.impl;

import com.talhaatif.budgettracker.dto.budget.BudgetRequest;
import com.talhaatif.budgettracker.dto.budget.BudgetResponse;
import com.talhaatif.budgettracker.entities.Budget;
import com.talhaatif.budgettracker.entities.User;
import com.talhaatif.budgettracker.repositories.BudgetRepository;
import com.talhaatif.budgettracker.services.BudgetService;
import com.talhaatif.budgettracker.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BudgetServiceImpl implements BudgetService {

    private final BudgetRepository budgetRepo;
    private final UserService userService;

    @Override
    public BudgetResponse createBudget(BudgetRequest request) {
        User user = userService.getCurrentUserEntity();

        Budget budget = Budget.builder()
                .name(request.getName())
                .description(request.getDescription())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .currentBalance(request.getCurrentBalance())
                .user(user)
                .build();

        Budget saved = budgetRepo.save(budget);
        return BudgetResponse.fromBudget(saved);
    }

    @Override
    public BudgetResponse getBudget(String id) {
        Budget budget = budgetRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Budget not found"));
        return BudgetResponse.fromBudget(budget);
    }


    @Override
    public Optional<Budget> getBudgetById(String id) {
        return Optional.ofNullable(budgetRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Budget not found")));
    }


    @Override
    public List<BudgetResponse> getAllBudgets() {
        return budgetRepo.findAll().stream()
                .map(BudgetResponse::fromBudget)
                .collect(Collectors.toList());
    }

    @Override
    public BudgetResponse updateBudget(String id, BudgetRequest request) {
        Budget budget = budgetRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Budget not found"));

        budget.setName(request.getName());
        budget.setDescription(request.getDescription());
        budget.setStartDate(request.getStartDate());
        budget.setEndDate(request.getEndDate());
        budget.setCurrentBalance(request.getCurrentBalance());

        Budget updated = budgetRepo.save(budget);
        return BudgetResponse.fromBudget(updated);
    }

    @Override
    public void deleteBudget(String id) {
        budgetRepo.deleteById(id);
    }
}
