package com.talhaatif.budgettracker.services.impl;


import com.talhaatif.budgettracker.entities.Category;
import com.talhaatif.budgettracker.entities.CategoryType;
import com.talhaatif.budgettracker.entities.User;
import com.talhaatif.budgettracker.repositories.CategoryRepository;
import com.talhaatif.budgettracker.services.CategoryService;
import com.talhaatif.budgettracker.services.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepo;

    private final UserService userService;

    public CategoryServiceImpl(CategoryRepository categoryRepo, UserService userService) {
        this.categoryRepo = categoryRepo;
        this.userService = userService;
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

    @Override
    public String getOrCreateCategory(String userId, String categoryName) {
        // Check if category exists for user
        Optional<Category> optionalCategory = categoryRepo.findByUserIdAndCategoryName(userId, categoryName);

        if (optionalCategory.isPresent()) {
            return optionalCategory.get().getId();
        }

        // If not exists, create default category (let’s assume EXPENSE type if not given)

        Category newCategory = Category.builder()
                .categoryName(categoryName)
                .type(CategoryType.EXPENSE)  // Default category type (or you can make it dynamic if needed)
                .user(userService.getCurrentUserEntity())
                .build();

        return categoryRepo.save(newCategory).getId();
    }

}
