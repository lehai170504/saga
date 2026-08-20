package com.saga.project.service;

import com.saga.academic.service.TeamValidationService;
import com.saga.project.repository.JpaGitRepoRepository;
import com.saga.project.repository.JpaJiraBoardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.security.access.AccessDeniedException;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProjectIntegrationServiceTest {

    @Mock
    private JpaJiraBoardRepository jiraBoardRepository;

    @Mock
    private JpaGitRepoRepository gitRepoRepository;

    @Mock
    private TeamValidationService teamValidationPort;

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    private ProjectIntegrationService projectIntegrationService;
    private UUID userId;
    private UUID teamId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(webClientBuilder.build()).thenReturn(webClient);
        projectIntegrationService = new ProjectIntegrationService(
                jiraBoardRepository, gitRepoRepository, teamValidationPort, webClientBuilder);
        userId = UUID.randomUUID();
        teamId = UUID.randomUUID();
    }

    @Test
    void generateJiraConnectUrl_AsLeader_ShouldReturnUrl() {
        when(teamValidationPort.isLeader(userId, teamId)).thenReturn(true);
        String url = projectIntegrationService.generateJiraConnectUrl(userId, teamId);
        assertNotNull(url);
        assertTrue(url.contains("state=" + teamId.toString()));
    }

    @Test
    void generateJiraConnectUrl_NotLeader_ShouldThrowAccessDeniedException() {
        when(teamValidationPort.isLeader(userId, teamId)).thenReturn(false);
        assertThrows(AccessDeniedException.class,
                () -> projectIntegrationService.generateJiraConnectUrl(userId, teamId));
    }
}
