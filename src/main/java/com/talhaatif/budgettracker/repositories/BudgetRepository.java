package com.talhaatif.budgettracker.repositories;

import com.talhaatif.budgettracker.entities.Budget;
import org.springframework.data.jpa.repository.JpaRepository;

public interface  BudgetRepository extends JpaRepository<Budget,String > {

}
