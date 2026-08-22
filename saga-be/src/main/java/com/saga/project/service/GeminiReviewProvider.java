package com.saga.project.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.saga.project.dto.AiReviewResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service("gemini")
@Slf4j
public class GeminiReviewProvider implements AiReviewProvider {

    private final WebClient webClient;
    private final String apiKey;
    private final String model;
    private final ObjectMapper objectMapper;

    public GeminiReviewProvider(WebClient.Builder webClientBuilder,
            @Value("${app.ai.gemini.api-key:}") String apiKey,
            @Value("${app.ai.gemini.base-url:https://generativelanguage.googleapis.com/v1beta/models/}") String baseUrl,
            @Value("${app.ai.gemini.model:gemini-1.5-flash}") String model,
            ObjectMapper objectMapper) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
        this.apiKey = apiKey;
        this.model = model;
        this.objectMapper = objectMapper;
    }

    @Override
    public String getProviderName() {
        return "gemini";
    }

    @Override
    public AiReviewResult analyzeCommit(String taskDescription, String gitDiff) {
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("Gemini API Key is missing. Skipping AI review.");
            return new AiReviewResult(true, "Skipped due to missing API key");
        }

        String systemInstruction = """
                You are a strict Senior Developer performing a Code Review.
                You must output ONLY a raw JSON object with no markdown formatting.
                The JSON object must have exactly two fields:
                1. "valid" (boolean): true if the code strictly addresses the task and contains no glaring bugs. false if the code is completely unrelated, seems malicious, or contains obvious logic errors.
                2. "reason" (string): A short explanation of why it is valid or invalid. If invalid, explain the bug or mismatch in Vietnamese.
                """;

        String userPrompt = String.format("Task Description:\n%s\n\nGit Diff:\n%s", taskDescription, gitDiff);

        // Gemini API uses a different payload structure than OpenAI/Grok
        Map<String, Object> requestBody = Map.of(
                "system_instruction", Map.of(
                        "parts", Map.of("text", systemInstruction)),
                "contents", List.of(
                        Map.of("parts", List.of(Map.of("text", userPrompt)))),
                "generationConfig", Map.of(
                        "temperature", 0.0,
                        "responseMimeType", "application/json"));

        try {
            JsonNode response = webClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path(model + ":generateContent")
                            .queryParam("key", apiKey)
                            .build())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

            if (response != null && response.has("candidates")) {
                JsonNode candidate = response.get("candidates").get(0);
                if (candidate.has("content") && candidate.get("content").has("parts")) {
                    String content = candidate.get("content").get("parts").get(0).get("text").asText();
                    content = content.replace("```json", "").replace("```", "").trim();
                    return objectMapper.readValue(content, AiReviewResult.class);
                }
            }
        } catch (Exception e) {
            log.error("Failed to call Gemini API", e);
            return new AiReviewResult(true, "AI API Error: " + e.getMessage());
        }

        return new AiReviewResult(true, "Default fallback");
    }

    @Override
    public String generateProgressReport(String prompt) {
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("Gemini API Key is missing. Skipping report generation.");
            return "AI Report generation is unavailable because the API key is missing.";
        }

        String systemInstruction = """
                You are an AI assistant helping a Lecturer and a student Team analyze their software project progress.
                You will be provided with a list of recent Jira tasks and Git commits.
                Generate a concise, insightful Markdown progress report in Vietnamese.
                Highlight what is done, what is pending, and any potential risks or blocked items.
                """;

        Map<String, Object> requestBody = Map.of(
                "system_instruction", Map.of(
                        "parts", Map.of("text", systemInstruction)),
                "contents", List.of(
                        Map.of("parts", List.of(Map.of("text", prompt)))),
                "generationConfig", Map.of(
                        "temperature", 0.3));

        try {
            JsonNode response = webClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path(model + ":generateContent")
                            .queryParam("key", apiKey)
                            .build())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

            if (response != null && response.has("candidates")) {
                JsonNode candidate = response.get("candidates").get(0);
                if (candidate.has("content") && candidate.get("content").has("parts")) {
                    return candidate.get("content").get("parts").get(0).get("text").asText();
                }
            }
        } catch (Exception e) {
            log.error("Failed to call Gemini API for progress report", e);
            return "Error generating report: " + e.getMessage();
        }

        return "Failed to generate AI progress report.";
    }
}
