package com.saga.project.service;

import com.saga.academic.service.TeamValidationService;
import com.saga.project.dto.*;
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
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.data.redis.core.RedisTemplate;
import java.util.concurrent.TimeUnit;

@Service
public class ProjectIntegrationService {
    private final JpaJiraBoardRepository jiraBoardRepository;
    private final JpaGitRepoRepository gitRepoRepository;
    private final TeamValidationService teamValidationPort;
    private final WebClient webClient;
    private final InitialSyncService initialSyncService;
    private final GithubAppAuthService githubAppAuthService;

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

    // Redis keys
    private static final String JIRA_TOKEN_PREFIX = "jira_token:";
    private static final String GITHUB_INSTALL_PREFIX = "github_installation:";
    private final RedisTemplate<String, Object> redisTemplate;

    public ProjectIntegrationService(JpaJiraBoardRepository jiraBoardRepository,
            JpaGitRepoRepository gitRepoRepository,
            TeamValidationService teamValidationPort,
            InitialSyncService initialSyncService,
            WebClient.Builder webClientBuilder,
            RedisTemplate<String, Object> redisTemplate,
            GithubAppAuthService githubAppAuthService) {
        this.redisTemplate = redisTemplate;
        this.jiraBoardRepository = jiraBoardRepository;
        this.githubAppAuthService = githubAppAuthService;
        this.gitRepoRepository = gitRepoRepository;
        this.teamValidationPort = teamValidationPort;
        this.initialSyncService = initialSyncService;
        this.webClient = webClientBuilder.build();
        
    }

    private void checkLeaderPermission(UUID userId, UUID teamId) {
        if (!teamValidationPort.isLeader(userId, teamId)) {
            throw new AccessDeniedException("You do not have Leader permission for this team.");
        }
    }

    // ==========================================
    // JIRA INTEGRATION FLOW
    // ==========================================

    public String generateJiraConnectUrl(UUID userId, UUID teamId) {
        checkLeaderPermission(userId, teamId);
        String state = teamId.toString();
        return String.format(
                "https://auth.atlassian.com/authorize?audience=api.atlassian.com&client_id=%s&scope=read:jira-work read:jira-user&redirect_uri=%s&state=%s&response_type=code&prompt=consent",
                jiraClientId, jiraRedirectUri, state);
    }

    public List<AvailableJiraSiteDTO> handleJiraCallback(UUID userId, String code, String state) {
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
        String refreshToken = (String) tokenResponse.get("refresh_token");
        Map<String, String> tokens = new HashMap<>();
        tokens.put("access_token", accessToken);
        if (refreshToken != null)
            tokens.put("refresh_token", refreshToken);
        redisTemplate.opsForValue().set(JIRA_TOKEN_PREFIX + teamId, tokens, 15, TimeUnit.MINUTES);

        // 2. Get Accessible Resources (Sites)
        List<Map<String, Object>> resources = webClient.get()
                .uri("https://api.atlassian.com/oauth/token/accessible-resources")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {
                })
                .block();

        if (resources == null)
            return Collections.emptyList();

