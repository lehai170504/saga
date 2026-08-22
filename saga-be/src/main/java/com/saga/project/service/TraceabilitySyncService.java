package com.saga.project.service;

import com.saga.project.dto.GithubWebhookPayload;
import com.saga.project.dto.JiraWebhookPayload;
import com.saga.project.entity.CommitData;
import com.saga.project.entity.GitRepo;
import com.saga.project.entity.TaskCommitLink;
import com.saga.project.entity.Task;
import com.saga.project.graph.CommitNode;
import com.saga.project.graph.CommitNodeRepository;
import com.saga.project.graph.JiraTaskNode;
import com.saga.project.graph.JiraTaskNodeRepository;
import com.saga.project.entity.TaskAttachment;
import com.saga.project.repository.JpaCommitDataRepository;
import com.saga.project.repository.JpaGitRepoRepository;
import com.saga.project.repository.JpaTaskCommitLinkRepository;
import com.saga.project.repository.JpaTaskRepository;
import com.saga.project.repository.JpaJiraBoardRepository;
import com.saga.project.entity.JiraBoard;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TraceabilitySyncService {

    private final JpaGitRepoRepository gitRepoRepository;
    private final JpaCommitDataRepository commitDataRepository;
    private final JpaTaskRepository taskRepository;
    private final JpaTaskCommitLinkRepository taskCommitLinkRepository;
    private final JiraTaskNodeRepository jiraTaskNodeRepository;
    private final CommitNodeRepository commitNodeRepository;
    private final JpaJiraBoardRepository jiraBoardRepository;

    private static final Pattern JIRA_KEY_PATTERN = Pattern.compile("([A-Z]+-[0-9]+)");

    public TraceabilitySyncService(JpaGitRepoRepository gitRepoRepository,
            JpaCommitDataRepository commitDataRepository,
            JpaTaskRepository taskRepository,
            JpaTaskCommitLinkRepository taskCommitLinkRepository,
            JiraTaskNodeRepository jiraTaskNodeRepository,
            CommitNodeRepository commitNodeRepository,
            JpaJiraBoardRepository jiraBoardRepository) {
        this.gitRepoRepository = gitRepoRepository;
        this.commitDataRepository = commitDataRepository;
        this.taskRepository = taskRepository;
        this.taskCommitLinkRepository = taskCommitLinkRepository;
        this.jiraTaskNodeRepository = jiraTaskNodeRepository;
        this.commitNodeRepository = commitNodeRepository;
        this.jiraBoardRepository = jiraBoardRepository;
    }

    @Async
    @Transactional
    public void handleJiraWebhook(JiraWebhookPayload payload) {
        log.info("Received Jira webhook event: {}", payload.getWebhookEvent());
        try {
            if (payload == null || payload.getIssue() == null)
                return;

            String issueKey = payload.getIssue().getKey();
            Optional<Task> taskOpt = taskRepository.findByIssueKey(issueKey);
            Task task;
            if (taskOpt.isPresent()) {
                task = taskOpt.get();
            } else {
                task = new Task();
                task.setIssueKey(issueKey);
                if (issueKey != null && issueKey.contains("-")) {
                    String projectKey = issueKey.split("-")[0];
                    java.util.Optional<JiraBoard> boardOpt = jiraBoardRepository.findByProjectKey(projectKey);
                    if (boardOpt.isPresent()) {
                        task.setBoardId(boardOpt.get().getId());
                    }
                }
            }

            if (payload.getIssue().getFields() != null) {
                task.setSummary(payload.getIssue().getFields().getSummary());
                if (payload.getIssue().getFields().getStatus() != null) {
                    task.setStatus(payload.getIssue().getFields().getStatus().getName());
                }
                if (payload.getIssue().getFields().getAttachment() != null) {
                    java.util.List<TaskAttachment> attachments = payload.getIssue().getFields().getAttachment()
                            .stream()
                            .map(a -> TaskAttachment.builder()
                                    .filename(a.getFilename())
                                    .url(a.getContent())
                                    .build())
                            .collect(java.util.stream.Collectors.toList());
                    task.setAttachments(attachments);
                }
            }
            taskRepository.save(task);

            JiraTaskNode taskNode = jiraTaskNodeRepository.findByIssueKey(payload.getIssue().getKey())
                    .orElse(new JiraTaskNode());
            taskNode.setIssueKey(task.getIssueKey());
            taskNode.setStatus(task.getStatus());
            jiraTaskNodeRepository.save(taskNode);
            log.info("Updated Jira Task: {} with summary and attachments", issueKey);
        } catch (Exception e) {
            log.error("Error processing Jira webhook", e);
        }
    }

    @Async
    @Transactional
    public void handleGithubWebhook(GithubWebhookPayload payload) {
        try {
            if (payload == null || payload.getRepository() == null || payload.getCommits() == null) {
                return; // Invalid payload
            }

            String externalRepoId = payload.getRepository().getId();
            Optional<GitRepo> repoOpt = gitRepoRepository.findByRepoId(externalRepoId);

            if (repoOpt.isEmpty()) {
                return; // Repo not linked in our system
            }
            GitRepo repo = repoOpt.get();

            String branchName = "unknown";
            if (payload.getRef() != null && payload.getRef().startsWith("refs/heads/")) {
                branchName = payload.getRef().substring(11); // Extract branch name after refs/heads/
            }

            for (GithubWebhookPayload.Commit commit : payload.getCommits()) {
                if (commitDataRepository.findByHash(commit.getId()).isPresent()) {
                    continue;
                }

                CommitData commitEntity = new CommitData();
                commitEntity.setRepoId(repo.getId());
                commitEntity.setHash(commit.getId());
                commitEntity.setMessage(commit.getMessage());
                commitEntity.setBranchName(branchName);
                if (commit.getAuthor() != null) {
                    commitEntity.setAuthorEmail(commit.getAuthor().getEmail());
                }
                commitEntity = commitDataRepository.save(commitEntity);

                CommitNode commitNode = commitNodeRepository.findByHash(commit.getId()).orElse(new CommitNode());
                commitNode.setHash(commit.getId());
                commitNode.setMessage(commit.getMessage());
                commitNode.setTimestamp(java.time.LocalDateTime.now().toString());
                commitNodeRepository.save(commitNode);

                if (commit.getMessage() != null) {
                    Matcher matcher = JIRA_KEY_PATTERN.matcher(commit.getMessage());
                    while (matcher.find()) {
                        String issueKey = matcher.group(1);
                        Optional<Task> taskOpt = taskRepository.findByIssueKey(issueKey);

                        Task task;
                        if (taskOpt.isPresent()) {
                            task = taskOpt.get();
                        } else {
                            task = new Task();
                            task.setIssueKey(issueKey);
                            task.setStatus("PENDING");
                            if (issueKey != null && issueKey.contains("-")) {
                                String projectKey = issueKey.split("-")[0];
                                java.util.Optional<JiraBoard> boardOpt = jiraBoardRepository.findByProjectKey(projectKey);
                                if (boardOpt.isPresent()) {
                                    task.setBoardId(boardOpt.get().getId());
                                }
                            }
                            task = taskRepository.save(task);
                        }

                        TaskCommitLink link = new TaskCommitLink();
                        link.setTaskId(task.getId());
                        link.setCommitId(commitEntity.getId());
                        taskCommitLinkRepository.save(link);

                        JiraTaskNode taskNode = jiraTaskNodeRepository.findByIssueKey(issueKey).orElse(null);
                        if (taskNode == null) {
                            taskNode = new JiraTaskNode();
                            taskNode.setIssueKey(issueKey);
                            taskNode.setStatus("PENDING");
                            taskNode = jiraTaskNodeRepository.save(taskNode);
                        }
                        
                        commitNode.getImplementsTasks().add(taskNode);
                        commitNodeRepository.save(commitNode);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error processing Github webhook", e);
        }
    }
}
