package com.saga.identity.application.port;

public interface ExternalIdentityPort {
    ExternalUserProfile getGithubProfile(String code);

    ExternalUserProfile getJiraProfile(String code);
}