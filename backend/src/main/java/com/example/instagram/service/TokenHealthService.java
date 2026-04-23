package com.example.instagram.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenHealthService {

    private final RestClient.Builder restClientBuilder;
    
    @Value("${instagram.access.token}")
    private String accessToken;

    private static final String DEBUG_TOKEN_URL = "https://graph.facebook.com/debug_token";

    /**
     * Checks the validity of the configured access token on startup.
     */
    @PostConstruct
    public void checkTokenHealth() {
        if (accessToken == null || accessToken.isBlank() || accessToken.startsWith("your_")) {
            log.error("CRITICAL: Instagram Access Token is not configured!");
            return;
        }

        log.info("Performing startup health check for Instagram Access Token...");
        
        try {
            // Note: debug_token usually requires an App Access Token or User Token with App Secret
            // For simplicity in this environment, we'll do a simple /me check
            RestClient client = restClientBuilder.baseUrl("https://graph.facebook.com/v19.0").build();
            Map<String, Object> response = client.get()
                    .uri(uriBuilder -> uriBuilder.path("/me")
                            .queryParam("access_token", accessToken)
                            .queryParam("fields", "id,name")
                            .build())
                    .retrieve()
                    .body(new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {});

            if (response != null && response.containsKey("id")) {
                log.info("Token health check PASSED. Identity: {} (ID: {})", 
                        response.get("name"), response.get("id"));
            } else {
                log.error("Token health check FAILED. Response: {}", response);
            }
        } catch (Exception e) {
            log.error("CRITICAL: Instagram Access Token is INVALID or EXPIRED: {}", e.getMessage());
            log.error("Please generate a new token and update application.properties.");
        }
    }
}
