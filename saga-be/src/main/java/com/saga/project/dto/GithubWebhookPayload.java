package com.saga.project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GithubWebhookPayload {
    private String ref;
    private Repository repository;
    private List<Commit> commits;

    @Data
    @NoArgsConstructor
    public static class Repository {
        private String id;
        private String name;
    }

    @Data
    @NoArgsConstructor
    public static class Commit {
        private String id;
        private String message;
        private Author author;

        @Data
        @NoArgsConstructor
        public static class Author {
            private String email;
            private String name;
        }
    }
}

