package com.saga.project.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class AiReviewServiceTest {

    private MockWebServer mockWebServer;
    private AiReviewService aiReviewService;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        WebClient.Builder webClientBuilder = WebClient.builder();
        ObjectMapper objectMapper = new ObjectMapper();
        aiReviewService = new AiReviewService(webClientBuilder, "dummy-api-key", mockWebServer.url("/").toString(), objectMapper);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void testAnalyzeCommit_ValidResponse() {
        String mockResponse = "{\"id\":\"chatcmpl-123\",\"choices\":[{\"message\":{\"role\":\"assistant\",\"content\":\"{\\\"valid\\\": true, \\\"reason\\\": \\\"Code implements the requested feature properly.\\\"}\"}}]}";
        mockWebServer.enqueue(new MockResponse()
                .setBody(mockResponse)
                .addHeader("Content-Type", "application/json"));

        AiReviewService.AiReviewResult result = aiReviewService.analyzeCommit("Fix login bug", "diff --git a/file b/file");

        assertTrue(result.valid());
        assertEquals("Code implements the requested feature properly.", result.reason());
    }

    @Test
    void testAnalyzeCommit_InvalidResponse() {
        String mockResponse = "{\"id\":\"chatcmpl-123\",\"choices\":[{\"message\":{\"role\":\"assistant\",\"content\":\"```json\\n{\\\"valid\\\": false, \\\"reason\\\": \\\"Code doesn't match Jira description.\\\"}\\n```\"}}]}";
        mockWebServer.enqueue(new MockResponse()
                .setBody(mockResponse)
                .addHeader("Content-Type", "application/json"));

        AiReviewService.AiReviewResult result = aiReviewService.analyzeCommit("Fix login bug", "diff --git a/file b/file");

        assertFalse(result.valid());
        assertEquals("Code doesn't match Jira description.", result.reason());
    }

    @Test
    void testAnalyzeCommit_MissingApiKey() {
        AiReviewService serviceWithoutKey = new AiReviewService(WebClient.builder(), "", mockWebServer.url("/").toString(), new ObjectMapper());
        AiReviewService.AiReviewResult result = serviceWithoutKey.analyzeCommit("Task", "Diff");

        assertTrue(result.valid());
        assertEquals("Skipped due to missing API key", result.reason());
    }
}
