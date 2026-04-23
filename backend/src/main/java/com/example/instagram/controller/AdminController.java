package com.example.instagram.controller;

import com.example.instagram.model.MediaItem;
import com.example.instagram.model.ReelConfig;
import com.example.instagram.model.ReelsData;
import com.example.instagram.service.ConfigManager;
import com.example.instagram.service.InstagramService;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminController {

    private final InstagramService instagramService;
    private final ConfigManager configManager;

    @Data
    @Builder
    public static class ReelResponse {
        private String id;
        @JsonProperty("thumbnail_url")
        private String thumbnailUrl;
        private String permalink;
        private String caption;
        private ReelConfig config;
    }

    @GetMapping("/reels")
    public Map<String, Object> fetchReels() {
        log.debug("Fetching reels for administration dashboard");
        try {
            List<MediaItem> mediaItems = instagramService.getAccountMedia();
            ReelsData configs = configManager.getAllConfigs();

            List<ReelResponse> reels = mediaItems.stream().map(item -> {
                String mediaId = item.getId();
                ReelConfig config = configs.getReels().getOrDefault(mediaId, configs.getDefaultConfig());

                return ReelResponse.builder()
                        .id(mediaId)
                        .thumbnailUrl(item.getThumbnailUrl() != null ? item.getThumbnailUrl() : item.getMediaUrl())
                        .permalink(item.getPermalink())
                        .caption(item.getCaption() != null ? 
                                item.getCaption().substring(0, Math.min(item.getCaption().length(), 100)) : "")
                        .config(config)
                        .build();
            }).collect(Collectors.toList());

            return Map.of("reels", reels, "total", reels.size());
        } catch (Exception e) {
            log.error("Failed to fetch reels for dashboard: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to fetch reels", e);
        }
    }

    @GetMapping("/reels/{mediaId}")
    public Map<String, Object> getReel(@PathVariable String mediaId) {
        log.debug("Fetching configuration for media ID: {}", mediaId);
        return Map.of("media_id", mediaId, "config", configManager.getReelConfig(mediaId));
    }

    @PutMapping("/reels/{mediaId}")
    public Map<String, Object> updateReel(@PathVariable String mediaId, @RequestBody ReelConfig config) {
        log.info("Updating automation configuration for media ID: {}", mediaId);
        try {
            configManager.updateReelConfig(mediaId, config);
            return Map.of("status", "updated", "media_id", mediaId);
        } catch (Exception e) {
            log.error("Failed to update config for media {}: {}", mediaId, e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update configuration", e);
        }
    }

    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        log.debug("Calculating automation statistics");
        try {
            List<MediaItem> mediaItems = instagramService.getAccountMedia();
            ReelsData configs = configManager.getAllConfigs();

            long configuredCount = mediaItems.stream()
                    .filter(item -> configs.getReels().containsKey(item.getId()))
                    .count();

            return Map.of(
                "total_reels", mediaItems.size(),
                "configured", (int) configuredCount,
                "using_default", (int) (mediaItems.size() - configuredCount)
            );
        } catch (Exception e) {
            log.error("Failed to calculate stats: {}", e.getMessage());
            return Map.of("error", "Failed to load statistics");
        }
    }

    @PostMapping("/test/send-dm")
    public Map<String, Object> testSendDm(@RequestBody Map<String, String> request) {
        log.info("Manual DM test triggered for comment ID: {}", request.get("comment_id"));
        try {
            return Map.of("status", "success", "result", 
                instagramService.sendDM(request.get("comment_id"), request.get("message")));
        } catch (Exception e) {
            log.error("Manual DM test failed: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Test DM failed", e);
        }
    }

    @PostMapping("/test/reply-comment")
    public Map<String, Object> testReplyComment(@RequestBody Map<String, String> request) {
        log.info("Manual comment reply test triggered for comment ID: {}", request.get("comment_id"));
        try {
            return Map.of("status", "success", "result", 
                instagramService.replyToComment(request.get("comment_id"), request.get("message")));
        } catch (Exception e) {
            log.error("Manual comment reply test failed: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Test reply failed", e);
        }
    }
}
