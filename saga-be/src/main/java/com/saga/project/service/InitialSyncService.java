package com.saga.project.service;

import com.saga.project.entity.Task;
import com.saga.project.entity.CommitData;
import com.saga.project.entity.JiraBoard;
import com.saga.project.entity.GitRepo;
import com.saga.project.entity.SyncStatus;
import com.saga.project.repository.JpaTaskRepository;
import com.saga.project.repository.JpaCommitDataRepository;
import com.saga.project.repository.JpaJiraBoardRepository;
import com.saga.project.repository.JpaGitRepoRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class InitialSyncService {

    private final JpaTaskRepository taskRepository;
    private final JpaCommitDataRepository commitDataRepository;
    private final JpaJiraBoardRepository jiraBoardRepository;
    private final JpaGitRepoRepository gitRepoRepository;

    public InitialSyncService(JpaTaskRepository taskRepository, JpaCommitDataRepository commitDataRepository, JpaJiraBoardRepository jiraBoardRepository, JpaGitRepoRepository gitRepoRepository) {
        this.taskRepository = taskRepository;
        this.commitDataRepository = commitDataRepository;
        this.jiraBoardRepository = jiraBoardRepository;
        this.gitRepoRepository = gitRepoRepository;
    }

    @Async
    @Transactional
    public void syncJiraTasks(UUID teamId, String siteId, String projectKey) {
        log.info("Starting background sync for Jira project {} (Site: {}) for Team: {}", projectKey, siteId, teamId);
        
        Optional<JiraBoard> boardOpt = jiraBoardRepository.findByTeamId(teamId);
        if (boardOpt.isEmpty()) return;
        JiraBoard board = boardOpt.get();
        board.setSyncStatus(SyncStatus.IN_PROGRESS);
        jiraBoardRepository.save(board);

        try {
            // TODO: Implement actual Atlassian API call to fetch all historical tasks
            // Use WebClient to call /rest/api/3/search?jql=project="SAGA"
            // Parse the results and save into JpaTaskRepository
            
            // Mock delay
            Thread.sleep(1000);

            board.setSyncStatus(SyncStatus.SUCCESS);
            board.setLastSyncedAt(LocalDateTime.now());
            board.setLastSyncMessage(null);
            log.info("Finished background sync for Jira project {}", projectKey);
        } catch (Exception e) {
            log.error("Failed background sync for Jira project {}", projectKey, e);
            board.setSyncStatus(SyncStatus.FAILED);
            board.setLastSyncMessage(e.getMessage());
        } finally {
            jiraBoardRepository.save(board);
        }
    }

    @Async
    @Transactional
    public void syncGithubCommits(UUID teamId, List<String> repoUrls) {
        log.info("Starting background sync for GitHub repos {} for Team: {}", repoUrls, teamId);
        
        List<GitRepo> repos = gitRepoRepository.findAllByTeamId(teamId);
        for (GitRepo repo : repos) {
            repo.setSyncStatus(SyncStatus.IN_PROGRESS);
        }
        gitRepoRepository.saveAll(repos);

        try {
            // TODO: Implement actual GitHub API call to fetch all historical commits
            // Use WebClient to call /repos/{owner}/{repo}/commits
            // Parse the results, save into JpaCommitDataRepository, and link tasks using TraceabilitySyncService regex
            
            // Mock delay
            Thread.sleep(1000);

            for (GitRepo repo : repos) {
                repo.setSyncStatus(SyncStatus.SUCCESS);
                repo.setLastSyncedAt(LocalDateTime.now());
                repo.setLastSyncMessage(null);
            }
            log.info("Finished background sync for GitHub repos");
        } catch (Exception e) {
            log.error("Failed background sync for GitHub repos", e);
            for (GitRepo repo : repos) {
                repo.setSyncStatus(SyncStatus.FAILED);
                repo.setLastSyncMessage(e.getMessage());
            }
        } finally {
            gitRepoRepository.saveAll(repos);
        }
    }
}
