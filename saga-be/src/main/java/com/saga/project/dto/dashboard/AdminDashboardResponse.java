package com.saga.project.dto.dashboard;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import com.saga.project.entity.SystemAuditLog;


@Data
@Builder
public class AdminDashboardResponse {
    private int totalUsers;
    private int totalStudents;
    private int totalLecturers;
    private int totalClasses;
    private int totalProjects;
    
    private int totalCommitsSynced;
    private int totalTasksSynced;
    
    private boolean githubWebhookActive;
    private boolean jiraWebhookActive;

    private List<SystemAuditLog> recentAuditLogs;
}
