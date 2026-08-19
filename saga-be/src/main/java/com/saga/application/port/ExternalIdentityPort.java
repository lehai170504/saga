package com.saga.application.port;
import java.util.List;
public interface ExternalIdentityPort {
    ExternalUserProfile getGithubProfile(String code);
    ExternalUserProfile getJiraProfile(String code);
}