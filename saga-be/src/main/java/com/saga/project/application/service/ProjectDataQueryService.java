package com.saga.project.application.service;

import com.saga.project.application.dto.CommitDTO;
import com.saga.project.application.dto.ProjectMetricsDTO;
import com.saga.project.application.dto.TaskDTO;
import com.saga.project.application.port.ProjectSecurityPort;
import com.saga.project.infrastructure.persistence.repository.JpaCommitDataRepository;
import com.saga.project.infrastructure.persistence.repository.JpaGitRepoRepository;
import com.saga.project.infrastructure.persistence.repository.JpaJiraBoardRepository;
import com.saga.project.infrastructure.persistence.repository.JpaTaskRepository;
import com.saga.shared.exception.UnauthorizedException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class ProjectDataQueryService {

    private final JpaTaskRepository taskRepository;
    private final JpaCommitDataRepository commitRepository;
    private final JpaJiraBoardRepository jiraBoardRepository;
    private final JpaGitRepoRepository gitRepoRepository;
    private final ProjectSecurityPort securityPort;

    public ProjectDataQueryService(
            JpaTaskRepository taskRepository,
            JpaCommitDataRepository commitRepository,
            JpaJiraBoardRepository jiraBoardRepository,
            JpaGitRepoRepository gitRepoRepository,
            ProjectSecurityPort securityPort) {
        this.taskRepository = taskRepository;
        this.commitRepository = commitRepository;
        this.jiraBoardRepository = jiraBoardRepository;
        this.gitRepoRepository = gitRepoRepository;
        this.securityPort = securityPort;
    }

    public void authorizeProjectAccess(UUID userId, UUID teamId, String role) {
        if ("LECTURER".equalsIgnoreCase(role)) {
            if (!securityPort.isLecturerOfTeam(userId, teamId)) {
                throw new UnauthorizedException("Access Denied: Bạn không phụ trách lớp của nhóm này.");
            }
        } else if ("STUDENT".equalsIgnoreCase(role)) {
            if (!securityPort.isStudentInTeam(userId, teamId)) {
                throw new UnauthorizedException("Access Denied: Bạn không phải thành viên của nhóm này.");
            }
        } else {
            throw new UnauthorizedException("Quyền truy cập không hợp lệ.");
        }
    }

    public ProjectMetricsDTO getProjectMetrics(UUID teamId) {
        boolean hasJira = jiraBoardRepository.existsByTeamId(teamId);
        boolean hasGit = gitRepoRepository.existsByTeamId(teamId);

        long totalTasks = jiraBoardRepository.findByTeamId(teamId)
                .map(board -> taskRepository.countByBoardId(board.getId())).orElse(0L);
        long totalCommits = gitRepoRepository.findByTeamId(teamId)
                .map(repo -> commitRepository.countByRepoId(repo.getId())).orElse(0L);

        return ProjectMetricsDTO.builder()
                .totalTasks(totalTasks)
                .totalCommits(totalCommits)
                .syncedJira(hasJira)
                .syncedGithub(hasGit)
                .build();
    }

    public Page<TaskDTO> getTeamTasks(UUID teamId, Pageable pageable) {
        return jiraBoardRepository.findByTeamId(teamId)
                .map(board -> taskRepository.findByBoardId(board.getId(), pageable)
                        .map(task -> TaskDTO.builder()
                                .id(task.getId())
                                .issueKey(task.getIssueKey())
                                .labels(task.getLabels())
                                .build()))
                .orElse(Page.empty());
    }

    public Page<CommitDTO> getTeamCommits(UUID teamId, Pageable pageable) {
        return gitRepoRepository.findByTeamId(teamId)
                .map(repo -> commitRepository.findByRepoId(repo.getId(), pageable)
                        .map(commit -> CommitDTO.builder()
                                .id(commit.getId())
                                .hash(commit.getHash())
                                .message(commit.getMessage())
                                .authorEmail(commit.getAuthorEmail())
                                .branchName(commit.getBranchName())
                                .build()))
                .orElse(Page.empty());
    }
}
