package com.example.instagram.controller;

import com.example.instagram.model.WebhookPayload;
import com.example.instagram.service.AutomationService;
import com.example.instagram.service.WebhookSignatureVerifier;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping({"/webhook", "/api/v1/webhooks/instagram"})
@RequiredArgsConstructor
public class WebhookController {

    @Value("${instagram.verify.token}")
    private String expectedToken;

    private final AutomationService automationService;
    private final WebhookSignatureVerifier signatureVerifier;
    private final ObjectMapper objectMapper;

    /**
     * Endpoint for Meta Webhook Verification (GET request).
     */
    @GetMapping
    public ResponseEntity<String> verifyWebhook(
            @RequestParam("hub.mode") String mode,
            @RequestParam("hub.verify_token") String verifyToken,
            @RequestParam("hub.challenge") String challenge) {

        log.info("Received webhook verification request: mode={}, token={}", mode, verifyToken);

        if ("subscribe".equals(mode) && expectedToken.equals(verifyToken)) {
            log.info("Webhook verification successful");
            return ResponseEntity.ok(challenge);
        }
        
        log.warn("Webhook verification failed: token mismatch");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Verification failed");
    }

    /**
     * Endpoint for receiving Meta Webhook events (POST request).
     */
    @PostMapping
    public Map<String, String> handleWebhook(
            @RequestHeader(value = "X-Hub-Signature-256", required = false) String signature,
            @RequestBody String rawPayload) {
        
        log.debug("Received webhook event. Signature: {}", signature);

        // Security Verification
        if (!signatureVerifier.verifySignature(rawPayload, signature)) {
            log.error("Invalid webhook signature. Request rejected.");
            return Map.of("status", "error", "message", "Invalid signature");
        }

        try {
            WebhookPayload body = objectMapper.readValue(rawPayload, WebhookPayload.class);
            if (body == null || !"instagram".equals(body.getObject())) {
                log.debug("Ignoring non-instagram webhook event");
                return Map.of("status", "ignored");
            }

            processEntries(body);
        } catch (Exception e) {
            log.error("Critical error processing webhook: {}", e.getMessage(), e);
        }

        return Map.of("status", "ok");
    }

    private void processEntries(WebhookPayload body) {
        Optional.ofNullable(body.getEntry()).ifPresent(entries -> 
            entries.forEach(entry -> 
                Optional.ofNullable(entry.getChanges()).ifPresent(changes -> 
                    changes.stream()
                        .filter(change -> "comments".equals(change.getField()))
                        .forEach(change -> {
                            try {
                                automationService.processComment(change.getValue());
                            } catch (Exception e) {
                                log.error("Error processing comment change: {}", e.getMessage());
                            }
                        })
                )
            )
        );
    }
}
