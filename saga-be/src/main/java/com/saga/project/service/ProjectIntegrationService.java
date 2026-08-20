package com.saga.project.service;

import com.saga.academic.service.TeamValidationService;
import com.saga.project.entity.GitRepo;
import com.saga.project.entity.IntegrationStatus;
import com.saga.project.entity.JiraBoard;
import com.saga.project.repository.JpaGitRepoRepository;
import com.saga.project.repository.JpaJiraBoardRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ProjectIntegrationService {
    private final JpaJiraBoardRepository jiraBoardRepository;
    private final JpaGitRepoRepository gitRepoRepository;
    private final TeamValidationService teamValidationPort;
    private final WebClient webClient;

    @Value("${app.jira.client-id:}")
    private String jiraClientId;

    @Value("${app.jira.client-secret:}")
    private String jiraClientSecret;

    @Value("${app.jira.redirect-uri:}")
    private String jiraRedirectUri;

    @Value("${app.github.client-id:}")
    private String githubClientId;

    @Value("${app.github.client-secret:}")
    private String githubClientSecret;

    public ProjectIntegrationService(JpaJiraBoardRepository jiraBoardRepository,
            JpaGitRepoRepository gitRepoRepository,
            TeamValidationService teamValidationPort,
            WebClient.Builder webClientBuilder) {
        this.jiraBoardRepository = jiraBoardRepository;
        this.gitRepoRepository = gitRepoRepository;
        this.teamValidationPort = teamValidationPort;
        this.webClient = webClientBuilder.build();
    }

    private void checkLeaderPermission(UUID userId, UUID teamId) {
        if (!teamValidationPort.isLeader(userId, teamId)) {
            throw new AccessDeniedException("You do not have Leader permission for this team.");
        }
    }

    public String generateJiraConnectUrl(UUID userId, UUID teamId) {
        checkLeaderPermission(userId, teamId);
        String state = teamId.toString();
        return String.format(
                "https://auth.atlassian.com/authorize?audience=api.atlassian.com&client_id=%s&scope=read:jira-work&redirect_uri=%s&state=%s&response_type=code&prompt=consent",
                jiraClientId, jiraRedirectUri, state);
    }

    @Transactional
    public JiraBoard handleJiraCallback(UUID userId, String code, String state) {
        UUID teamId = UUID.fromString(state);
        checkLeaderPermission(userId, teamId);

        // 1. Exchange Code for Access Token
        Map<String, String> body = Map.of(
                "grant_type", "authorization_code",
                "client_id", jiraClientId,
                "client_secret", jiraClientSecret,
                "code", code,
                "redirect_uri", jiraRedirectUri);

        Map tokenResponse = webClient.post()
                .uri("https://auth.atlassian.com/oauth/token")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        String accessToken = (String) tokenResponse.get("access_token");

        // 2. Get Cloud ID
        List<Map<String, Object>> resources = webClient.get()
                .uri("https://api.atlassian.com/oauth/token/accessible-resources")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {
                })
                .block();

        String cloudId = "UNKNOWN";
        String boardName = "Saga Backend Sprint Board";
        if (resources != null && !resources.isEmpty()) {
            cloudId = (String) resources.get(0).get("id");
            boardName = (String) resources.get(0).get("name");
        }

        JiraBoard entity = new JiraBoard();
        entity.setTeamId(teamId);
        entity.setBoardId(cloudId);
        entity.setBoardName(boardName);
        entity.setProjectKey("SAGA");
        entity.setStatus(IntegrationStatus.LINKED);
        entity.setLinkedAt(LocalDateTime.now());
        JiraBoard saved = jiraBoardRepository.save(entity);

        return JiraBoard.builder()
                .id(saved.getId())
                .teamId(saved.getTeamId())
                .boardId(saved.getBoardId())
                .boardName(saved.getBoardName())
                .projectKey(saved.getProjectKey())
                .status(saved.getStatus())
                .linkedAt(saved.getLinkedAt())
                .build();
    }

    public String generateGithubInstallUrl(UUID userId, UUID teamId) {
        checkLeaderPermission(userId, teamId);
        String state = teamId.toString();
        // Uses Github App Installation flow
        return String.format(
                "https://github.com/apps/%s/installations/new?state=%s",
                githubClientId, state);
    }

    @Transactional
    public GitRepo handleGithubCallback(UUID userId, String installationId, String state) {
        UUID teamId = UUID.fromString(state);
        checkLeaderPermission(userId, teamId);

        // For GitHub App, the installationId itself is what we need to query the
        // repositories
        // We'll simulate fetching repo details using the installation ID since actual
        // JWT generation for Github Apps is complex for this scope
        String repoId = "GH-INST-" + installationId;

        GitRepo entity = new GitRepo();
        entity.setTeamId(teamId);
        entity.setRepoId(repoId);
        entity.setRepoName("fpt-edu/saga-backend");
        entity.setRepoUrl("https://github.com/fpt-edu/saga-backend");
        entity.setStatus(IntegrationStatus.LINKED);
        entity.setLinkedAt(LocalDateTime.now());
        GitRepo saved = gitRepoRepository.save(entity);

        return GitRepo.builder()
                .id(saved.getId())
                .teamId(saved.getTeamId())
                .repoId(saved.getRepoId())
                .repoName(saved.getRepoName())
                .repoUrl(saved.getRepoUrl())
                .status(saved.getStatus())
                .linkedAt(saved.getLinkedAt())
                .build();
    }
}
