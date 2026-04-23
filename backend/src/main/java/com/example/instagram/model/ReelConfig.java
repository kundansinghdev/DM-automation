package com.example.instagram.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReelConfig {
    @JsonProperty("trigger_keyword")
    private String triggerKeyword;
    
    @JsonProperty("dm_message")
    private String dmMessage;
    
    @JsonProperty("comment_reply")
    private String commentReply;
    
    private boolean active;
}
