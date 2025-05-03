package com.talhaatif.budgettracker.services.impl;

import com.talhaatif.budgettracker.dto.ai.GeminiApiResponse;
import com.talhaatif.budgettracker.dto.ai.GeminiParsedResponse;
import com.talhaatif.budgettracker.services.GeminiApiService;
import com.talhaatif.budgettracker.utils.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeminiApiServiceImpl implements GeminiApiService {

    private final RestTemplate restTemplate;

    private String geminiApiKey=""; // will secure


    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=%s";

    @Override
    public GeminiParsedResponse parseUserMessage(String userMessage) {
        log.info("Calling Gemini API for message: {}", userMessage);

        // 1️⃣ Prepare the prompt
        String prompt = buildPrompt(userMessage);

        // 2️⃣ Prepare the request body
        Map<String, Object> body = new HashMap<>();
        body.put("contents", List.of(
                Map.of("parts", List.of(Map.of("text", prompt)))
        ));

        // 3️⃣ Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // 4️⃣ Call Gemini API
        String url = String.format(GEMINI_API_URL, geminiApiKey);
        ResponseEntity<GeminiApiResponse> response = restTemplate.exchange(
                url, HttpMethod.POST, requestEntity, GeminiApiResponse.class
        );

        // 5️⃣ Extract AI response text
        String aiText = response.getBody()
                .getCandidates()
                .get(0)
                .getContent()
                .getParts()
                .get(0)
                .getText();

        log.info("AI raw response: {}", aiText);

        // 6️⃣ Convert AI text (assumed JSON) to GeminiParsedResponse
        return JsonUtils.fromJson(cleanAIResponse(aiText), GeminiParsedResponse.class);
    }

    private String buildPrompt(String userMessage) {
        return "You are a financial assistant AI. Extract transaction details from the user's message.\n" +
                "Respond with only a raw JSON object — no markdown, no explanation, no text.\n" +
                "JSON format:\n" +
                "{ \"amount\": number, \"categoryName\": string, \"accountName\": string, \"description\": string, \"transactionType\": \"EXPENSE\" or \"INCOME\" }\n" +
                "User message: \"" + userMessage + "\"";
    }


    private String cleanAIResponse(String response) {
        return response
                .replaceAll("(?i)```json", "")
                .replaceAll("(?i)```", "")
                .trim();
    }



}

