package com.saga.project.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class AiReviewService {

    private final WebClient webClient;
    private final String apiKey;
    private final ObjectMapper objectMapper;

    public AiReviewService(WebClient.Builder webClientBuilder,
            @Value("${app.ai.grok.api-key:}") String apiKey,
            @Value("${app.ai.grok.base-url:https://api.x.ai/v1}") String baseUrl,
            ObjectMapper objectMapper) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
        this.apiKey = apiKey;
        this.objectMapper = objectMapper;
    }

    public record AiReviewResult(boolean valid, String reason) {
    }

    public AiReviewResult analyzeCommit(String taskDescription, String gitDiff) {
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("Grok API Key is missing. Skipping AI review.");
            return new AiReviewResult(true, "Skipped due to missing API key");
        }

        String systemPrompt = """
                You are a strict Senior Developer performing a Code Review.
                You must output ONLY a raw JSON object with no markdown formatting.
                The JSON object must have exactly two fields:
                1. "valid" (boolean): true if the code strictly addresses the task and contains no glaring bugs. false if the code is completely unrelated, seems malicious, or contains obvious logic errors.
                2. "reason" (string): A short explanation of why it is valid or invalid. If invalid, explain the bug or mismatch in Vietnamese.
                """;

        String userPrompt = String.format("Task Description:\n%s\n\nGit Diff:\n%s", taskDescription, gitDiff);

        Map<String, Object> requestBody = Map.of(
                "model", "grok-beta",
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user", "content", userPrompt)),
                "temperature", 0.0);

        try {
            JsonNode response = webClient.post()
                    .uri("/chat/completions")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

            if (response != null && response.has("choices")) {
                String content = response.get("choices").get(0).get("message").get("content").asText();
                // Strip markdown formatting if AI still outputs it despite the prompt
                content = content.replace("```json", "").replace("```", "").trim();
                return objectMapper.readValue(content, AiReviewResult.class);
            }
        } catch (Exception e) {
            log.error("Failed to call Grok API", e);
            return new AiReviewResult(true, "AI API Error: " + e.getMessage());
        }

        return new AiReviewResult(true, "Default fallback");
    }
}
