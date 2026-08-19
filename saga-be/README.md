# Saga Backend - Student Academic & Project Management System

Saga Backend là hệ thống cốt lõi quản lý Học vụ và Tích hợp dự án (Jira/GitHub), được thiết kế theo chuẩn **Clean Architecture** kết hợp với **Package-by-Feature**. Sự kết hợp này mang lại khả năng phân tách module mạnh mẽ (Loose Coupling), giúp dự án dễ dàng bảo trì và sẵn sàng chuyển đổi sang Microservices trong tương lai.

---

## 🏗 Kiến Trúc Hệ Thống (Architecture)

Hệ thống tuân thủ **Dependency Inversion Principle (DIP)**. Các module độc lập không "gọi" trực tiếp vào Repository của module khác. Thay vào đó, chúng giao tiếp thông qua các Interface (Ports) và được triển khai bởi các Adapters tại module cung cấp dữ liệu.

### Cấu Trúc Thư Mục (Package-by-Feature)
```text
saga-be/src/main/java/com/saga/
├── academic/      # Quản lý Học vụ & Đội nhóm (Semester, Subject, Course, Team)
├── auth/          # Xác thực, JWT, OAuth2 Google, Redis Refresh Token
├── identity/      # Quản lý liên kết tài khoản External (VD: Liên kết Identity Map)
├── project/       # Tích hợp hệ thống ngoài (Jira, GitHub), Webhook, Traceability
├── shared/        # Cấu hình chung, Security Filters, Exception Handlers
└── user/          # Quản lý người dùng cốt lõi (Admin, Lecturer, Student, Roles)
```
*Mỗi Feature module đều tuân theo Clean Architecture với các phân lớp: `domain`, `application`, `infrastructure`.*

---

## ✨ Tính Năng Đã Triển Khai (Features)

### 1. Security & Authentication (`auth`, `identity`, `user`)
- Đăng nhập qua **Google OAuth2** và ánh xạ vào hệ thống tài khoản nội bộ (Identity Map).
- Xác thực dựa trên **JWT (JSON Web Token)** với `JwtAuthenticationFilter`.
- Phân quyền (Role-Based Access Control) chi tiết: `ADMIN`, `LECTURER`, `STUDENT`.
- Tối ưu bảo mật với **Redis Refresh Token**: Cấp lại Access Token không cần đăng nhập lại, có Time-To-Live tự hủy sau 7 ngày.
- Cơ chế Token Blacklist hỗ trợ Logout an toàn.

### 2. Academic Management (`academic`)
- **Admin**: Quản lý Master Data (Học kỳ, Môn học, Khóa học). Thiết lập Active Semester (Học kỳ hiện tại) dạng Singleton. Phân trang dữ liệu (`Pageable`).
- **Lecturer (Giảng viên)**:
  - Import danh sách sinh viên vào Lớp học qua file **Excel (Apache POI)** (Validate file size < 5MB, tối đa 1000 dòng).
  - Quản lý chia nhóm sinh viên (Teams), giới hạn số lượng và phân định Nhóm trưởng (Leader).
  - **Data-Level Security**: LECTURER chỉ được quyền thao tác trên các Khóa học mà mình đang dạy (`instructorId`).

### 3. Project Integration & Traceability (`project`)
- **DIP in Action**: Sử dụng `TeamValidationPort` gọi chéo từ `project` sang `academic` một cách lỏng lẻo (Decoupled).
- **OAuth API Integration**: 
  - Tích hợp API thật bằng **WebClient** (Non-blocking).
  - Đổi mã Code lấy Jira Cloud ID (BoardID) và GitHub Installation ID để lưu trữ kết nối.
- **Traceability (Webhook Ingestion)**:
  - Tiếp nhận Webhook từ GitHub cho mỗi sự kiện Commit.
  - Sử dụng **@Async ThreadPool** để xử lý không đồng bộ (Non-blocking I/O), bọc `try-catch` ngăn ngừa sập luồng.
  - Sử dụng **Regex (`[A-Z]+-[0-9]+`)** quét tin nhắn Commit để bóc tách mã Jira, từ đó tự động móc nối Commit vào Task thông qua bảng `TaskCommitLink`.

---

## 🚀 Hướng Dẫn Chạy Dự Án

### Yêu cầu cấu hình (Prerequisites)
- **Java 17+**
- **PostgreSQL**: Chạy ở port `5432`
- **Redis**: Chạy ở port `6379` (Docker: `docker run -p 6379:6379 -d redis`)
- Môi trường (Environment Variables) hoặc cập nhật trong `application.yml`:
  - `GOOGLE_CLIENT_ID`
  - `JIRA_CLIENT_ID`, `JIRA_CLIENT_SECRET`, `JIRA_REDIRECT_URI`
  - `GITHUB_CLIENT_ID`, `GITHUB_CLIENT_SECRET`

### Khởi động (Running)
1. Cài đặt các dependencies và chạy Test:
```bash
./mvnw clean verify
```
2. Chạy ứng dụng Spring Boot:
```bash
./mvnw spring-boot:run
```

Hệ thống sẽ chạy tại `http://localhost:8080`.
Tài liệu API Swagger có sẵn tại `http://localhost:8080/swagger-ui/index.html`.
