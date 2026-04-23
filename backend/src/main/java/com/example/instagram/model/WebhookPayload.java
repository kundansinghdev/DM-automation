package com.example.instagram.model;

import lombok.Data;
import java.util.List;

@Data
public class WebhookPayload {
    private String object;
    private List<Entry> entry;

    @Data
    public static class Entry {
        private String id;
        private Long time;
        private List<Change> changes;
    }

    @Data
    public static class Change {
        private String field;
        private Value value;
    }

    @Data
    public static class Value {
        private String id;
        private String text;
        private Media media;
        private From from;
    }

    @Data
    public static class Media {
        private String id;
    }

    @Data
    public static class From {
        private String id;
        private String username;
    }
}
