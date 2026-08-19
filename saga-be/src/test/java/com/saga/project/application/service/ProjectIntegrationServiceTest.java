package com.saga.project.application.service;

import com.saga.project.application.port.TeamValidationPort;
import com.saga.project.domain.IntegrationStatus;
import com.saga.project.domain.JiraBoard;
import com.saga.project.infrastructure.persistence.entity.JiraBoardEntity;
import com.saga.project.infrastructure.persistence.repository.JpaGitRepoRepository;
import com.saga.project.infrastructure.persistence.repository.JpaJiraBoardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProjectIntegrationServiceTest {

    @Mock
    private JpaJiraBoardRepository jiraBoardRepository;

    @Mock
    private JpaGitRepoRepository gitRepoRepository;

    @Mock
    private TeamValidationPort teamValidationPort;

    @InjectMocks
    private ProjectIntegrationService projectIntegrationService;

    private UUID userId;
    private UUID teamId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
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

    @Test
    void handleJiraCallback_AsLeader_ShouldSaveJiraBoard() {
        when(teamValidationPort.isLeader(userId, teamId)).thenReturn(true);

        JiraBoardEntity mockSavedEntity = new JiraBoardEntity();
        mockSavedEntity.setId(UUID.randomUUID());
        mockSavedEntity.setTeamId(teamId);
        mockSavedEntity.setBoardId("JIRA-BOARD-999");
        mockSavedEntity.setProjectKey("SAGA");
        mockSavedEntity.setStatus(IntegrationStatus.LINKED);
        mockSavedEntity.setLinkedAt(LocalDateTime.now());

        when(jiraBoardRepository.save(any(JiraBoardEntity.class))).thenReturn(mockSavedEntity);

        JiraBoard board = projectIntegrationService.handleJiraCallback(userId, "auth_code_123", teamId.toString());

        assertNotNull(board);
        assertEquals("JIRA-BOARD-999", board.getBoardId());
        assertEquals(teamId, board.getTeamId());
        verify(jiraBoardRepository, times(1)).save(any(JiraBoardEntity.class));
    }
}
