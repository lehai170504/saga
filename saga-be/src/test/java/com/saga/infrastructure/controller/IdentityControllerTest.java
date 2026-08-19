package com.saga.infrastructure.controller;

import com.saga.application.port.JwtProviderPort;
import com.saga.application.port.TokenBlacklistPort;
import com.saga.application.port.UserRepositoryPort;
import com.saga.application.service.IdentityService;
import com.saga.domain.ExternalProvider;
import com.saga.domain.IdentityMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(IdentityController.class)
@AutoConfigureMockMvc(addFilters = false) // Disable security for simple controller unit test
class IdentityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IdentityService identityService;

    // Mock beans required by SecurityConfig
    @MockBean
    private JwtProviderPort jwtProviderPort;

    @MockBean
    private TokenBlacklistPort tokenBlacklistPort;

    @MockBean
    private UserRepositoryPort userRepositoryPort;

    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
    }

    @Test
    void testGetMyIdentities() throws Exception {
        IdentityMap map = IdentityMap.builder()
                .id(UUID.randomUUID())
                .internalUserId(userId)
                .externalProvider(ExternalProvider.GITHUB)
                .externalId("test@github.com")
                .build();

        when(identityService.getIdentities(any(UUID.class))).thenReturn(List.of(map));

        mockMvc.perform(get("/api/v1/identities/me")
                .requestAttr("userId", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].externalProvider").value("GITHUB"))
                .andExpect(jsonPath("$.data[0].externalId").value("test@github.com"));
    }

    @Test
    void testLinkGithub() throws Exception {
        mockMvc.perform(post("/api/v1/identities/github/callback")
                .requestAttr("userId", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"code\": \"github-code-123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("GitHub linked successfully"));

        verify(identityService).linkGithub(eq(userId), eq("github-code-123"));
    }

    @Test
    void testLinkJira() throws Exception {
        mockMvc.perform(post("/api/v1/identities/jira/callback")
                .requestAttr("userId", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"code\": \"jira-code-123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Jira linked successfully"));

        verify(identityService).linkJira(eq(userId), eq("jira-code-123"));
    }
}
