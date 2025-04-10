package com.talhaatif.budgettracker.repositories;

import com.talhaatif.budgettracker.entities.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface  BudgetRepository extends JpaRepository<Budget,String > {

}
