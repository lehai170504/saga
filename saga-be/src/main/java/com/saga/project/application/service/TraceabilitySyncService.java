package com.saga.project.application.service;

import com.saga.project.application.dto.GithubWebhookPayload;
import com.saga.project.infrastructure.persistence.entity.CommitDataEntity;
import com.saga.project.infrastructure.persistence.entity.GitRepoEntity;
import com.saga.project.infrastructure.persistence.entity.TaskCommitLinkEntity;
import com.saga.project.infrastructure.persistence.entity.TaskEntity;
import com.saga.project.infrastructure.persistence.repository.JpaCommitDataRepository;
import com.saga.project.infrastructure.persistence.repository.JpaGitRepoRepository;
import com.saga.project.infrastructure.persistence.repository.JpaTaskCommitLinkRepository;
import com.saga.project.infrastructure.persistence.repository.JpaTaskRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TraceabilitySyncService {

    private final JpaGitRepoRepository gitRepoRepository;
    private final JpaCommitDataRepository commitDataRepository;
    private final JpaTaskRepository taskRepository;
    private final JpaTaskCommitLinkRepository taskCommitLinkRepository;

    private static final Pattern JIRA_KEY_PATTERN = Pattern.compile("([A-Z]+-[0-9]+)");

    public TraceabilitySyncService(JpaGitRepoRepository gitRepoRepository,
                                   JpaCommitDataRepository commitDataRepository,
                                   JpaTaskRepository taskRepository,
                                   JpaTaskCommitLinkRepository taskCommitLinkRepository) {
        this.gitRepoRepository = gitRepoRepository;
        this.commitDataRepository = commitDataRepository;
        this.taskRepository = taskRepository;
        this.taskCommitLinkRepository = taskCommitLinkRepository;
    }

    @Async
    @Transactional
    public void handleGithubWebhook(GithubWebhookPayload payload) {
        try {
        if (payload == null || payload.getRepository() == null || payload.getCommits() == null) {
            return; // Invalid payload
        }

        String externalRepoId = payload.getRepository().getId();
        Optional<GitRepoEntity> repoOpt = gitRepoRepository.findByRepoId(externalRepoId);
        
        if (repoOpt.isEmpty()) {
            return; // Repo not linked in our system
        }
        GitRepoEntity repo = repoOpt.get();

        String branchName = "unknown";
        if (payload.getRef() != null && payload.getRef().startsWith("refs/heads/")) {
            branchName = payload.getRef().substring(11); // Extract branch name after refs/heads/
        }

        for (GithubWebhookPayload.Commit commit : payload.getCommits()) {
            // Avoid duplicate commits
            if (commitDataRepository.findByHash(commit.getId()).isPresent()) {
                continue; 
            }

            // Save Commit
            CommitDataEntity commitEntity = new CommitDataEntity();
            commitEntity.setRepoId(repo.getId());
            commitEntity.setHash(commit.getId());
            commitEntity.setMessage(commit.getMessage());
            commitEntity.setBranchName(branchName);
            if (commit.getAuthor() != null) {
                commitEntity.setAuthorEmail(commit.getAuthor().getEmail());
            }
            commitEntity = commitDataRepository.save(commitEntity);

            // Data Linkage via Regex
            if (commit.getMessage() != null) {
                Matcher matcher = JIRA_KEY_PATTERN.matcher(commit.getMessage());
                while (matcher.find()) {
                    String issueKey = matcher.group(1);
                    Optional<TaskEntity> taskOpt = taskRepository.findByIssueKey(issueKey);
                    
                    if (taskOpt.isPresent()) {
                        TaskEntity task = taskOpt.get();
                        TaskCommitLinkEntity link = new TaskCommitLinkEntity();
                        link.setTaskId(task.getId());
                        link.setCommitId(commitEntity.getId());
                        taskCommitLinkRepository.save(link);
                    }
                }
            }
        }
        } catch (Exception e) {
            // Log error so the async thread doesn't just die silently
            System.err.println("Error processing webhook: " + e.getMessage());
        }
    }
}
