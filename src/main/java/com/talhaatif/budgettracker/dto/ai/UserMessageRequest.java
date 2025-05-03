package com.talhaatif.budgettracker.dto.ai;

import lombok.Data;

@Data
public class UserMessageRequest {
    private String message; // User's natural language message like "Add 30 for food"
}

