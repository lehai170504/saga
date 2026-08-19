package com.saga.project.application.service;
import com.saga.project.domain.GitRepo;
import com.saga.project.domain.IntegrationStatus;
import com.saga.project.domain.JiraBoard;
import com.saga.project.infrastructure.persistence.entity.GitRepoEntity;
import com.saga.project.infrastructure.persistence.entity.JiraBoardEntity;
import com.saga.project.infrastructure.persistence.repository.JpaGitRepoRepository;
import com.saga.project.infrastructure.persistence.repository.JpaJiraBoardRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.UUID;
@Service
public class ProjectIntegrationService {
    private final JpaJiraBoardRepository jiraBoardRepository;
    private final JpaGitRepoRepository gitRepoRepository;
    @Value("${app.jira.client-id:}")
    private String jiraClientId;
    @Value("${app.github.app-name:}")
    private String githubAppName;
    public ProjectIntegrationService(JpaJiraBoardRepository jiraBoardRepository, JpaGitRepoRepository gitRepoRepository) {
        this.jiraBoardRepository = jiraBoardRepository;
        this.gitRepoRepository = gitRepoRepository;
    }
    private void checkLeaderPermission(UUID projectId) {
        boolean isLeader = true; 
        if (!isLeader) {
            throw new AccessDeniedException("You do not have Leader permission to perform this project integration action.");
        }
    }
    public String generateJiraConnectUrl(UUID projectId) {
        checkLeaderPermission(projectId);
        String state = projectId.toString(); 
        return String.format(
            "https://auth.atlassian.com/authorize?audience=api.atlassian.com&client_id=%s&scope=read:jira-work&redirect_uri=http://localhost:8080/api/v1/integrations/jira/callback&state=%s&response_type=code&prompt=consent",
            jiraClientId, state
        );
    }
    @Transactional
    public JiraBoard handleJiraCallback(String code, String state) {
        UUID projectId = UUID.fromString(state);
        checkLeaderPermission(projectId);
        JiraBoardEntity entity = new JiraBoardEntity();
        entity.setProjectId(projectId);
        entity.setBoardId("JIRA-BOARD-999");
        entity.setBoardName("Saga Backend Sprint Board");
        entity.setProjectKey("SAGA");
        entity.setStatus(IntegrationStatus.LINKED);
        entity.setLinkedAt(LocalDateTime.now());
        JiraBoardEntity saved = jiraBoardRepository.save(entity);
        return JiraBoard.builder()
                .id(saved.getId())
                .projectId(saved.getProjectId())
                .boardId(saved.getBoardId())
                .boardName(saved.getBoardName())
                .projectKey(saved.getProjectKey())
                .status(saved.getStatus())
                .linkedAt(saved.getLinkedAt())
                .build();
    }
    public String generateGithubInstallUrl(UUID projectId) {
        checkLeaderPermission(projectId);
        String state = projectId.toString();
        return String.format(
            "https://github.com/apps/%s/installations/new?state=%s",
            githubAppName, state
        );
    }
    @Transactional
    public GitRepo handleGithubCallback(String installationId, String state) {
        UUID projectId = UUID.fromString(state);
        checkLeaderPermission(projectId);
        GitRepoEntity entity = new GitRepoEntity();
        entity.setProjectId(projectId);
        entity.setRepoId("REPO-777");
        entity.setRepoName("fpt-edu/saga-backend");
        entity.setRepoUrl("https://github.com/fpt-edu/saga-backend");
        entity.setStatus(IntegrationStatus.LINKED);
        entity.setLinkedAt(LocalDateTime.now());
        GitRepoEntity saved = gitRepoRepository.save(entity);
        return GitRepo.builder()
                .id(saved.getId())
                .projectId(saved.getProjectId())
                .repoId(saved.getRepoId())
                .repoName(saved.getRepoName())
                .repoUrl(saved.getRepoUrl())
                .status(saved.getStatus())
                .linkedAt(saved.getLinkedAt())
                .build();
    }
}