package com.talhaatif.budgettracker.services.impl;

import com.talhaatif.budgettracker.dto.ai.GeminiParsedResponse;
import com.talhaatif.budgettracker.dto.ai.UserMessageRequest;
import com.talhaatif.budgettracker.dto.transaction.TransactionCreateRequest;
import com.talhaatif.budgettracker.entities.User;
import com.talhaatif.budgettracker.services.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AiTransactionServiceImpl implements AiTransactionService {

    private final GeminiApiService geminiApiService;
    private final CategoryService categoryService;
    private final AccountService accountService;
    private final UserService userService;
    private final TransactionService transactionService;

    @Override
    public void processUserMessage(UserMessageRequest request) {
        User user = userService.getCurrentUserEntity();

        String userId = user.getId();
        // Step 1: AI Parsing
        GeminiParsedResponse parsed = geminiApiService.parseUserMessage(request.getMessage());

        // Step 2: Category
        String categoryId = categoryService.getOrCreateCategory(userId, parsed.getCategoryName());

        // Step 3: Account
        String accountId = (parsed.getAccountName() != null)
                ? accountService.getAccountIdByName(userId, parsed.getAccountName())
                : accountService.getFirstAccountIdForUser(userId);

        // Step 4: Build Transaction Request
        TransactionCreateRequest txRequest = new TransactionCreateRequest();
        txRequest.setAmount(BigDecimal.valueOf(parsed.getAmount()));
        txRequest.setDate(LocalDate.now());
        txRequest.setDescription(parsed.getDescription());
        txRequest.setType(parsed.getTransactionType());
        txRequest.setAccountId(accountId);
        txRequest.setCategoryId(categoryId);
        txRequest.setBudgetId(null); // no budget for now

        // Step 5: Save Transaction
        transactionService.createTransaction(txRequest);
    }
}

