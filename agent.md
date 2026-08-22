# SYSTEM CONTEXT: SAGA (Student Activity Graph-Based Assessment)

## 1. PROJECT OVERVIEW
SAGA là hệ thống quản lý học vụ và đánh giá đồ án sinh viên dựa trên đồ thị. Hệ thống tích hợp với Jira và GitHub thông qua Webhook để tự động thu thập dữ liệu (Tasks, Commits), liên kết vết (Traceability) và tính toán điểm đóng góp của từng thành viên trong nhóm dựa trên cấu hình trọng số và đánh giá đồng cấp.

## 2. TECH STACK & ARCHITECTURE
*   **Backend:** Java 17, Spring Boot 3.
*   **Database:** PostgreSQL (Supabase) cho quan hệ, Neo4j cho đồ thị (hiện thực hóa logic CA). Flyway quản lý migration (resources/db/migration).
*   **Architecture:** Package-by-Feature (Ví dụ: auth, identity, academic, project, evaluation, graph, shared).
*   **Real-time:** WebSocket (STOMP) cho Notification & AI Review Alerts.
*   **AI Integration:** Sử dụng Strategy Pattern kết nối Grok (x.ai) và Gemini (Google) để phân tích Commit tự động.
*   **Internal Module Structure:** Phân vùng thư mục:
    *   controller: REST APIs.
    *   dto: Request/Response Data Transfer Objects.
    *   entity: JPA Entities.
    *   
epository: Spring Data JPA.
    *   service: Business Logic.
*   **Coding Conventions:** 
    *   ID luôn dùng UUID (Hibernate: @GeneratedValue(strategy = GenerationType.UUID)).
    *   Sử dụng Lombok (@Data, @Builder, @NoArgsConstructor, etc.).
    *   Response chuẩn hóa qua ApiResponse<T> hoặc PageResponse<T>.
*   **Testing Strategy (TDD):** 
    *   Bắt buộc có Unit Test cho mọi Service bằng JUnit 5, Mockito (@Mock, @InjectMocks) và AssertJ.
    *   Luôn cover 2 luồng: Happy Path và Exception Path.

## 3. DATABASE SCHEMA (POSTGRESQL - SOURCE OF TRUTH)
Cấu trúc cơ sở dữ liệu dựa trên các file migration thực tế đã chạy trong hệ thống. Agent bắt buộc phải tuân thủ nghiêm ngặt các trường dữ liệu này khi tạo Entity:

### Module: User & Identity
*   **users**: id (PK, UUID), email (Unique), 
ame, picture, 
ole, status, password.
*   **students**: id (PK, UUID), user_id (FK -> users.id), student_code (Unique).
*   **lecturers**: id (PK, UUID), user_id (FK -> users.id).
*   **identity_map**: id (PK, UUID), internal_user_id (FK -> users.id), external_provider, external_id, 
ame, email, connected_at.

### Module: Academic Master Data
*   **semesters**: id (PK, UUID), code (Unique), 
ame, start_date, end_date, is_active.
*   **subjects**: id (PK, UUID), subject_code (Unique), subject_name.
*   **classes**: id (PK, UUID), class_code (Unique).
*   **courses**: id (PK, UUID), semester_id, subject_id, class_id, instructor_id (FK -> users).
*   **course_students**: id (PK, UUID), course_id (FK -> courses.id), student_id (FK -> users.id), created_at, updated_at.
*   **teams**: id (PK, UUID), course_id, 
ame.
*   **team_members**: id (PK, UUID), team_id, student_id, role (LEADER, MEMBER), status.

### Module: Project Integration
*   **jira_boards**: id (PK, UUID), team_id (FK -> teams.id), site_id, project_key, sync_status (PENDING, IN_PROGRESS, SUCCESS, FAILED), last_synced_at, last_sync_message.
*   **git_repos**: id (PK, UUID), team_id (FK -> teams.id), repo_id, repo_name, repo_url, is_private, sync_status, last_synced_at, last_sync_message.

### Module: Traceability (Jira & Github Data)
*   **tasks** (Jira Tasks): id (PK, UUID), board_id (FK -> jira_boards), sprint_id, issue_key, story_point, assignee_id (FK -> users.id), summary, status.
*   **task_labels**: task_id (FK -> tasks), labels (String). (e.g., saga:be:code, saga:all:doc).
*   **task_attachments**: task_id (FK -> tasks), filename, url.
*   **commit_data**: id (PK, UUID), repo_id (FK -> git_repos), hash, message, author_email, branch_name, created_at.
*   **task_commit_links**: id (PK, UUID), task_id (FK -> tasks), commit_id (FK -> commit_data).

### Module: Notification & Audit Log
*   **notifications**: id (PK, UUID), recipient_id (FK -> users.id), team_id (FK -> teams.id, nullable), title, message, type (SYSTEM, AI_REVIEW, JIRA_SYNC, GITHUB_SYNC), is_read, created_at.
*   **system_audit_logs**: id (PK, UUID), action, entity_name, entity_id, performed_by, timestamp, details.

### Module: Evaluation (Đánh giá & Trọng số)
*   **task_weight_configs**: id (PK, UUID), course_id (nullable), team_id (nullable), doc_weight, code_weight, test_weight, research_weight.
*   **student_contributions**: id (PK, UUID), sprint_id, student_id, base_points, retrospective_coefficient, pie_score, calculated_percentage, overridden_percentage, override_reason, final_percentage.
*   **peer_reviews**: id (PK, UUID), sprint_id, reviewer_id, 
reviewee_id, score.
