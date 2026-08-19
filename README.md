# Saga - Student Academic & Project Management System

Chào mừng đến với dự án **Saga**! Đây là một hệ thống quản lý học vụ và tích hợp nền tảng quản lý dự án (Jira, GitHub), được xây dựng trên kiến trúc hướng dịch vụ (Microservices-ready) dành cho môi trường giáo dục đại học.

Dự án này là một **Monorepo** chứa cả Frontend và Backend, giúp toàn bộ các kỹ sư (kể cả những người mới onboard) có thể dễ dàng follow tiến độ và cấu trúc của dự án.

---

## 📂 Cấu trúc Repository (Monorepo)

```text
saga/
├── saga-be/     # Backend (Spring Boot 3, Java 17, PostgreSQL, Redis)
└── saga-fe/     # Frontend (React / Next.js / TypeScript - Đang phát triển)
```

---

## 🛠️ Trạng thái Hệ Thống (Changelog & Progress)

### 1. Saga Backend (`saga-be`) - ĐÃ HOÀN THIỆN KIẾN TRÚC LÕI (CORE)
Backend được xây dựng chuẩn **Clean Architecture** kết hợp **Package-by-Feature**.

- **Security & Identity**:
  - Tích hợp đăng nhập Google OAuth2.
  - Phân quyền (Role-based): `ADMIN`, `LECTURER`, `STUDENT`.
  - Quản lý phiên đăng nhập cực bảo mật với **JWT** và **Redis Refresh Token** (Access Token hết hạn sẽ tự động gia hạn nhờ Refresh Token lưu trên Redis, TTL 7 ngày).

- **Academic Management (Học Vụ)**:
  - Cấu hình Học kỳ hiện tại (Active Semester).
  - Phân trang (Pagination) chuẩn RESTful API cho Master Data (Môn học, Khóa học).
  - Giảng viên Import danh sách sinh viên qua Excel (Giới hạn size 5MB, 1000 row) và chia nhóm Tự động.

- **Project Integration (Jira & GitHub)**:
  - Áp dụng Dependency Inversion (DIP) để module Project có thể giao tiếp lỏng lẻo với module Academic.
  - Sử dụng **WebClient** (Non-blocking) để kết nối Real API với Atlassian và GitHub (đổi OAuth Code lấy Cloud ID / Installation ID thật).
  - Tích hợp **Webhook** từ GitHub: Sử dụng `@Async` ThreadPool nhận Webhook không block I/O. Áp dụng Regex tự động móc nối Commit Message (chứa mã Jira VD: `SAGA-12`) vào Task.

### 2. Saga Frontend (`saga-fe`) - ĐANG LÊN KẾ HOẠCH (PLANNING)
- Dự kiến sử dụng ReactJS / NextJS với TypeScript.
- Cấu trúc thư mục theo tính năng tương đương với Backend (Auth, Academic, Project Dashboard).
- Thiết kế giao diện (UI) hiện đại, tập trung vào UX cho từng vai trò người dùng (Giảng viên, Sinh viên, Quản trị viên).

---

## 🚀 Hướng Dẫn Cài Đặt Chung (Dành cho Dev mới)

Để chạy dự án mượt mà trên môi trường Local, bạn cần chuẩn bị:

### 1. Requirements
- **Java 17+** (Cho Backend)
- **Node.js 18+** (Cho Frontend)
- **PostgreSQL**: Port `5432`
- **Redis**: Port `6379` (Khuyến nghị dùng Docker: `docker run -p 6379:6379 -d redis`)

### 2. Thiết lập Backend (`saga-be`)
Cấu hình các biến môi trường hoặc file `application.yml` trước khi chạy:
- Google OAuth: `GOOGLE_CLIENT_ID`
- Jira/GitHub OAuth: `JIRA_CLIENT_ID`, `JIRA_CLIENT_SECRET`, `GITHUB_CLIENT_ID`, `GITHUB_CLIENT_SECRET`

```bash
cd saga-be
./mvnw clean verify # Tự động chạy 20+ Unit Tests để đảm bảo code sạch
./mvnw spring-boot:run
```
*Tài liệu API Swagger*: `http://localhost:8080/swagger-ui/index.html`

### 3. Thiết lập Frontend (`saga-fe`)
(Sẽ cập nhật sau khi Frontend hoàn thiện khung sườn cơ bản).

---

> **Lưu ý dành cho Team**: 
> - Mọi Feature mới ở Backend **BẮT BUỘC** phải tuân theo cấu trúc Package-by-Feature và Clean Architecture. Không truy cập chéo Repository của module khác mà phải dùng Ports/Adapters.
> - Bất kỳ PR nào cũng phải đi kèm Unit Test (Sử dụng Mockito) và đạt Pass 100% trước khi Merge.
