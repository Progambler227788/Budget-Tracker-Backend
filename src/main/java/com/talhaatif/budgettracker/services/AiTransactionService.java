package com.talhaatif.budgettracker.services;

import com.talhaatif.budgettracker.dto.ai.UserMessageRequest;

public interface AiTransactionService {
    void processUserMessage(UserMessageRequest request);
}

