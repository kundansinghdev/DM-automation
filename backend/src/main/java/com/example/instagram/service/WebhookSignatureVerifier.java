package com.example.instagram.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

@Slf4j
@Service
public class WebhookSignatureVerifier {

    @Value("${instagram.app.secret:}")
    private String appSecret;

    private static final String HMAC_SHA256_ALGORITHM = "HmacSHA256";

    /**
     * Verifies the X-Hub-Signature-256 header sent by Meta.
     * 
     * @param payload The raw request body as a string.
     * @param signatureHeader The value of the X-Hub-Signature-256 header.
     * @return true if the signature is valid, false otherwise.
     */
    public boolean verifySignature(String payload, String signatureHeader) {
        if (appSecret == null || appSecret.isBlank()) {
            log.warn("Instagram App Secret is not configured. Skipping signature verification.");
            return true; // Bypass in dev if not set
        }

        if (signatureHeader == null || !signatureHeader.startsWith("sha256=")) {
            log.warn("Missing or invalid X-Hub-Signature-256 header.");
            return false;
        }

        String signature = signatureHeader.substring(7); // Remove "sha256="
        try {
            String expectedSignature = calculateHmacSha256(payload, appSecret);
            boolean isValid = expectedSignature.equalsIgnoreCase(signature);
            
            if (!isValid) {
                log.error("Webhook signature mismatch! Incoming: {}, Expected: {}", signature, expectedSignature);
            }
            
            return isValid;
        } catch (Exception e) {
            log.error("Error during signature verification: {}", e.getMessage());
            return false;
        }
    }

    private String calculateHmacSha256(String data, String key) 
            throws NoSuchAlgorithmException, InvalidKeyException {
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), HMAC_SHA256_ALGORITHM);
        Mac mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
        mac.init(secretKeySpec);
        return toHexString(mac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
    }

    private String toHexString(byte[] bytes) {
        Formatter formatter = new Formatter();
        for (byte b : bytes) {
            formatter.format("%02x", b);
        }
        return formatter.toString();
    }
}
