package com.saga.auth.service;

import com.saga.auth.dto.UserProfileDTO;
import com.saga.auth.service.GoogleAuthService;
import com.saga.shared.exception.UnauthorizedException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import java.util.Map;

@Component
public class GoogleAuthService {

    private final RestTemplate restTemplate = new RestTemplate();

    public UserProfileDTO verifyToken(String token) {
        try {
            String userInfoUrl = "https://www.googleapis.com/oauth2/v3/userinfo";
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            HttpEntity<String> entity = new HttpEntity<>("", headers);

            ResponseEntity<Map> response = restTemplate.exchange(userInfoUrl, HttpMethod.GET, entity, Map.class);
            Map<String, Object> payload = response.getBody();

            if (payload != null && payload.containsKey("email")) {
                return UserProfileDTO.builder()
                        .email((String) payload.get("email"))
                        .name((String) payload.get("name"))
                        .picture((String) payload.get("picture"))
                        .build();
            } else {
                throw new UnauthorizedException("Invalid Google Token.");
            }
        } catch (Exception e) {
            throw new UnauthorizedException("Failed to verify Google Token: " + e.getMessage());
        }
    }
}
