package com.saga.project.application.service;

import com.saga.project.application.dto.CommitDTO;
import com.saga.project.application.dto.ProjectMetricsDTO;
import com.saga.project.application.dto.TaskDTO;
import com.saga.project.application.port.ProjectSecurityPort;
import com.saga.project.infrastructure.persistence.entity.CommitDataEntity;
import com.saga.project.infrastructure.persistence.entity.GitRepoEntity;
import com.saga.project.infrastructure.persistence.entity.JiraBoardEntity;
import com.saga.project.infrastructure.persistence.entity.TaskEntity;
import com.saga.project.infrastructure.persistence.repository.JpaCommitDataRepository;
import com.saga.project.infrastructure.persistence.repository.JpaGitRepoRepository;
import com.saga.project.infrastructure.persistence.repository.JpaJiraBoardRepository;
import com.saga.project.infrastructure.persistence.repository.JpaTaskRepository;
import com.saga.shared.exception.UnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProjectDataQueryServiceTest {

    @Mock private JpaTaskRepository taskRepository;
    @Mock private JpaCommitDataRepository commitRepository;
    @Mock private JpaJiraBoardRepository jiraBoardRepository;
    @Mock private JpaGitRepoRepository gitRepoRepository;
    @Mock private ProjectSecurityPort securityPort;

    @InjectMocks
    private ProjectDataQueryService queryService;

    private UUID teamId;
    private UUID userId;

    @BeforeEach
    void setUp() {
        teamId = UUID.randomUUID();
        userId = UUID.randomUUID();
    }

    @Test
    void authorizeProjectAccess_Lecturer_Success() {
        when(securityPort.isLecturerOfTeam(userId, teamId)).thenReturn(true);
        assertDoesNotThrow(() -> queryService.authorizeProjectAccess(userId, teamId, "LECTURER"));
    }

    @Test
    void authorizeProjectAccess_Lecturer_Denied() {
        when(securityPort.isLecturerOfTeam(userId, teamId)).thenReturn(false);
        assertThrows(UnauthorizedException.class, 
            () -> queryService.authorizeProjectAccess(userId, teamId, "LECTURER"));
    }

    @Test
    void authorizeProjectAccess_Student_Success() {
        when(securityPort.isStudentInTeam(userId, teamId)).thenReturn(true);
        assertDoesNotThrow(() -> queryService.authorizeProjectAccess(userId, teamId, "STUDENT"));
    }

    @Test
    void authorizeProjectAccess_Student_Denied() {
        when(securityPort.isStudentInTeam(userId, teamId)).thenReturn(false);
        assertThrows(UnauthorizedException.class, 
            () -> queryService.authorizeProjectAccess(userId, teamId, "STUDENT"));
    }

    @Test
    void authorizeProjectAccess_InvalidRole_ThrowsException() {
        assertThrows(UnauthorizedException.class, 
            () -> queryService.authorizeProjectAccess(userId, teamId, "ADMIN"));
    }

    @Test
    void getProjectMetrics_WithJiraAndGit() {
        UUID boardId = UUID.randomUUID();
        UUID repoId = UUID.randomUUID();
        
        JiraBoardEntity board = new JiraBoardEntity();
        board.setId(boardId);
        
        GitRepoEntity repo = new GitRepoEntity();
        repo.setId(repoId);

        when(jiraBoardRepository.existsByTeamId(teamId)).thenReturn(true);
        when(gitRepoRepository.existsByTeamId(teamId)).thenReturn(true);
        
        when(jiraBoardRepository.findByTeamId(teamId)).thenReturn(Optional.of(board));
        when(gitRepoRepository.findByTeamId(teamId)).thenReturn(Optional.of(repo));
        
        when(taskRepository.countByBoardId(boardId)).thenReturn(10L);
        when(commitRepository.countByRepoId(repoId)).thenReturn(25L);

        ProjectMetricsDTO metrics = queryService.getProjectMetrics(teamId);

        assertTrue(metrics.isSyncedJira());
        assertTrue(metrics.isSyncedGithub());
        assertEquals(10L, metrics.getTotalTasks());
        assertEquals(25L, metrics.getTotalCommits());
    }

    @Test
    void getTeamTasks_BoardFound() {
        UUID boardId = UUID.randomUUID();
        JiraBoardEntity board = new JiraBoardEntity();
        board.setId(boardId);

        TaskEntity task = new TaskEntity();
        task.setId(UUID.randomUUID());
        task.setIssueKey("SAGA-123");

        Pageable pageable = PageRequest.of(0, 10);
        Page<TaskEntity> taskPage = new PageImpl<>(List.of(task));

        when(jiraBoardRepository.findByTeamId(teamId)).thenReturn(Optional.of(board));
        when(taskRepository.findByBoardId(boardId, pageable)).thenReturn(taskPage);

        Page<TaskDTO> result = queryService.getTeamTasks(teamId, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("SAGA-123", result.getContent().get(0).getIssueKey());
    }

    @Test
    void getTeamTasks_BoardNotFound() {
        Pageable pageable = PageRequest.of(0, 10);
        when(jiraBoardRepository.findByTeamId(teamId)).thenReturn(Optional.empty());

        Page<TaskDTO> result = queryService.getTeamTasks(teamId, pageable);

        assertTrue(result.isEmpty());
    }

    @Test
    void getTeamCommits_RepoFound() {
        UUID repoId = UUID.randomUUID();
        GitRepoEntity repo = new GitRepoEntity();
        repo.setId(repoId);

        CommitDataEntity commit = new CommitDataEntity();
        commit.setId(UUID.randomUUID());
        commit.setHash("abcdef");

        Pageable pageable = PageRequest.of(0, 10);
        Page<CommitDataEntity> commitPage = new PageImpl<>(List.of(commit));

        when(gitRepoRepository.findByTeamId(teamId)).thenReturn(Optional.of(repo));
        when(commitRepository.findByRepoId(repoId, pageable)).thenReturn(commitPage);

        Page<CommitDTO> result = queryService.getTeamCommits(teamId, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("abcdef", result.getContent().get(0).getHash());
    }

    @Test
    void getTeamCommits_RepoNotFound() {
        Pageable pageable = PageRequest.of(0, 10);
        when(gitRepoRepository.findByTeamId(teamId)).thenReturn(Optional.empty());

        Page<CommitDTO> result = queryService.getTeamCommits(teamId, pageable);

        assertTrue(result.isEmpty());
    }
}