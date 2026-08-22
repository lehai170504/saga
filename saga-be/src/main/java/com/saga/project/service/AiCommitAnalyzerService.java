package com.saga.project.service;

import com.saga.project.entity.GitRepo;
import com.saga.project.entity.Task;
import com.saga.project.repository.JpaGitRepoRepository;
import com.saga.project.repository.JpaTaskRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Map;

@Service
@Slf4j
public class AiCommitAnalyzerService {

    private final JpaGitRepoRepository gitRepoRepository;
    private final JpaTaskRepository taskRepository;
    private final AiReviewProvider aiReviewProvider;
    private final NotificationService notificationService;
    private final WebClient webClient;
    private final GithubAppAuthService githubAppAuthService;

    public AiCommitAnalyzerService(JpaGitRepoRepository gitRepoRepository,
            JpaTaskRepository taskRepository,
            Map<String, AiReviewProvider> reviewProviders,
            @org.springframework.beans.factory.annotation.Value("${app.ai.provider:grok}") String providerName,
            NotificationService notificationService,
            WebClient.Builder webClientBuilder,
            GithubAppAuthService githubAppAuthService) {
        this.gitRepoRepository = gitRepoRepository;
        this.taskRepository = taskRepository;
        this.aiReviewProvider = reviewProviders.getOrDefault(providerName, reviewProviders.get("grok"));
        this.notificationService = notificationService;
        this.webClient = webClientBuilder.build();
        this.githubAppAuthService = githubAppAuthService;
    }

    private static final Pattern JIRA_KEY_PATTERN = Pattern.compile("([A-Z]+-[0-9]+)");

    @Async
    public void analyzeCommit(String externalRepoId, String commitHash, String commitMessage, String authorEmail) {
        log.info("Starting AI analysis for commit: {}", commitHash);

        Matcher matcher = JIRA_KEY_PATTERN.matcher(commitMessage);
        if (!matcher.find()) {
            log.info("No Jira key found in commit message. Skipping AI review.");
            return;
        }
        String issueKey = matcher.group(1);

        Optional<GitRepo> repoOpt = gitRepoRepository.findByRepoId(externalRepoId);
        if (repoOpt.isEmpty()) {
            log.warn("Repo not found for AI analysis: {}", externalRepoId);
            return;
        }
        GitRepo repo = repoOpt.get();
        UUID teamId = repo.getTeamId();

        Optional<Task> taskOpt = taskRepository.findByIssueKey(issueKey);
        String taskDescription = "Task " + issueKey + " without description.";
        if (taskOpt.isPresent()) {
            Task task = taskOpt.get();
            if (task.getSummary() != null) {
                taskDescription = task.getSummary(); // We use summary as description for MVP
            }
        }

        String gitDiff = fetchGitDiff(repo, commitHash);
        if (gitDiff == null || gitDiff.isBlank()) {
            log.warn("Could not fetch Git Diff for commit: {}", commitHash);
            return;
        }

        // Call AI
        com.saga.project.dto.AiReviewResult result = aiReviewProvider.analyzeCommit(taskDescription, gitDiff);

        if (!result.valid()) {
            log.warn("AI detected invalid commit {}: {}", commitHash, result.reason());

            // Send Notification
            // In a real system, we might query the user by email to get their UUID.
            // For now, broadcast to the team.
            String alertTitle = "Cảnh báo Code không hợp lệ từ AI (" + issueKey + ")";
            String alertMessage = "Commit " + commitHash.substring(0, 7) + " bị AI đánh cờ: " + result.reason();
            notificationService.sendTeamNotification(teamId, alertTitle, alertMessage, "ALERT");
        } else {
            log.info("AI approved commit {}", commitHash);
        }
    }

    private String fetchGitDiff(GitRepo repo, String commitHash) {
        try {
            String repoUrl = repo.getRepoUrl();
            String path = repoUrl.replace("https://github.com/", "");
            String apiUrl = "https://api.github.com/repos/" + path + "/commits/" + commitHash;

            WebClient.RequestHeadersSpec<?> requestSpec = webClient.get()
                    .uri(apiUrl)
                    .header(HttpHeaders.ACCEPT, "application/vnd.github.v3.diff");

            if (repo.getAccessToken() != null && repo.getAccessToken().startsWith("GH-INST-")) {
                String installationId = repo.getAccessToken().replace("GH-INST-", "");
                String installationToken = githubAppAuthService.getInstallationAccessToken(installationId);
                requestSpec.header(HttpHeaders.AUTHORIZATION, "Bearer " + installationToken);
            }

            return requestSpec.retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (Exception e) {
            log.error("Error fetching git diff for AI analysis", e);
            return null;
        }
    }
}
