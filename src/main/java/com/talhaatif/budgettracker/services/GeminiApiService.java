package com.talhaatif.budgettracker.services;

import com.talhaatif.budgettracker.dto.ai.GeminiParsedResponse;

public interface GeminiApiService {
    GeminiParsedResponse parseUserMessage(String userMessage);
}

