package com.saga.project.service;

import com.saga.project.dto.GithubWebhookPayload;
import com.saga.project.entity.CommitData;
import com.saga.project.entity.GitRepo;
import com.saga.project.entity.Task;
import com.saga.project.repository.JpaCommitDataRepository;
import com.saga.project.repository.JpaGitRepoRepository;
import com.saga.project.repository.JpaTaskCommitLinkRepository;
import com.saga.project.repository.JpaTaskRepository;
import com.saga.project.graph.CommitNodeRepository;
import com.saga.project.graph.JiraTaskNodeRepository;
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
    private CommitNodeRepository commitNodeRepository;

    @Mock
    private JiraTaskNodeRepository jiraTaskNodeRepository;

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

        GitRepo gitRepoEntity = new GitRepo();
        gitRepoEntity.setId(UUID.randomUUID());
        when(gitRepoRepository.findByRepoId("ext-repo-123")).thenReturn(Optional.of(gitRepoEntity));

        when(commitDataRepository.findByHash("hash123")).thenReturn(Optional.empty());

        CommitData savedCommit = new CommitData();
        savedCommit.setId(UUID.randomUUID());
        when(commitDataRepository.save(any(CommitData.class))).thenReturn(savedCommit);

        Task task = new Task();
        task.setId(UUID.randomUUID());
        task.setIssueKey("SAGA-45");
        when(taskRepository.findByIssueKey("SAGA-45")).thenReturn(Optional.of(task));

        traceabilitySyncService.handleGithubWebhook(payload);

        verify(commitDataRepository, times(1)).save(any(CommitData.class));
        verify(taskRepository, times(1)).findByIssueKey("SAGA-45");
        verify(taskCommitLinkRepository, times(1)).save(any());
    }
}
