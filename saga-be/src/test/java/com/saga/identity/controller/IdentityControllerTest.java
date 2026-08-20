package com.saga.identity.controller;

import com.saga.user.repository.JpaUserRepository;
import com.saga.identity.service.IdentityService;
import com.saga.identity.entity.ExternalProvider;
import com.saga.identity.entity.IdentityMap;
import com.saga.user.entity.User;
import com.saga.shared.response.ApiResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IdentityControllerTest {

    @Mock
    private IdentityService identityService;

    @Mock
    private JpaUserRepository userRepository;

    @InjectMocks
    private IdentityController identityController;

    private UUID userId;
    private MockedStatic<SecurityContextHolder> mockedSecurityContextHolder;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        User mockUser = User.builder().id(userId).email("test@fpt.edu.vn").build();
        when(userRepository.findByEmail("test@fpt.edu.vn")).thenReturn(Optional.of(mockUser));

        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn("test@fpt.edu.vn");

        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        mockedSecurityContextHolder = Mockito.mockStatic(SecurityContextHolder.class);
        mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
    }

    @AfterEach
    void tearDown() {
        mockedSecurityContextHolder.close();
    }

    @Test
    void testGetMyIdentities() {
        IdentityMap map = IdentityMap.builder()
                .id(UUID.randomUUID())
                .internalUserId(userId)
                .externalProvider(ExternalProvider.GITHUB)
                .externalId("test@github.com")
                .build();

        when(identityService.getIdentities(any(UUID.class))).thenReturn(List.of(map));

        ResponseEntity<ApiResponse<List<IdentityMap>>> response = identityController.getMyIdentities();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().getData().size());
        assertEquals(ExternalProvider.GITHUB, response.getBody().getData().get(0).getExternalProvider());
    }

    @Test
    void testLinkGithub() {
        ResponseEntity<ApiResponse<String>> response = identityController.linkGithub(Map.of("code", "github-code-123"));

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Linked GitHub", response.getBody().getData());
        verify(identityService).linkGithub(eq(userId), eq("github-code-123"));
    }

    @Test
    void testLinkJira() {
        ResponseEntity<ApiResponse<String>> response = identityController.linkJira(Map.of("code", "jira-code-123"));

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Linked Jira", response.getBody().getData());
        verify(identityService).linkJira(eq(userId), eq("jira-code-123"));
    }
}
