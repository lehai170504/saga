package com.saga.project.service;

import com.saga.academic.service.TeamValidationService;
import com.saga.project.dto.GithubConfirmRequest;
import com.saga.project.dto.JiraConfirmRequest;
import com.saga.project.entity.GitRepo;
import com.saga.project.entity.JiraBoard;
import com.saga.project.repository.JpaGitRepoRepository;
import com.saga.project.repository.JpaJiraBoardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.Arrays;

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

    @Mock
    private InitialSyncService initialSyncService;

    private ProjectIntegrationService projectIntegrationService;
    private UUID userId;
    private UUID teamId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(webClientBuilder.build()).thenReturn(webClient);
        projectIntegrationService = new ProjectIntegrationService(
                jiraBoardRepository, gitRepoRepository, teamValidationPort, initialSyncService, webClientBuilder);
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
    void confirmJiraProject_Success() {
        when(teamValidationPort.isLeader(userId, teamId)).thenReturn(true);
        when(jiraBoardRepository.findByTeamId(teamId)).thenReturn(Optional.empty());
        
        JiraConfirmRequest req = new JiraConfirmRequest();
        req.setSiteId("site-123");
        req.setProjectKey("SAGA");
        req.setBoardName("SAGA Board");
        
        JiraBoard mockBoard = new JiraBoard();
        mockBoard.setProjectKey("SAGA");
        when(jiraBoardRepository.save(any(JiraBoard.class))).thenReturn(mockBoard);
        
        JiraBoard res = projectIntegrationService.confirmJiraProject(userId, teamId, req);
        assertNotNull(res);
        assertEquals("SAGA", res.getProjectKey());
        
        verify(jiraBoardRepository, times(1)).save(any(JiraBoard.class));
    }

    @Test
    void confirmJiraProject_AlreadyExists_ThrowsException() {
        when(teamValidationPort.isLeader(userId, teamId)).thenReturn(true);
        when(jiraBoardRepository.findByTeamId(teamId)).thenReturn(Optional.of(new JiraBoard()));
        
        JiraConfirmRequest req = new JiraConfirmRequest();
        req.setSiteId("site-123");
        req.setProjectKey("SAGA");
        
        assertThrows(IllegalStateException.class, () -> projectIntegrationService.confirmJiraProject(userId, teamId, req));
    }
    
    @Test
    void unlinkJira_Success() {
        when(teamValidationPort.isLeader(userId, teamId)).thenReturn(true);
        JiraBoard board = new JiraBoard();
        when(jiraBoardRepository.findByTeamId(teamId)).thenReturn(Optional.of(board));
        
        projectIntegrationService.unlinkJira(userId, teamId);
        
        verify(jiraBoardRepository, times(1)).delete(board);
    }
    
    @Test
    void confirmGithubRepos_Success() {
        when(teamValidationPort.isLeader(userId, teamId)).thenReturn(true);
        // Mock the handle callback to set temp state
        projectIntegrationService.handleGithubCallback(userId, "inst-123", teamId.toString());
        
        GithubConfirmRequest req = new GithubConfirmRequest();
        req.setRepoUrls(Arrays.asList("https://github.com/fpt/repo1", "https://github.com/fpt/repo2"));
        
        when(gitRepoRepository.findByRepoUrl(anyString())).thenReturn(Optional.empty());
        when(gitRepoRepository.save(any(GitRepo.class))).thenAnswer(i -> i.getArguments()[0]);
        
        List<GitRepo> res = projectIntegrationService.confirmGithubRepos(userId, teamId, req);
        
        assertEquals(2, res.size());
        verify(gitRepoRepository, times(2)).save(any(GitRepo.class));
    }
    
    @Test
    void unlinkGithub_Success() {
        when(teamValidationPort.isLeader(userId, teamId)).thenReturn(true);
        List<GitRepo> repos = Arrays.asList(new GitRepo(), new GitRepo());
        when(gitRepoRepository.findAllByTeamId(teamId)).thenReturn(repos);
        
        projectIntegrationService.unlinkGithub(userId, teamId);
        
        verify(gitRepoRepository, times(1)).deleteAll(repos);
    }
}



