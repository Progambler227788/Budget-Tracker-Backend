package com.talhaatif.budgettracker.repositories;

import com.talhaatif.budgettracker.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category,String> {
}
