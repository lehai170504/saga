package com.saga.project.dto;

import lombok.Data;

@Data
public class JiraWebhookPayload {
    private String webhookEvent;
    private String timestamp;
    private Issue issue;

    @Data
    public static class Issue {
        private String id;
        private String key;
        private Fields fields;
    }

    @Data
    public static class Fields {
        private String summary;
        private Assignee assignee;
        private Status status;
        private java.util.List<Attachment> attachment;
    }

    @Data
    public static class Attachment {
        private String filename;
        private String content; // URL to download
    }

    @Data
    public static class DummyFields {
        private String summary;
        private Assignee assignee;
        private Status status;
    }

    @Data
    public static class Assignee {
        private String accountId;
        private String displayName;
        private String emailAddress;
    }

    @Data
    public static class Status {
        private String name;
    }
}
