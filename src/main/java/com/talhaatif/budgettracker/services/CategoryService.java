package com.talhaatif.budgettracker.services;


import com.talhaatif.budgettracker.entities.Category;
import java.util.List;
import java.util.Optional;

public interface CategoryService {
    Category createCategory(Category category);
    Optional<Category> getCategoryById(String id);
    List<Category> getAllCategories();
    Category updateCategory(Category category);
    void deleteCategory(String id);
}
