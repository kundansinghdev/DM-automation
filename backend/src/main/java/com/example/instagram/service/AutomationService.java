package com.example.instagram.service;

import com.example.instagram.model.ReelConfig;
import com.example.instagram.model.WebhookPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class AutomationService {

    private final InstagramService instagramService;
    private final ConfigManager configManager;
    private final Random random = new Random();

    @org.springframework.beans.factory.annotation.Value("${instagram.page.id}")
    private String pageId;

    @org.springframework.beans.factory.annotation.Value("${instagram.business.account.id}")
    private String businessAccountId;

    /**
     * Processes an incoming comment from the Instagram Webhook.
     * 
     * @param value The comment value payload from the webhook.
     */
    public void processComment(WebhookPayload.Value value) {
        if (value == null || value.getId() == null || value.getMedia() == null) {
            log.warn("Received malformed comment payload: {}", value);
            return;
        }

        String commenterId = Optional.ofNullable(value.getFrom())
                .map(WebhookPayload.From::getId)
                .orElse("");

        String commenterUsername = Optional.ofNullable(value.getFrom())
                .map(WebhookPayload.From::getUsername)
                .orElse("unknown_user");

        // Prevent recursion: Ignore comments from the Page itself
        if (commenterId.equals(pageId) || commenterId.equals(businessAccountId)) {
            log.debug("Ignoring self-comment from Page/Business ID: {}", commenterId);
            return;
        }

        log.info("Processing comment from @{} (Comment ID: {})", commenterUsername, value.getId());

        ReelConfig config = configManager.getReelConfig(value.getMedia().getId());
        if (!config.isActive()) {
            log.debug("Automation disabled for Reel ID: {}", value.getMedia().getId());
            return;
        }

        String commentText = Optional.ofNullable(value.getText()).map(String::toLowerCase).orElse("");
        String trigger = Optional.ofNullable(config.getTriggerKeyword()).map(String::toLowerCase).orElse("");

        if (!trigger.isEmpty() && commentText.contains(trigger)) {
            log.info("Trigger keyword '{}' detected in comment: '{}'", trigger, commentText);
            
            handleDM(value.getId(), config.getDmMessage());
            handlePublicReply(value.getId(), commenterUsername, config.getCommentReply());
        }
    }

    private void handleDM(String commentId, String dmTemplate) {
        Optional.ofNullable(dmTemplate)
                .filter(msg -> !msg.isBlank())
                .ifPresent(msg -> {
                    try {
                        String selectedMsg = getRandomMessage(msg);
                        instagramService.sendDM(commentId, selectedMsg);
                        log.info("Successfully sent DM for comment: {}", commentId);
                    } catch (Exception e) {
                        log.error("Failed to send DM for comment {}: {}", commentId, e.getMessage());
                    }
                });
    }

    private void handlePublicReply(String commentId, String username, String replyTemplate) {
        Optional.ofNullable(replyTemplate)
                .filter(msg -> !msg.isBlank())
                .ifPresent(msg -> {
                    try {
                        String selectedMsg = getRandomMessage(msg);
                        String personalizedReply = "@" + username + " " + selectedMsg;
                        instagramService.replyToComment(commentId, personalizedReply);
                        log.info("Successfully posted public reply for comment: {}", commentId);
                    } catch (Exception e) {
                        log.error("Failed to post public reply for comment {}: {}", commentId, e.getMessage());
                    }
                });
    }

    private String getRandomMessage(String msg) {
        if (msg == null || msg.isBlank()) return "";
        String[] options = msg.split("\\|");
        if (options.length == 1) return options[0].trim();
        return options[random.nextInt(options.length)].trim();
    }
}
