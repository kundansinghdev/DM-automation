package com.example.instagram.service;

import com.example.instagram.model.MediaItem;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class InstagramService {
    private static final String GRAPH_API_URL = "https://graph.facebook.com/v19.0";
    
    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    
    @Value("${instagram.access.token}")
    private String accessToken;

    @Value("${instagram.business.account.id}")
    private String businessAccountId;

    @Value("${instagram.page.id}")
    private String pageId;

    public InstagramService(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.baseUrl(GRAPH_API_URL).build();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Sends a Direct Message (DM) as a private reply to an Instagram comment.
     * 
     * @param commentId The ID of the comment being replied to.
     * @param message The message text to send.
     * @return API response as a Map.
     */
    public Map<String, Object> sendDM(String commentId, String message) {
        log.debug("Sending DM for comment {}: {}", commentId, message);
        
        Map<String, Object> payload = Map.of(
            "recipient", Map.of("comment_id", commentId),
            "message", Map.of("text", message)
        );
        
        try {
            return restClient.post()
                    .uri(uriBuilder -> uriBuilder.path("/{pageId}/messages")
                            .queryParam("access_token", accessToken)
                            .build(pageId))
                    .body(payload)
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            log.error("DM API Error for comment {}: {}", commentId, e.getMessage());
            throw e;
        }
    }

    /**
     * Posts a public reply to an Instagram comment.
     * 
     * @param commentId The ID of the comment being replied to.
     * @param message The message text to post.
     * @return API response as a Map.
     */
    public Map<String, Object> replyToComment(String commentId, String message) {
        log.debug("Posting public reply for comment {}: {}", commentId, message);
        Map<String, String> payload = Map.of("message", message);
        
        try {
            return restClient.post()
                    .uri(uriBuilder -> uriBuilder.path("/{commentId}/replies")
                            .queryParam("access_token", accessToken)
                            .build(commentId))
                    .body(payload)
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            log.error("Reply API Error for comment {}: {}", commentId, e.getMessage());
            throw e;
        }
    }

    /**
     * Fetches media items for the configured Instagram Business Account.
     * 
     * @return List of MediaItem objects.
     */
    public List<MediaItem> getAccountMedia() {
        if (businessAccountId == null || businessAccountId.isEmpty() || businessAccountId.contains("${")) {
            log.warn("Business Account ID not configured, skipping media fetch.");
            return Collections.emptyList();
        }

        log.debug("Fetching media for account: {}", businessAccountId);
        try {
            Map<String, Object> response = restClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/{businessAccountId}/media")
                            .queryParam("access_token", accessToken)
                            .queryParam("fields", "id,media_type,media_url,thumbnail_url,permalink,caption")
                            .queryParam("limit", 100)
                            .build(businessAccountId))
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<String, Object>>() {});
            
            if (response != null && response.containsKey("data")) {
                return objectMapper.convertValue(response.get("data"), new TypeReference<List<MediaItem>>() {});
            }
        } catch (Exception e) {
            log.error("Media Fetch API Error: {}", e.getMessage());
        }
        return Collections.emptyList();
    }
}
