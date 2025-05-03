package com.talhaatif.budgettracker.controllers;
;

import com.talhaatif.budgettracker.dto.category.CategoryCreateRequest;
import com.talhaatif.budgettracker.dto.category.CategoryResponse;
import com.talhaatif.budgettracker.dto.category.CategoryUpdateRequest;
import com.talhaatif.budgettracker.entities.Category;
import com.talhaatif.budgettracker.services.CategoryService;
import com.talhaatif.budgettracker.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "Categories", description = "Category management API endpoints")
@SecurityRequirement(name = "bearerAuth")
public class CategoryController {

    private final CategoryService categoryService;
    private final UserService userService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Create category", description = "Create a new category")
    public ResponseEntity<CategoryResponse> createCategory(
            @Valid @RequestBody CategoryCreateRequest request) {

        Category category = new Category();
        category.setCategoryName(request.getName());
        category.setDescription(request.getDescription());
        category.setType(request.getType());
        category.setUser(userService.getCurrentUserEntity());

        Category savedCategory = categoryService.createCategory(category);

        return ResponseEntity.ok(CategoryResponse.fromCategory(savedCategory));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get category by ID", description = "Retrieve a specific category by its ID")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable String id) {
        Category category = categoryService.getCategoryById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        return ResponseEntity.ok(CategoryResponse.fromCategory(category));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get all categories", description = "Retrieve all categories for the current user")
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        List<CategoryResponse> categories = categoryService.getAllCategories().stream()
                .map(CategoryResponse::fromCategory)
                .collect(Collectors.toList());

        return ResponseEntity.ok(categories);
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Update category", description = "Update an existing category")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable String id,
            @Valid @RequestBody CategoryUpdateRequest request) {

        Category category = categoryService.getCategoryById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        category.setCategoryName(request.getName());
        category.setDescription(request.getDescription());
        category.setType(request.getType());

        Category updatedCategory = categoryService.updateCategory(category);

        return ResponseEntity.ok(CategoryResponse.fromCategory(updatedCategory));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Delete category", description = "Delete a category")
    public ResponseEntity<Void> deleteCategory(@PathVariable String id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}

