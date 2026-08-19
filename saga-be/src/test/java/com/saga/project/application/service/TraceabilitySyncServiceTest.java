package com.saga.project.application.service;

import com.saga.project.application.dto.GithubWebhookPayload;
import com.saga.project.infrastructure.persistence.entity.CommitDataEntity;
import com.saga.project.infrastructure.persistence.entity.GitRepoEntity;
import com.saga.project.infrastructure.persistence.entity.TaskEntity;
import com.saga.project.infrastructure.persistence.repository.JpaCommitDataRepository;
import com.saga.project.infrastructure.persistence.repository.JpaGitRepoRepository;
import com.saga.project.infrastructure.persistence.repository.JpaTaskCommitLinkRepository;
import com.saga.project.infrastructure.persistence.repository.JpaTaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TraceabilitySyncServiceTest {

    @Mock
    private JpaGitRepoRepository gitRepoRepository;

    @Mock
    private JpaCommitDataRepository commitDataRepository;

    @Mock
    private JpaTaskRepository taskRepository;

    @Mock
    private JpaTaskCommitLinkRepository taskCommitLinkRepository;

    @InjectMocks
    private TraceabilitySyncService traceabilitySyncService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void handleGithubWebhook_ValidPayload_ShouldLinkCommitToTask() {
        // Mock Payload
        GithubWebhookPayload payload = new GithubWebhookPayload();
        payload.setRef("refs/heads/main");
        
        GithubWebhookPayload.Repository repo = new GithubWebhookPayload.Repository();
        repo.setId("ext-repo-123");
        payload.setRepository(repo);
        
        GithubWebhookPayload.Commit commit = new GithubWebhookPayload.Commit();
        commit.setId("hash123");
        commit.setMessage("Fix issue SAGA-45 for the login screen");
        GithubWebhookPayload.Commit.Author author = new GithubWebhookPayload.Commit.Author();
        author.setEmail("test@saga.com");
        commit.setAuthor(author);
        payload.setCommits(Collections.singletonList(commit));
        
        // Mock GitRepo lookup
        GitRepoEntity gitRepoEntity = new GitRepoEntity();
        gitRepoEntity.setId(UUID.randomUUID());
        when(gitRepoRepository.findByRepoId("ext-repo-123")).thenReturn(Optional.of(gitRepoEntity));
        
        // Mock Commit verification (not exists)
        when(commitDataRepository.findByHash("hash123")).thenReturn(Optional.empty());
        
        // Mock Commit save
        CommitDataEntity savedCommit = new CommitDataEntity();
        savedCommit.setId(UUID.randomUUID());
        when(commitDataRepository.save(any(CommitDataEntity.class))).thenReturn(savedCommit);
        
        // Mock Task lookup
        TaskEntity task = new TaskEntity();
        task.setId(UUID.randomUUID());
        task.setIssueKey("SAGA-45");
        when(taskRepository.findByIssueKey("SAGA-45")).thenReturn(Optional.of(task));
        
        // Execute
        traceabilitySyncService.handleGithubWebhook(payload);
        
        // Verify
        verify(commitDataRepository, times(1)).save(any(CommitDataEntity.class));
        verify(taskRepository, times(1)).findByIssueKey("SAGA-45");
        verify(taskCommitLinkRepository, times(1)).save(any());
    }
}
