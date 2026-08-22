# SAGA Backend - Chi Tiết Toàn Tập API Reference

Tài liệu này cung cấp danh sách đầy đủ 100% các API hiện có dưới Backend (8 Modules) kèm theo cấu trúc Request Body, Parameters và Response chi tiết để tiện cho việc tích hợp Frontend.

Mọi API thành công đều trả về dạng bọc ApiResponse<T>:
`json
{
  "success": true,
  "data": <T>, // Dữ liệu trả về
  "message": "Success",
  "errors": null,
  "timestamp": "2026-08-21T10:00:00"
}
`

---

## 1. Auth Module (/api/v1/auth)

### 1.1. POST /login (Đăng nhập Google)
- **Request Body**: { "token": "ya29..." } (Token Google từ Frontend trả về).
- **Response data**:
  `json
  {
    "accessToken": "ey...",
    "refreshToken": "ey...",
    "user": {
      "id": "UUID",
      "email": "student@fpt.edu.vn",
      "name": "Nguyễn Văn A",
      "role": "STUDENT"
    }
  }
  `

### 1.2. POST /login-local (Đăng nhập Test)
- **Request Body**: { "email": "admin@fpt.edu.vn", "password": "123" }
- **Response data**: Giống POST /login.

### 1.3. POST /refresh (Làm mới Token)
- **Query Params**: ?token={refreshToken}
- **Response data**: "ey..." (Chuỗi Access Token mới).

### 1.4. GET /me (Lấy thông tin User)
- **Response data**:
  `json
  {
    "email": "student@fpt.edu.vn",
    "name": "Nguyễn Văn A",
    "picture": "https://..."
  }
  `

### 1.5. POST /logout (Đăng xuất)
- Xóa session và revoke refresh token trên DB.
- **Response data**: "Logged out successfully"

---

## 2. Identity Module (/api/v1/identities)
Quản lý việc sinh viên link tài khoản Jira/Github cá nhân để nhận diện (không phải link Project).

### 2.1. GET /me (Lấy Identity)
- **Response data**: List<IdentityMap>
  `json
  [
    { "provider": "github", "providerUserId": "12345", "username": "nguyenvana" }
  ]
  `

### 2.2. POST /github/callback & POST /jira/callback
- **Request Body**: { "code": "...", "state": "..." }
- **Response data**: "Linked successfully"

### 2.3. DELETE /{provider} (Xóa liên kết)
- **Path Variable**: provider ("github" hoặc "jira")
- **Response data**: 
ull

---

## 3. Academic Module (Quản lý Master Data & Lớp học)

### 3.1. Admin API (/api/v1/admin/academic)
- **GET /subjects**, **GET /classes**: 
  - **Query Params**: page, size, sort.
  - **Response data**: Page DTO của Môn học hoặc Lớp học.
- **POST /subjects**, **POST /classes**: Tạo mới.
  - **Request Body**: { "code": "PRJ301", "name": "Java Web" }
- **POST /courses/{courseId}/import-roster**: 
  - **Request Type**: multipart/form-data (Upload file Excel SV).

