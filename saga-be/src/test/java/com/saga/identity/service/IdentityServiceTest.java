package com.saga.identity.service;

import com.saga.identity.repository.JpaIdentityMapRepository;
import com.saga.identity.entity.ExternalProvider;
import com.saga.identity.entity.IdentityMap;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.UUID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.saga.shared.exception.BadRequestException;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class IdentityServiceTest {
        @Mock
        private JpaIdentityMapRepository identityMapRepository;
        @Mock
        private ExternalIdentityService externalIdentityPort;
        @InjectMocks
        private IdentityService identityService;

        @Test
        void testLinkGithub_Success() {
                UUID userId = UUID.randomUUID();
                ExternalUserProfile profile = ExternalUserProfile.builder()
                                .id("123456")
                                .name("Test User")
                                .email("test@github.com")
                                .build();
                when(externalIdentityPort.getGithubProfile("dummy-code")).thenReturn(profile);
                when(identityMapRepository.findByExternalIdAndExternalProvider("123456", ExternalProvider.GITHUB))
                                .thenReturn(Optional.empty());

                identityService.linkGithub(userId, "dummy-code");
                verify(identityMapRepository).deleteByInternalUserIdAndExternalProvider(userId,
                                ExternalProvider.GITHUB);
                verify(identityMapRepository).save(any(IdentityMap.class));
        }

        @Test
        void testLinkGithub_DuplicateLink_ThrowsBadRequestException() {
                UUID userId = UUID.randomUUID();
                UUID otherUserId = UUID.randomUUID();
                ExternalUserProfile profile = ExternalUserProfile.builder()
                                .id("123456")
                                .name("Test User")
                                .build();
                when(externalIdentityPort.getGithubProfile("dummy-code")).thenReturn(profile);

                IdentityMap existingMap = new IdentityMap();
                existingMap.setInternalUserId(otherUserId);

                when(identityMapRepository.findByExternalIdAndExternalProvider("123456", ExternalProvider.GITHUB))
                                .thenReturn(Optional.of(existingMap));

                assertThrows(BadRequestException.class, () -> identityService.linkGithub(userId, "dummy-code"));
        }

        @Test
        void testLinkJira_Success() {
                UUID userId = UUID.randomUUID();
                ExternalUserProfile profile = ExternalUserProfile.builder()
                                .id("jira-account-id")
                                .name("Jira User")
                                .email("jira@example.com")
                                .build();
                when(externalIdentityPort.getJiraProfile("dummy-code")).thenReturn(profile);
                when(identityMapRepository.findByExternalIdAndExternalProvider("jira-account-id",
                                ExternalProvider.JIRA)).thenReturn(Optional.empty());

                identityService.linkJira(userId, "dummy-code");
                verify(identityMapRepository).deleteByInternalUserIdAndExternalProvider(userId, ExternalProvider.JIRA);
                verify(identityMapRepository).save(any(IdentityMap.class));
        }

        @Test
        void testLinkJira_DuplicateLink_ThrowsBadRequestException() {
                UUID userId = UUID.randomUUID();
                UUID otherUserId = UUID.randomUUID();
                ExternalUserProfile profile = ExternalUserProfile.builder()
                                .id("jira-account-id")
                                .name("Jira User")
                                .build();
                when(externalIdentityPort.getJiraProfile("dummy-code")).thenReturn(profile);

                IdentityMap existingMap = new IdentityMap();
                existingMap.setInternalUserId(otherUserId);

                when(identityMapRepository.findByExternalIdAndExternalProvider("jira-account-id",
                                ExternalProvider.JIRA))
                                .thenReturn(Optional.of(existingMap));

                assertThrows(BadRequestException.class, () -> identityService.linkJira(userId, "dummy-code"));
        }
}
