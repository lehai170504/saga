package com.saga.auth.service;

import com.saga.auth.dto.UserProfileDTO;
import com.saga.shared.exception.UnauthorizedException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import org.mockito.ArgumentMatchers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoogleAuthServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private GoogleAuthService googleAuthService;

    @Test
    void verifyToken_Success() {
        String token = "valid_token";
        Map<String, Object> mockPayload = Map.of(
                "email", "test@fpt.edu.vn",
                "name", "Test User",
                "picture", "pic.jpg");
        ResponseEntity<Map<String, Object>> responseEntity = ResponseEntity.ok(mockPayload);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class),
                ArgumentMatchers.<org.springframework.core.ParameterizedTypeReference<Map<String, Object>>>any()))
                .thenReturn(responseEntity);

        UserProfileDTO profile = googleAuthService.verifyToken(token);

        assertNotNull(profile);
        assertEquals("test@fpt.edu.vn", profile.getEmail());
        assertEquals("Test User", profile.getName());
    }

    @Test
    void verifyToken_InvalidToken_ThrowsUnauthorizedException() {
        String token = "invalid_token";
        Map<String, Object> mockPayload = Map.of("error", "invalid_token");
        ResponseEntity<Map<String, Object>> responseEntity = ResponseEntity.ok(mockPayload);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class),
                ArgumentMatchers.<org.springframework.core.ParameterizedTypeReference<Map<String, Object>>>any()))
                .thenReturn(responseEntity);

        UnauthorizedException exception = assertThrows(UnauthorizedException.class,
                () -> googleAuthService.verifyToken(token));
        assertTrue(exception.getMessage().contains("Invalid Google Token"));
    }

    @Test
    void verifyToken_RestError_ThrowsUnauthorizedException() {
        String token = "error_token";
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class),
                ArgumentMatchers.<org.springframework.core.ParameterizedTypeReference<Map<String, Object>>>any()))
                .thenThrow(new RuntimeException("Connection timeout"));

        UnauthorizedException exception = assertThrows(UnauthorizedException.class,
                () -> googleAuthService.verifyToken(token));
        assertTrue(exception.getMessage().contains("Failed to verify Google Token"));
    }
}
