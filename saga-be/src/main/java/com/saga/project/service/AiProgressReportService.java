package com.saga.project.service;

import com.saga.project.dto.CommitDTO;
import com.saga.project.dto.TaskDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class AiProgressReportService {

    private final ProjectDataQueryService queryService;
    private final AiReviewProvider aiReviewProvider;

    public AiProgressReportService(ProjectDataQueryService queryService,
            Map<String, AiReviewProvider> reviewProviders,
            @Value("${app.ai.provider:grok}") String providerName) {
        this.queryService = queryService;
        this.aiReviewProvider = reviewProviders.getOrDefault(providerName, reviewProviders.get("grok"));
    }

    public String generateReport(UUID teamId) {
        log.info("Generating AI progress report for team: {}", teamId);

        // Fetch latest 50 tasks
        List<TaskDTO> tasks = queryService.getTeamTasks(teamId, PageRequest.of(0, 50)).getContent();

        // Fetch latest 50 commits
        List<CommitDTO> commits = queryService.getTeamCommits(teamId, PageRequest.of(0, 50)).getContent();

        StringBuilder prompt = new StringBuilder();
        prompt.append("Here is the latest data for the project:\n\n");

        prompt.append("### Recent Jira Tasks:\n");
        if (tasks.isEmpty()) {
            prompt.append("No tasks found.\n");
        } else {
            for (TaskDTO task : tasks) {
                prompt.append("- [").append(task.getIssueKey()).append("] ");
                if (task.getLabels() != null && !task.getLabels().isEmpty()) {
                    prompt.append("Labels: ").append(task.getLabels()).append("\n");
                } else {
                    prompt.append("\n");
                }
            }
        }

        prompt.append("\n### Recent Git Commits:\n");
        if (commits.isEmpty()) {
            prompt.append("No commits found.\n");
        } else {
            for (CommitDTO commit : commits) {
                prompt.append("- [").append(commit.getHash().substring(0, Math.min(commit.getHash().length(), 7)))
                        .append("] ");
                prompt.append("Author: ").append(commit.getAuthorEmail()).append(" | ");
                prompt.append("Message: ").append(commit.getMessage().replace("\n", " ")).append("\n");
            }
        }

        return aiReviewProvider.generateProgressReport(prompt.toString());
    }
}
