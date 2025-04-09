package com.talhaatif.budgettracker.services.impl;


import com.talhaatif.budgettracker.entities.Category;
import com.talhaatif.budgettracker.repositories.CategoryRepository;
import com.talhaatif.budgettracker.services.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepo;

    public CategoryServiceImpl(CategoryRepository categoryRepo) {
        this.categoryRepo = categoryRepo;
    }

    @Override
    public Category createCategory(Category category) {
        return categoryRepo.save(category);
    }

    @Override
    public Optional<Category> getCategoryById(String id) {
        return categoryRepo.findById(id);
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepo.findAll();
    }

    @Override
    public Category updateCategory(Category category) {
        return categoryRepo.save(category);
    }

    @Override
    public void deleteCategory(String id) {
        categoryRepo.deleteById(id);
    }
}
