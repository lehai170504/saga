package com.saga.project.dto.dashboard;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import com.saga.project.entity.SystemAuditLog;


@Data
@Builder
public class AdminDashboardResponse {
    // System Overview
    private int totalUsers;
    private int totalStudents;
    private int totalLecturers;
    private int totalClasses;
    private int totalProjects;
    
    // Engagement Stats
    private int totalCommitsSynced;
    private int totalTasksSynced;
    
    // Webhook Status (Mock)
    private boolean githubWebhookActive;
    private boolean jiraWebhookActive;

    // Audit Logs
    private List<SystemAuditLog> recentAuditLogs;
}
