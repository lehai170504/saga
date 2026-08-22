package com.saga.project.service;

import com.saga.project.entity.GitRepo;
import com.saga.project.entity.Task;
import com.saga.project.repository.JpaGitRepoRepository;
import com.saga.project.repository.JpaTaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiCommitAnalyzerService {

    private final JpaGitRepoRepository gitRepoRepository;
    private final JpaTaskRepository taskRepository;
    private final AiReviewService aiReviewService;
    private final NotificationService notificationService;
    private final WebClient webClient;

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

        // Call Grok AI
        AiReviewService.AiReviewResult result = aiReviewService.analyzeCommit(taskDescription, gitDiff);

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

            // For Github Apps, the access token is often the installation token
            if (repo.getAccessToken() != null && repo.getAccessToken().startsWith("GH-INST")) {
                // Not ideal, we should re-fetch token, but for now MVP logic assumes token or
                // public repo
                // If it's a public repo, diff can be fetched without auth
                // To fetch auth, we'd use
                // GithubAppAuthService.getInstallationAccessToken(repo.getAccessToken())
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
