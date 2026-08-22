package com.saga.project.service;

import com.saga.project.entity.Task;
import com.saga.project.graph.CommitNode;
import com.saga.project.graph.CommitNodeRepository;
import com.saga.project.graph.JiraTaskNode;
import com.saga.project.graph.JiraTaskNodeRepository;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.saga.project.entity.TaskCommitLink;
import com.saga.project.repository.JpaTaskCommitLinkRepository;
import com.saga.project.entity.CommitData;
import com.saga.project.entity.JiraBoard;
import com.saga.project.entity.GitRepo;
import com.saga.project.entity.SyncStatus;
import com.saga.project.repository.JpaTaskRepository;
import com.saga.project.repository.JpaCommitDataRepository;
import com.saga.project.repository.JpaJiraBoardRepository;
import com.saga.project.repository.JpaGitRepoRepository;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.HttpHeaders;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class InitialSyncService {

    private final JpaTaskRepository taskRepository;
    private final JpaCommitDataRepository commitDataRepository;
    private final JpaJiraBoardRepository jiraBoardRepository;
    private final JpaGitRepoRepository gitRepoRepository;
    private final WebClient webClient;
    private final JiraTaskNodeRepository jiraTaskNodeRepository;
    private final CommitNodeRepository commitNodeRepository;
    private final JpaTaskCommitLinkRepository taskCommitLinkRepository;
    private static final Pattern JIRA_KEY_PATTERN = Pattern.compile("([A-Z]+-[0-9]+)");

    public InitialSyncService(JpaTaskRepository taskRepository, JpaCommitDataRepository commitDataRepository,
            JpaJiraBoardRepository jiraBoardRepository, JpaGitRepoRepository gitRepoRepository,
            WebClient.Builder webClientBuilder, JiraTaskNodeRepository jiraTaskNodeRepository,
            CommitNodeRepository commitNodeRepository, JpaTaskCommitLinkRepository taskCommitLinkRepository) {
        this.jiraTaskNodeRepository = jiraTaskNodeRepository;
        this.commitNodeRepository = commitNodeRepository;
        this.taskCommitLinkRepository = taskCommitLinkRepository;
        this.webClient = webClientBuilder.build();
        this.taskRepository = taskRepository;
        this.commitDataRepository = commitDataRepository;
        this.jiraBoardRepository = jiraBoardRepository;
        this.gitRepoRepository = gitRepoRepository;
    }

    @Async
    @Transactional
    public void syncJiraTasks(UUID teamId, String siteId, String projectKey) {
        log.info("Starting background sync for Jira project {} (Site: {}) for Team: {}", projectKey, siteId, teamId);

        Optional<JiraBoard> boardOpt = jiraBoardRepository.findByTeamId(teamId);
        if (boardOpt.isEmpty())
            return;
        JiraBoard board = boardOpt.get();
        board.setSyncStatus(SyncStatus.IN_PROGRESS);
        jiraBoardRepository.save(board);

        try {
            if (board.getAccessToken() != null) {
                String jql = "project=\"" + projectKey + "\"";
                String url = "https://api.atlassian.com/ex/jira/" + siteId + "/rest/api/3/search?jql=" + jql
                        + "&maxResults=100";

                JsonNode response = webClient.get()
                        .uri(url)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + board.getAccessToken())
                        .retrieve()
                        .bodyToMono(JsonNode.class)
                        .block();

                if (response != null && response.hasNonNull("issues")) {
                    JsonNode issues = response.get("issues");
                    for (JsonNode issue : issues) {
                        JsonNode fields = issue.get("fields");

                        String issueKey = issue.get("key").asText();
                        Task task = taskRepository.findByIssueKey(issueKey).orElseGet(Task::new);
                        task.setBoardId(board.getId());
                        task.setIssueKey(issueKey);
                        task.setSummary(fields != null && fields.hasNonNull("summary") ? fields.get("summary").asText()
                                : "No Summary");

                        if (fields != null && fields.hasNonNull("status")) {
                            JsonNode statusObj = fields.get("status");
                            task.setStatus(statusObj.get("name").asText());
                        }

                        taskRepository.save(task);

                        JiraTaskNode taskNode = jiraTaskNodeRepository.findByIssueKey(task.getIssueKey())
                                .orElse(new JiraTaskNode());
                        taskNode.setIssueKey(task.getIssueKey());
                        taskNode.setStoryPoint(task.getStoryPoint());
                        taskNode.setStatus(task.getStatus());
                        jiraTaskNodeRepository.save(taskNode);
                    }
                }
            }

            board.setSyncStatus(SyncStatus.SUCCESS);
            board.setLastSyncedAt(LocalDateTime.now());
            board.setLastSyncMessage(null);
            log.info("Finished background sync for Jira project {}", projectKey);
        } catch (Exception e) {
            log.error("Failed background sync for Jira project {}", projectKey, e);
            board.setSyncStatus(SyncStatus.FAILED);
            board.setLastSyncMessage(e.getMessage());
        } finally {
            jiraBoardRepository.save(board);
        }
    }

    @Async
    @Transactional
    public void syncGithubCommits(UUID teamId, List<String> repoUrls) {
        log.info("Starting background sync for GitHub repos {} for Team: {}", repoUrls, teamId);

        List<GitRepo> repos = gitRepoRepository.findAllByTeamId(teamId);
        for (GitRepo repo : repos) {
            repo.setSyncStatus(SyncStatus.IN_PROGRESS);
        }
        gitRepoRepository.saveAll(repos);

        try {
            for (GitRepo repo : repos) {
                String repoUrl = repo.getRepoUrl();
                String path = repoUrl.replace("https://github.com/", "");
                String apiUrl = "https://api.github.com/repos/" + path + "/commits?per_page=100";

                WebClient.RequestHeadersSpec<?> requestSpec = webClient.get().uri(apiUrl);

                if (repo.getAccessToken() != null && !repo.getAccessToken().startsWith("GH-INST")) {
                    requestSpec.header(HttpHeaders.AUTHORIZATION, "Bearer " + repo.getAccessToken());
                }

                try {
                    JsonNode commits = requestSpec
                            .retrieve()
                            .bodyToMono(JsonNode.class)
                            .block();

                    if (commits != null && commits.isArray()) {
                        for (JsonNode commitObj : commits) {
                            String hash = commitObj.get("sha").asText();
                            if (commitDataRepository.findByHash(hash).isPresent()) {
                                continue;
                            }
                            JsonNode commitData = commitObj.get("commit");
                            String message = commitData.hasNonNull("message") ? commitData.get("message").asText()
                                    : null;
                            JsonNode author = commitData.get("author");
                            String authorEmail = author.hasNonNull("email") ? author.get("email").asText() : null;

                            CommitData entity = new CommitData();
                            entity.setRepoId(repo.getId());
                            entity.setHash(hash);
                            entity.setMessage(message);
                            entity.setAuthorEmail(authorEmail);
                            entity.setBranchName("main");

                            commitDataRepository.save(entity);

                            CommitNode commitNode = commitNodeRepository.findByHash(hash).orElse(new CommitNode());
                            commitNode.setHash(hash);
                            commitNode.setMessage(message);
                            commitNode.setTimestamp(java.time.LocalDateTime.now().toString());
                            commitNodeRepository.save(commitNode);

                            if (message != null) {
                                Matcher matcher = JIRA_KEY_PATTERN.matcher(message);
                                while (matcher.find()) {
                                    String issueKey = matcher.group(1);
                                    java.util.Optional<Task> taskOpt = taskRepository.findByIssueKey(issueKey);

                                    if (taskOpt.isPresent()) {
                                        Task task = taskOpt.get();
                                        TaskCommitLink link = new TaskCommitLink();
                                        link.setTaskId(task.getId());
                                        link.setCommitId(entity.getId());
                                        taskCommitLinkRepository.save(link);

                                        JiraTaskNode taskNode = jiraTaskNodeRepository.findByIssueKey(issueKey)
                                                .orElse(null);
                                        if (taskNode != null) {
                                            commitNode.getImplementsTasks().add(taskNode);
                                            commitNodeRepository.save(commitNode);
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error("Failed to fetch commits for repo {}", repoUrl, e);
                }
            }

            for (GitRepo repo : repos) {
                repo.setSyncStatus(SyncStatus.SUCCESS);
                repo.setLastSyncedAt(LocalDateTime.now());
                repo.setLastSyncMessage(null);
            }
            log.info("Finished background sync for GitHub repos");
        } catch (Exception e) {
            log.error("Failed background sync for GitHub repos", e);
            for (GitRepo repo : repos) {
                repo.setSyncStatus(SyncStatus.FAILED);
                repo.setLastSyncMessage(e.getMessage());
            }
        } finally {
            gitRepoRepository.saveAll(repos);
        }
    }
}
