package com.saga.project.service;

import com.saga.evaluation.calculator.SlicingPieCalculator;
import com.saga.evaluation.dto.StudentContributionDTO;
import com.saga.project.dto.dashboard.AdminDashboardResponse;
import com.saga.project.dto.dashboard.LecturerDashboardResponse;
import com.saga.project.dto.dashboard.StudentDashboardResponse;
import com.saga.user.entity.User;
import com.saga.project.graph.dto.GraphDataDTO;
import com.saga.project.repository.JpaSystemAuditLogRepository;

import com.saga.academic.repository.JpaClassRepository;
import com.saga.academic.repository.JpaCourseStudentRepository;
import com.saga.project.repository.JpaCommitDataRepository;
import com.saga.project.repository.JpaTaskRepository;
import com.saga.project.repository.JpaJiraBoardRepository;
import com.saga.project.repository.JpaGitRepoRepository;
import com.saga.project.graph.StudentNodeRepository;
import com.saga.academic.repository.JpaTeamMemberRepository;
import com.saga.academic.repository.JpaTeamRepository;
import com.saga.academic.entity.TeamMember;
import com.saga.academic.entity.Team;
import com.saga.project.entity.JiraBoard;
import com.saga.project.entity.Task;
import com.saga.evaluation.entity.PeerReview;
import com.saga.evaluation.entity.TaskWeightConfig;
import com.saga.evaluation.repository.JpaTaskWeightConfigRepository;
import com.saga.evaluation.repository.JpaPeerReviewRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Pageable;
import com.saga.user.repository.JpaUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final JpaUserRepository userRepository;
    private final JpaSystemAuditLogRepository auditLogRepository;
    private final JpaClassRepository classRepository;
    private final JpaCourseStudentRepository courseStudentRepository;
    private final JpaCommitDataRepository commitDataRepository;
    private final JpaTaskRepository taskRepository;
    private final JpaJiraBoardRepository jiraBoardRepository;
    private final JpaGitRepoRepository gitRepoRepository;
    private final StudentNodeRepository studentNodeRepository;
    private final JpaTeamMemberRepository teamMemberRepository;
    private final JpaTeamRepository teamRepository;
    private final JpaTaskWeightConfigRepository taskWeightConfigRepository;
    private final JpaPeerReviewRepository peerReviewRepository;

    @Cacheable(value = "dashboard:admin", key = "'global'")
    public AdminDashboardResponse getAdminDashboard() {
        boolean githubActive = gitRepoRepository.countByStatus(com.saga.project.entity.IntegrationStatus.LINKED) > 0;
        boolean jiraActive = jiraBoardRepository.countByStatus(com.saga.project.entity.IntegrationStatus.LINKED) > 0;

        return AdminDashboardResponse.builder()
                .totalUsers((int) userRepository.count())
                .totalStudents((int) userRepository.countByRole(com.saga.user.entity.Role.STUDENT))
                .totalLecturers((int) userRepository.countByRole(com.saga.user.entity.Role.LECTURER))
                .totalClasses((int) classRepository.count())
                .totalProjects((int) jiraBoardRepository.count())
                .totalCommitsSynced((int) commitDataRepository.count())
                .totalTasksSynced((int) taskRepository.count())
                .githubWebhookActive(githubActive)
                .jiraWebhookActive(jiraActive)
                .recentAuditLogs(auditLogRepository.findTop10ByOrderByCreatedAtDesc())
                .build();
    }

    @Cacheable(value = "dashboard:lecturer", key = "#lecturerId")
    public LecturerDashboardResponse getLecturerDashboard(UUID lecturerId) {
        List<java.time.LocalDateTime> taskDates = taskRepository.findTaskCompletedDatesByInstructorId(lecturerId);
        List<java.time.LocalDateTime> commitDates = commitDataRepository.findCommitDatesByInstructorId(lecturerId);
        
        Map<String, Integer> heatmap = new HashMap<>();
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        for (java.time.LocalDateTime dt : taskDates) {
            String dateStr = dt.format(formatter);
            heatmap.put(dateStr, heatmap.getOrDefault(dateStr, 0) + 1);
        }
        for (java.time.LocalDateTime dt : commitDates) {
            String dateStr = dt.format(formatter);
            heatmap.put(dateStr, heatmap.getOrDefault(dateStr, 0) + 1);
        }

        int supervisedTeams = (int) teamRepository.countBySupervisorId(lecturerId);
        int enrolledStudents = (int) courseStudentRepository.countUniqueStudentsByInstructorId(lecturerId);
        int isolatedStudents = (int) studentNodeRepository.countIsolatedStudents();

        return LecturerDashboardResponse.builder()
                .totalSupervisedTeams(supervisedTeams)
                .totalEnrolledStudents(enrolledStudents)
                .isolatedStudentsCount(isolatedStudents)
                .heatmapActivity(heatmap)
                .snaGraph(GraphDataDTO.builder().nodes(Collections.emptyList()).edges(Collections.emptyList()).build()) // Require specific Cypher graph retrieval later
                .build();
    }

    @Cacheable(value = "dashboard:student", key = "#studentId.toString() + '-' + (#courseId != null ? #courseId.toString() : 'all')")
    public StudentDashboardResponse getStudentDashboard(UUID studentId, UUID courseId) {

        User student = userRepository.findById(studentId).orElse(null);
        int totalCommits = 0;
        if (student != null && student.getEmail() != null) {
            totalCommits = (int) commitDataRepository.countByAuthorEmail(student.getEmail());
        }
        int totalTasks = (int) taskRepository.countByAssigneeId(studentId);

        StudentContributionDTO pieSlice = null;
        if (courseId != null) {
            List<TeamMember> studentMemberships = teamMemberRepository.findByStudentId(studentId);
            Team activeTeam = null;
            for (TeamMember tm : studentMemberships) {
                Team t = teamRepository.findById(tm.getTeamId()).orElse(null);
                if (t != null && courseId.equals(t.getCourseId())) {
                    activeTeam = t;
                    break;
                }
            }

            if (activeTeam != null) {
                final UUID activeTeamId = activeTeam.getId();
                JiraBoard board = jiraBoardRepository.findAll().stream().filter(b -> b.getTeamId().equals(activeTeamId))
                        .findFirst().orElse(null);
                if (board != null) {
                    List<Task> tasks = taskRepository.findByBoardId(board.getId(), Pageable.unpaged()).getContent();
                    List<TaskWeightConfig> weights = taskWeightConfigRepository.findByCourseId(courseId);

                    List<TeamMember> teamMembers = teamMemberRepository.findByTeamId(activeTeam.getId());
                    List<PeerReview> peerReviews = new ArrayList<>();
                    for (TeamMember tm : teamMembers) {
                        peerReviews.addAll(peerReviewRepository.findByRevieweeId(tm.getStudentId()));
                    }

                    List<StudentContributionDTO> pieResults = SlicingPieCalculator.calculate(tasks, weights,
                            peerReviews);
                    pieSlice = pieResults.stream().filter(p -> p.getStudentId().equals(studentId)).findFirst()
                            .orElse(null);
                }
            }
        }

        if (pieSlice == null) {
            pieSlice = StudentContributionDTO.builder()
                    .studentId(studentId)
                    .pieScore(0.0)
                    .calculatedPercentage(0.0)
                    .build();
        }

        Map<String, Double> radar = new HashMap<>();
        if (courseId != null) {
            List<TeamMember> studentMemberships = teamMemberRepository.findByStudentId(studentId);
            Team activeTeam = null;
            for (TeamMember tm : studentMemberships) {
                Team t = teamRepository.findById(tm.getTeamId()).orElse(null);
                if (t != null && courseId.equals(t.getCourseId())) {
                    activeTeam = t;
                    break;
                }
            }
            if (activeTeam != null) {
                final UUID activeTeamId = activeTeam.getId();
                JiraBoard board = jiraBoardRepository.findAll().stream().filter(b -> b.getTeamId().equals(activeTeamId)).findFirst().orElse(null);
                if (board != null) {
                    List<Task> allTasks = taskRepository.findByBoardId(board.getId(), Pageable.unpaged()).getContent();
                    for (Task t : allTasks) {
                        if (studentId.equals(t.getAssigneeId()) && t.getLabels() != null) {
                            for (String label : t.getLabels()) {
                                radar.put(label, radar.getOrDefault(label, 0.0) + (t.getStoryPoint() != null ? t.getStoryPoint() : 1.0));
                            }
                        }
                    }
                }
            }
        }
        
        if (radar.isEmpty()) {
            radar.put("Code", 0.0);
            radar.put("Test", 0.0);
            radar.put("Doc", 0.0);
            radar.put("Research", 0.0);
        }
        return StudentDashboardResponse.builder()
                .totalCommits(totalCommits)
                .totalTasksCompleted(totalTasks)
                .contributionSlices(pieSlice)
                .radarChartSkills(radar)
                .traceabilityGraph(
                        GraphDataDTO.builder().nodes(Collections.emptyList()).edges(Collections.emptyList()).build()) // Mock
                .build();
    }
}
