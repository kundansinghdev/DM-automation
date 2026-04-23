package com.example.instagram.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MediaItem {
    private String id;
    @JsonProperty("media_type")
    private String mediaType;
    @JsonProperty("media_url")
    private String mediaUrl;
    @JsonProperty("thumbnail_url")
    private String thumbnailUrl;
    private String permalink;
    private String caption;
}
