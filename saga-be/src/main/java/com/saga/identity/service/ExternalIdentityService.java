package com.saga.identity.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Map;
import com.saga.identity.service.ExternalUserProfile;

@Component
public class ExternalIdentityService {
        private final RestTemplate restTemplate = new RestTemplate();
        @Value("${app.github.client-id:}")
        private String githubClientId;
        @Value("${app.github.client-secret:}")
        private String githubClientSecret;
        @Value("${app.jira.client-id:}")
        private String jiraClientId;
        @Value("${app.jira.client-secret:}")
        private String jiraClientSecret;
        @Value("${app.jira.redirect-uri:}")
        private String jiraRedirectUri;

        public ExternalUserProfile getGithubProfile(String code) {
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(List.of(MediaType.APPLICATION_JSON));
                Map<String, String> request = Map.of("client_id", githubClientId, "client_secret", githubClientSecret,
                                "code",
                                code);
                HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);
                ResponseEntity<Map> tokenResponse = restTemplate.postForEntity(
                                "https://github.com/login/oauth/access_token",
                                entity, Map.class);
                String accessToken = (String) tokenResponse.getBody().get("access_token");
                HttpHeaders emailHeaders = new HttpHeaders();
                emailHeaders.setBearerAuth(accessToken);
                ResponseEntity<List<Map<String, Object>>> emailsResponse = restTemplate.exchange(
                                "https://api.github.com/user/emails", HttpMethod.GET, new HttpEntity<>(emailHeaders),
                                new ParameterizedTypeReference<List<Map<String, Object>>>() {
                                });
                String email = emailsResponse.getBody().stream().map(m -> (String) m.get("email")).findFirst()
                                .orElse("");
                ResponseEntity<Map> userResponse = restTemplate.exchange("https://api.github.com/user", HttpMethod.GET,
                                new HttpEntity<>(emailHeaders), Map.class);
                String name = (String) userResponse.getBody().get("name");
                if (name == null || name.isEmpty())
                        name = (String) userResponse.getBody().get("login");
                String id = String.valueOf(userResponse.getBody().get("id"));
                return ExternalUserProfile.builder().id(id).name(name).email(email).build();
        }

        public ExternalUserProfile getJiraProfile(String code) {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                Map<String, String> request = Map.of("grant_type", "authorization_code", "client_id", jiraClientId,
                                "client_secret", jiraClientSecret, "code", code, "redirect_uri", jiraRedirectUri);
                ResponseEntity<Map> tokenResponse = restTemplate.postForEntity("https://auth.atlassian.com/oauth/token",
                                new HttpEntity<>(request, headers), Map.class);
                String accessToken = (String) tokenResponse.getBody().get("access_token");
                HttpHeaders meHeaders = new HttpHeaders();
                meHeaders.setBearerAuth(accessToken);
                ResponseEntity<Map> meResponse = restTemplate.exchange("https://api.atlassian.com/me", HttpMethod.GET,
                                new HttpEntity<>(meHeaders), Map.class);
                String email = (String) meResponse.getBody().get("email");
                String name = (String) meResponse.getBody().get("name");
                String id = (String) meResponse.getBody().get("account_id");
                return ExternalUserProfile.builder().id(id).name(name).email(email).build();
        }
}