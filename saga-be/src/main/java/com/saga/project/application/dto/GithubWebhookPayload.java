package com.saga.project.application.dto;

import lombok.Data;
import java.util.List;

@Data
public class GithubWebhookPayload {
    private String ref;
    private Repository repository;
    private List<Commit> commits;

    @Data
    public static class Repository {
        private String id;
        private String name;
    }

    @Data
    public static class Commit {
        private String id;
        private String message;
        private Author author;

        @Data
        public static class Author {
            private String email;
            private String name;
        }
    }
}
