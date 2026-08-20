# SYSTEM CONTEXT: SAGA (Student Activity Graph-Based Assessment)

## 1. PROJECT OVERVIEW
SAGA là hệ thống quản lý học vụ và đánh giá đồ án sinh viên dựa trên đồ thị. Hệ thống tích hợp với Jira và GitHub thông qua Webhook để tự động thu thập dữ liệu (Tasks, Commits), liên kết vết (Traceability) và tính toán điểm đóng góp của từng thành viên trong nhóm dựa trên cấu hình trọng số và đánh giá đồng cấp.

## 2. TECH STACK & ARCHITECTURE
*   **Backend:** Java 17, Spring Boot 3.
*   **Database:** PostgreSQL (Supabase) cho quan hệ, Neo4j cho đồ thị. Flyway quản lý migration (`resources/db/migration`).
*   **Architecture:** Package-by-Feature (Ví dụ: `auth`, `identity`, `academic`, `project`, `evaluation`, `graph`, `shared`).
*   **Internal Module Structure:** Phẳng và thực dụng:
    *   `controller`: REST APIs.
    *   `dto`: Request/Response Data Transfer Objects.
    *   `entity`: JPA Entities.
    *   `repository`: Spring Data JPA.
    *   `service`: Business Logic.
*   **Coding Conventions:** 
    *   ID luôn dùng `UUID` (Hibernate: `@GeneratedValue(strategy = GenerationType.UUID)`).
    *   Sử dụng Lombok (`@Data`, `@Builder`, `@NoArgsConstructor`, etc.).
    *   Response chuẩn hóa qua `ApiResponse<T>` hoặc `PageResponse<T>`.
*   **Testing Strategy (TDD):** 
    *   Bắt buộc có Unit Test cho mọi Service bằng JUnit 5, Mockito (`@Mock`, `@InjectMocks`) và AssertJ.
    *   Luôn cover 2 luồng: Happy Path và Exception Path.

## 3. DATABASE SCHEMA (POSTGRESQL - SOURCE OF TRUTH)
Cấu trúc cơ sở dữ liệu dựa trên các file migration thực tế đã chạy trong hệ thống. Agent bắt buộc phải tuân thủ nghiêm ngặt các trường dữ liệu này khi tạo Entity:

### Module: User & Identity (Đã có SQL)
*   **users**: `id` (PK, UUID), `email` (Unique), `name`, `picture`, `role`, `status`[cite: 1], `password`[cite: 6].
*   **students**: `id` (PK, UUID), `user_id` (FK -> users.id), `student_code` (Unique)[cite: 1].
*   **lecturers**: `id` (PK, UUID), `user_id` (FK -> users.id)[cite: 1].
*   **identity_map**: `id` (PK, UUID), `internal_user_id` (FK -> users.id), `external_provider`, `external_id`[cite: 2], `name`, `email`, `connected_at`[cite: 3].

### Module: Academic Master Data (Đã có SQL một phần)
*   **semesters**: `id` (PK, UUID), `code` (Unique)[cite: 4], `name`, `start_date`, `end_date`, `is_active`.
*   **subjects** (Target): `id` (PK, UUID), `subject_code` (Unique), `subject_name`.
*   **classes** (Target): `id` (PK, UUID), `class_code` (Unique).
*   **courses** (Target): `id` (PK, UUID), `semester_id`, `subject_id`, `class_id`, `instructor_id` (FK -> users).
*   **course_students**: `id` (PK, UUID), `course_id` (FK -> courses.id), `student_id` (FK -> users.id), `created_at`, `updated_at`[cite: 5].

*(Các module Project Integration, Traceability, và Evaluation sẽ được định nghĩa cấu trúc chi tiết trong các phase tiếp theo).*