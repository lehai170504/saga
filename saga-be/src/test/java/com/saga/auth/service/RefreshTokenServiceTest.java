package com.saga.auth.service;

import com.saga.auth.entity.RefreshToken;
import com.saga.auth.repository.RefreshTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createRefreshToken_ShouldReturnSavedToken() {
        UUID userId = UUID.randomUUID();
        String email = "test@saga.com";
        RefreshToken mockToken = RefreshToken.builder()
                .token("random-token-string")
                .userId(userId)
                .email(email)
                .expiration(604800L)
                .build();

        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(mockToken);

        RefreshToken result = refreshTokenService.createRefreshToken(userId, email);

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(email, result.getEmail());
        verify(refreshTokenRepository, times(1)).save(any(RefreshToken.class));
    }

    @Test
    void findByToken_ShouldReturnOptionalToken() {
        RefreshToken mockToken = RefreshToken.builder().token("abc").build();
        when(refreshTokenRepository.findById("abc")).thenReturn(Optional.of(mockToken));

        Optional<RefreshToken> result = refreshTokenService.findByToken("abc");
        assertTrue(result.isPresent());
        assertEquals("abc", result.get().getToken());
    }
}