        return resources.stream().map(res -> AvailableJiraSiteDTO.builder()
                .id((String) res.get("id"))
                .name((String) res.get("name"))
                .url((String) res.get("url"))
                .build()).collect(Collectors.toList());
    }

    public List<AvailableJiraProjectDTO> getAvailableJiraProjects(UUID userId, UUID teamId, String siteId) {
        checkLeaderPermission(userId, teamId);
        Map<String, String> tokens = (Map<String, String>) redisTemplate.opsForValue().get(JIRA_TOKEN_PREFIX + teamId);
        String accessToken = tokens != null ? tokens.get("access_token") : null;
        if (accessToken == null) {
            throw new IllegalStateException("No active Jira connection process found. Please reconnect.");
        }

        // Fetch projects from Jira REST API for this specific site
        try {
            List<Map<String, Object>> projects = webClient.get()
                    .uri("https://api.atlassian.com/ex/jira/" + siteId + "/rest/api/3/project")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {
                    })
                    .block();

            if (projects == null)
                return Collections.emptyList();

            return projects.stream().map(p -> AvailableJiraProjectDTO.builder()
                    .id((String) p.get("id"))
                    .key((String) p.get("key"))
                    .name((String) p.get("name"))
                    .style((String) p.get("style"))
                    .build()).collect(Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList(); // Return empty if error (e.g. no permission)
        }
    }

    @Transactional
    public JiraBoard confirmJiraProject(UUID userId, UUID teamId, JiraConfirmRequest request) {
        checkLeaderPermission(userId, teamId);

        Optional<JiraBoard> existing = jiraBoardRepository.findByTeamId(teamId);
        if (existing.isPresent()) {
            throw new IllegalStateException("Team is already linked to a Jira Board.");
        }

        JiraBoard entity = new JiraBoard();
        entity.setTeamId(teamId);
        entity.setBoardId(request.getSiteId()); // using boardId for cloudId historically, or use siteId
        entity.setSiteId(request.getSiteId());
        entity.setProjectKey(request.getProjectKey());
        entity.setBoardName(
                request.getBoardName() != null ? request.getBoardName() : request.getProjectKey() + " Board");
        entity.setStatus(IntegrationStatus.LINKED);
        entity.setLinkedAt(LocalDateTime.now());

        Map<String, String> tokens = (Map<String, String>) redisTemplate.opsForValue().get(JIRA_TOKEN_PREFIX + teamId);
        if (tokens != null) {
            entity.setAccessToken(tokens.get("access_token"));
            entity.setRefreshToken(tokens.get("refresh_token"));
        }

        // Remove token from temp storage to free memory
        redisTemplate.delete(JIRA_TOKEN_PREFIX + teamId);

        initialSyncService.syncJiraTasks(teamId, entity.getSiteId(), entity.getProjectKey());

        return jiraBoardRepository.save(entity);
    }

    @Transactional
    public void triggerManualJiraSync(UUID userId, UUID teamId) {
        checkLeaderPermission(userId, teamId);
        JiraBoard board = jiraBoardRepository.findByTeamId(teamId)
                .orElseThrow(() -> new IllegalStateException("Team is not linked to Jira."));
        initialSyncService.syncJiraTasks(teamId, board.getSiteId(), board.getProjectKey());
    }

    @Transactional
    public void unlinkJira(UUID userId, UUID teamId) {
        checkLeaderPermission(userId, teamId);
        jiraBoardRepository.findByTeamId(teamId).ifPresent(board -> {
            jiraBoardRepository.delete(board);
        });
    }

    // ==========================================
    // GITHUB INTEGRATION FLOW
    // ==========================================

    public String generateGithubInstallUrl(UUID userId, UUID teamId) {
        checkLeaderPermission(userId, teamId);
        String state = teamId.toString();
        return String.format(
                "https://github.com/apps/%s/installations/new?state=%s",
                githubClientId, state);
    }

    public List<AvailableGithubRepoDTO> handleGithubCallback(UUID userId, String installationId, String state) {
        UUID teamId = UUID.fromString(state);
        checkLeaderPermission(userId, teamId);
        redisTemplate.opsForValue().set(GITHUB_INSTALL_PREFIX + teamId, installationId, 15, TimeUnit.MINUTES);

        try {
            // Generate Installation Access Token using the private key and app ID
            String installationToken = githubAppAuthService.getInstallationAccessToken(installationId);

            // Fetch repositories using WebClient
            Map<String, Object> response = webClient.get()
                    .uri("https://api.github.com/installation/repositories")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + installationToken)
                    .header(HttpHeaders.ACCEPT, "application/vnd.github.v3+json")
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();

            if (response != null && response.containsKey("repositories")) {
                List<Map<String, Object>> reposNode = (List<Map<String, Object>>) response.get("repositories");
                return reposNode.stream().map(node -> AvailableGithubRepoDTO.builder()
                        .id(String.valueOf(node.get("id")))
                        .fullName((String) node.get("full_name"))
                        .url((String) node.get("html_url"))
                        .isPrivate((Boolean) node.get("private"))
                        .build()).collect(Collectors.toList());
            }
            return Collections.emptyList();
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch Github repositories: " + e.getMessage(), e);
        }
    }

    @Transactional
    public List<GitRepo> confirmGithubRepos(UUID userId, UUID teamId, GithubConfirmRequest request) {
        checkLeaderPermission(userId, teamId);

        String installationId = (String) redisTemplate.opsForValue().get(GITHUB_INSTALL_PREFIX + teamId);
        if (installationId == null) {
            throw new IllegalStateException("No active Github connection process found.");
        }

        List<GitRepo> savedRepos = new ArrayList<>();

        for (String url : request.getRepoUrls()) {
            // Check if already linked
            Optional<GitRepo> existing = gitRepoRepository.findByRepoUrl(url);
            if (existing.isEmpty()) {
                String repoName = url.substring(url.lastIndexOf("/") + 1);
                GitRepo entity = new GitRepo();
                entity.setTeamId(teamId);
                entity.setRepoId("GH-INST-" + installationId + "-" + repoName);
                entity.setRepoName(repoName);
                entity.setRepoUrl(url);
                entity.setAccessToken(installationId); // For now, store installation ID as token to be used to fetch
                                                       // commits
                entity.setStatus(IntegrationStatus.LINKED);
                entity.setLinkedAt(LocalDateTime.now());

                Map<String, String> tokens = (Map<String, String>) redisTemplate.opsForValue()
                        .get(JIRA_TOKEN_PREFIX + teamId);
                if (tokens != null) {
                    entity.setAccessToken(tokens.get("access_token"));
                    entity.setRefreshToken(tokens.get("refresh_token"));
                }
                savedRepos.add(gitRepoRepository.save(entity));
            }
        }

        redisTemplate.delete(GITHUB_INSTALL_PREFIX + teamId);

        initialSyncService.syncGithubCommits(teamId, request.getRepoUrls());

        return savedRepos;
    }

    @Transactional
    public void triggerManualGithubSync(UUID userId, UUID teamId) {
        checkLeaderPermission(userId, teamId);
        List<GitRepo> repos = gitRepoRepository.findAllByTeamId(teamId);
        if (repos.isEmpty()) {
            throw new IllegalStateException("Team is not linked to Github.");
        }
        initialSyncService.syncGithubCommits(teamId,
                repos.stream().map(GitRepo::getRepoUrl).collect(Collectors.toList()));
    }

    @Transactional
    public void unlinkGithub(UUID userId, UUID teamId) {
        checkLeaderPermission(userId, teamId);
        List<GitRepo> repos = gitRepoRepository.findAllByTeamId(teamId);
        if (!repos.isEmpty()) {
            gitRepoRepository.deleteAll(repos);
        }
    }
}