### 3.2. Lecturer API (/api/v1/lecturer/courses)
- **GET /**: Lấy danh sách lớp mình dạy.
  - **Response data**:
    `json
    {
      "content": [
        { "id": "UUID", "semesterId": "UUID", "subjectId": "UUID", "classId": "UUID" }
      ]
    }
    `
- **GET /{courseId}/students**: Lấy DS sinh viên trong lớp.
- **GET /{courseId}/teams**: Lấy DS Nhóm (TeamDTO) trong lớp.
  - **Response data**:
    `json
    { "content": [ { "id": "UUID", "name": "Team 1" } ] }
    `
- **POST /api/v1/courses/{courseId}/import-teams**: Upload file phân nhóm (multipart/form-data).

### 3.3. Student API (/api/v1/student/courses)
- **GET /**: Lấy các lớp sinh viên đang tham gia.
- **GET /{courseId}/my-team**: Lấy chi tiết nhóm của mình.
  - **Response data**:
    `json
    {
      "id": "UUID",
      "name": "Team 1",
      "members": [
        { "id": "UUID", "name": "Student A", "role": "STUDENT" }
      ]
    }
    `

---

## 4. User Module (/api/v1/admin/users)

- **GET /students**, **GET /lecturers**: Lấy DS User (Admin).
  - **Query Params**: page, size, search, status.
  - **Response data**: Phân trang danh sách UserResponseDTO.
- **GET /lecturers/all**: Lấy tất cả GV không phân trang.
- **PUT /{userId}/status**: Khóa/Mở khóa.
  - **Query Params**: ?status=ACTIVE/INACTIVE/BANNED

---

## 5. Project Integration (/api/v1/integrations)
Dành cho Role **LEADER**.

### 5.1. Jira
- **GET /jira/connect?teamId=...**: Trả về URL string.
- **GET /jira/callback?code=...&state=...**: 
  - **Response data**: List<AvailableJiraSiteDTO>
    `json
    [ { "id": "site123", "url": "https://saga.atlassian.net", "name": "SAGA Workspace" } ]
    `
- **GET /jira/projects?teamId=...&siteId=...**: 
  - **Response data**: List<AvailableJiraProjectDTO>
    `json
    [ { "id": "proj1", "key": "SAGA", "name": "SAGA", "style": "classic" } ]
    `
- **POST /jira/confirm**:
  - **Query**: ?teamId=...
  - **Body**: { "siteId": "site123", "projectKey": "SAGA" }
- **POST /jira/sync?teamId=...**: Force sync (Không có body).
- **DELETE /jira?teamId=...**: Xóa kết nối Jira.

### 5.2. Github
- **GET /github/install?teamId=...**: Trả về URL cài đặt Github App.
- **POST /github/callback**: Body: { "code": "..." }
  - **Response data**: List<AvailableGithubRepoDTO>
- **POST /github/confirm**:
  - **Query**: ?teamId=...
  - **Body**: { "repoUrls": ["url1", "url2"] } (List String).
- **POST /github/sync?teamId=...**: Force sync.
- **DELETE /github?teamId=...**: Xóa kết nối Github.

---

## 6. Project Monitoring (/api/v1/lecturer/teams/{teamId}/project và /api/v1/student/teams/{teamId}/project)

- **GET /metrics**:
  - **Response data**:
    `json
    { "totalTasks": 25, "totalCommits": 130, "syncedJira": true, "syncedGithub": true }
    `
- **GET /tasks**: Phân trang Tasks (Jira).
  - **Response data**:
    `json
    {
      "content": [
        {
          "id": "UUID",
          "issueKey": "SAGA-1",
          "status": "DONE",
          "storyPoint": 5.0,
          "labels": ["saga:be:code"],
          "summary": "Fix bug login",
          "attachments": [
             { "filename": "doc.pdf", "url": "https://..." }
          ]
        }
      ]
    }
    `
- **GET /commits**: Phân trang Commits (Github).
  - **Response data**:
    `json
    {
      "content": [
        { "id": "UUID", "hash": "abc12", "message": "SAGA-1: Fixed", "authorEmail": "...", "branchName": "main" }
      ]
    }
    `

---

## 7. Webhooks (/api/v1/webhooks)
Hệ thống Atlassian/Github sẽ bắn Payload vào đây (Không yêu cầu JWT Auth).
- **POST /jira**: Body là JSON của Jira Webhook.
- **POST /github**: Body là JSON của Github Webhook.

---

## 8. Evaluation Module (Đánh giá sinh viên)

### 8.1. Lecturer
- **PUT /api/v1/lecturer/courses/{courseId}/evaluation/weights**: Ghi đè cấu hình Môn.
  - **Request Body**: { "docWeight": 0.2, "codeWeight": 0.5, "testWeight": 0.3 }
- **PUT /api/v1/lecturer/teams/{teamId}/evaluation/weights?courseId=...**: Ghi đè cấu hình Nhóm.
- **POST /api/v1/lecturer/sprints/{sprintId}/students/{studentId}/override**: Bắt buộc sửa phần trăm (Manual override).
  - **Request Body**: { "overriddenPercentage": 20.5, "overrideReason": "Chấm tay" }
- **GET /api/v1/lecturer/teams/{teamId}/sprints/{sprintId}/report**: Lấy báo cáo điểm.
  - **Response data**: SprintReportDTO
    `json
    {
      "sprintId": "Sprint 1",
      "contributions": [
        {
          "studentId": "UUID",
          "basePoints": 15.0,
          "retrospectiveCoefficient": 1.2,
          "pieScore": 18.0,
          "finalPercentage": 25.5
        }
      ]
    }
    `

### 8.2. Student
- **POST /api/v1/student/evaluation/peer-reviews?studentId=...**: Nộp đánh giá chéo.
  - **Request Body**: Array các đánh giá cho đồng đội.
- **GET /api/v1/student/teams/{teamId}/sprints/{sprintId}/report**: Giống API của Lecturer nhưng Student chỉ được xem.
