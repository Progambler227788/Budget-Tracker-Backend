package com.talhaatif.budgettracker.dto.category;


import com.talhaatif.budgettracker.entities.CategoryType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CategoryUpdateRequest {
    @NotBlank
    private String name;

    private String description;

    @NotNull
    private CategoryType type;
}

