package com.saga.project.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saga.project.dto.AiReviewResult;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class GeminiReviewProviderTest {

    private MockWebServer mockWebServer;
    private GeminiReviewProvider geminiReviewProvider;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        WebClient.Builder webClientBuilder = WebClient.builder();
        ObjectMapper objectMapper = new ObjectMapper();
        geminiReviewProvider = new GeminiReviewProvider(webClientBuilder, "dummy-api-key",
                mockWebServer.url("/").toString(), "gemini-1.5-flash", objectMapper);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void testAnalyzeCommit_ValidResponse() {
        String mockResponse = "{\"candidates\":[{\"content\":{\"parts\":[{\"text\":\"{\\\"valid\\\": true, \\\"reason\\\": \\\"Code is good.\\\"}\"}]}}]}";
        mockWebServer.enqueue(new MockResponse()
                .setBody(mockResponse)
                .addHeader("Content-Type", "application/json"));

        AiReviewResult result = geminiReviewProvider.analyzeCommit("Task", "Diff");

        assertTrue(result.valid());
        assertEquals("Code is good.", result.reason());
    }

    @Test
    void testAnalyzeCommit_InvalidResponse() {
        String mockResponse = "{\"candidates\":[{\"content\":{\"parts\":[{\"text\":\"```json\\n{\\\"valid\\\": false, \\\"reason\\\": \\\"Bad code.\\\"}\\n```\"}]}}]}";
        mockWebServer.enqueue(new MockResponse()
                .setBody(mockResponse)
                .addHeader("Content-Type", "application/json"));

        AiReviewResult result = geminiReviewProvider.analyzeCommit("Task", "Diff");

        assertFalse(result.valid());
        assertEquals("Bad code.", result.reason());
    }

    @Test
    void testAnalyzeCommit_MissingApiKey() {
        GeminiReviewProvider serviceWithoutKey = new GeminiReviewProvider(WebClient.builder(), "",
                mockWebServer.url("/").toString(), "gemini-1.5-flash", new ObjectMapper());
        AiReviewResult result = serviceWithoutKey.analyzeCommit("Task", "Diff");

        assertTrue(result.valid());
        assertEquals("Skipped due to missing API key", result.reason());
    }
}
