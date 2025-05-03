package com.talhaatif.budgettracker.dto.category;

import com.talhaatif.budgettracker.entities.Category;
import com.talhaatif.budgettracker.entities.CategoryType;
import lombok.Data;

@Data
public class CategoryResponse {
    private String id;
    private String name;
    private String description;
    private CategoryType type;

    public static CategoryResponse fromCategory(Category category) {
        CategoryResponse response = new CategoryResponse();
        response.setId(category.getId());
        response.setName(category.getCategoryName());
        response.setDescription(category.getDescription());
        response.setType(category.getType());
        return response;
    }
}
