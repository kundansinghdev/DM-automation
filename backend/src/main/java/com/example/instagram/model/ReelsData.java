package com.example.instagram.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.HashMap;
import java.util.Map;

@Data
public class ReelsData {
    private Map<String, ReelConfig> reels = new HashMap<>();
    
    @JsonProperty("default")
    private ReelConfig defaultConfig;
}
