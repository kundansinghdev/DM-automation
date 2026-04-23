package com.example.instagram.service;

import com.example.instagram.model.ReelConfig;
import com.example.instagram.model.ReelsData;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

@Slf4j
@Service
public class ConfigManager {
    private static final String CONFIG_FILE = "reels_config.json";
    private final ObjectMapper objectMapper;
    private ReelsData data;

    public ConfigManager() {
        this.objectMapper = new ObjectMapper()
                .enable(SerializationFeature.INDENT_OUTPUT);
    }

    @PostConstruct
    public void init() {
        loadConfig();
    }

    private synchronized void loadConfig() {
        File file = new File(CONFIG_FILE);
        log.info("Loading automation configuration from: {}", file.getAbsolutePath());
        
        if (!file.exists()) {
            log.info("Configuration file not found, creating default configuration.");
            this.data = createDefaultConfig();
            saveConfig();
        } else {
            try {
                this.data = objectMapper.readValue(file, ReelsData.class);
                if (this.data.getReels() == null) {
                    this.data.setReels(new HashMap<>());
                }
                log.info("Configuration loaded successfully. Active reel overrides: {}", this.data.getReels().size());
            } catch (IOException e) {
                log.error("Failed to read configuration file '{}'. Using defaults as fallback. Error: {}", CONFIG_FILE, e.getMessage());
                this.data = createDefaultConfig();
            }
        }
    }

    private ReelsData createDefaultConfig() {
        ReelsData defaultData = new ReelsData();
        defaultData.setReels(new HashMap<>());
        defaultData.setDefaultConfig(ReelConfig.builder()
                .triggerKeyword("info")
                .dmMessage("Thanks for your interest! Check your DMs.")
                .commentReply("Sent you a DM!")
                .active(true)
                .build());
        return defaultData;
    }

    public synchronized void saveConfig() {
        log.debug("Saving configuration to disk...");
        try {
            objectMapper.writeValue(new File(CONFIG_FILE), data);
            log.info("Configuration persisted successfully.");
        } catch (IOException e) {
            log.error("CRITICAL: Failed to save configuration to disk: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to save configuration", e);
        }
    }

    public ReelsData getAllConfigs() {
        return data;
    }

    public ReelConfig getReelConfig(String mediaId) {
        return data.getReels().getOrDefault(mediaId, data.getDefaultConfig());
    }

    public void updateReelConfig(String mediaId, ReelConfig config) {
        log.info("Updating automation config for media ID: {}", mediaId);
        data.getReels().put(mediaId, config);
        saveConfig();
    }
}
