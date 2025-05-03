package com.talhaatif.budgettracker.repositories;

import com.talhaatif.budgettracker.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface CategoryRepository extends JpaRepository<Category,String> {
    Optional<Category> findByUserIdAndCategoryName(String userId, String categoryName);
}
