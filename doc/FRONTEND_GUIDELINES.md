# 📊 SAGA - Tài Liệu Hướng Dẫn FE & Tích Hợp API (Frontend MVP)

Dự án này là hệ thống đánh giá quá trình liên tục (Continuous Assessment) dựa trên đồ thị (Graph-Based) của sinh viên. Hệ thống tích hợp sâu với GitHub (Commits, PRs), Jira (Tasks, Sprints) và **AI (Grok/Gemini)** để phân tích tự động tỷ lệ đóng góp của từng thành viên.

Tài liệu này là "kim chỉ nam" cho team Frontend (FE) thiết lập thư mục, quản lý state và gọi các API Backend (BE) đã xây dựng sẵn.

---

## 🛠 1. Tech Stack Chốt Hạ
- **Framework:** Next.js (App Router) + TypeScript.
- **Styling:** Tailwind CSS + shadcn/ui.
- **Graph Rendering:** Cytoscape.js (Cập nhật Imperative, KHÔNG nhét vào React State).
- **Server State:** TanStack Query (Quản lý API REST caching, refetching).
- **Client State:** Zustand (Chỉ lưu UI tĩnh như theme, filters, selected Node).
- **Real-time:** **STOMP over WebSocket** (Sử dụng thư viện `@stomp/stompjs` kết nối vào `/ws`).

> [!WARNING]
> **Chú ý về Real-time:** Backend đang sử dụng WebSocket (STOMP) chứ không phải SSE. Nhớ nhét JWT Token vào Header lúc thực hiện `CONNECT` frame để qua được lớp chặn bảo mật.

---

## 📂 2. Kiến Trúc Thư Mục (Feature-Sliced Design) & Mapping API

Hệ thống FE phải chia tách rõ ràng thành các `features`. Tuyệt đối không nhét mọi logic gọi API vào chung một thư mục. Dưới đây là danh sách mapping các nhóm API của Backend vào cấu trúc thư mục của Frontend.

```text
src/
├── app/                  # Lớp Routing (Next.js App Router). Chỉ import Component.
├── features/             # LỚP NGHIỆP VỤ CỐT LÕI (Domain)
│   ├── auth/             # Quản lý đăng nhập, JWT, User Profile
│   ├── admin/            # Dành cho role Admin (Quản lý User, System Audit Logs)
│   ├── academic/         # Quản lý Semester, Class, Group (Sinh viên + Giảng viên)
│   ├── project/          # Quản lý Sprint, Task, Backlog (Sinh viên + Giảng viên)
│   ├── teams/            # Quản lý nhóm và thành viên (Lecturer Team Controller)
│   ├── integrations/     # Quản lý cấu hình liên kết Jira, GitHub của team
│   ├── graph/            # Lõi hiển thị đồ thị Graph Neo4j
│   ├── dashboard/        # Bảng điều khiển thống kê (Dashboard Controller)
│   ├── evaluation/       # Tính điểm, peer review (Continuous Assessment)
│   └── notifications/    # Thông báo và WebSocket Alerts (AI Review Alerts)
│
├── components/           # UI Components chung (Button, Modal, Toast)
├── hooks/                # Custom Hooks (useWebSocket, useDebounce)
├── lib/                  # Cấu hình (axios.ts, queryClient.ts, stompClient.ts)
└── types/                # Interfaces & Types (User, Task, APIResponse...)
```

---

## 🔗 3. Danh Mục Tổng Hợp API Backend Trực Tiếp

Dưới đây là TOÀN BỘ các API thực tế đang chạy ở dưới Backend, được phân nhóm theo tính năng để Frontend dễ gọi:

### A. Feature: `auth` (Xác thực & Danh tính)
*Dành cho màn hình Login, Lấy token và Lấy thông tin user.*
- **Base Path:** `/api/v1/auth` & `/api/v1/identities`
- `POST /api/v1/auth/login-local`: Đăng nhập bằng DB.
- `POST /api/v1/auth/login`: Đăng nhập chung.
- `POST /api/v1/auth/refresh`: Refresh Token.
- `GET /api/v1/auth/me`: Thông tin User ngắn gọn.
- `POST /api/v1/auth/logout`: Đăng xuất.
- `GET /api/v1/identities/me`: Lấy Profile chi tiết.
- `DELETE /api/v1/identities/{provider}`: Hủy liên kết tài khoản mạng xã hội.

### B. Feature: `admin` (Quản trị hệ thống)
*Dành cho màn hình của Admin.*
- **Quản lý Users (AdminUserController)**
  - `GET /api/v1/admin/users/students`: Danh sách sinh viên.
  - `GET /api/v1/admin/users/lecturers`: Danh sách giảng viên có phân trang.
  - `GET /api/v1/admin/users/lecturers/all`: Toàn bộ giảng viên.
  - `PUT /api/v1/admin/users/{userId}/status`: Khóa/Mở tài khoản.
- **Quản lý Academic (AdminAcademicController)**
  - `GET`, `POST /api/v1/admin/academic/semesters`: Quản lý học kỳ.
  - `PUT /api/v1/admin/academic/semesters/{semesterId}/active`: Đặt học kỳ active.
  - `GET`, `POST /api/v1/admin/academic/courses`: Quản lý môn học mở (Course).
  - `GET`, `POST`, `PUT`, `DELETE /api/v1/admin/academic/subjects`: Quản lý danh mục môn (Subject).
  - `GET`, `POST`, `PUT`, `DELETE /api/v1/admin/academic/classes`: Quản lý Lớp (Class).
- **Lịch sử hệ thống (AdminAuditLogController)**
  - *(Các endpoint GET logs nội bộ hệ thống).*

### C. Feature: `academic` & `teams` (Học vụ)
*Dành cho Giảng viên & Sinh viên xem thông tin lớp học.*
- **Lecturer (LecturerAcademicController & LecturerTeamController)**
  - `GET /api/v1/lecturer/courses/{courseId}/students`: Sinh viên trong khóa học.
  - `GET /api/v1/lecturer/courses/{courseId}/teams`: Danh sách nhóm.
  - `GET /api/v1/courses/{courseId}/template`: Template của Course.
- **Student (StudentAcademicController)**
  - `GET /api/v1/student/courses/{courseId}/my-team`: Lấy nhóm của sinh viên đang login.

### D. Feature: `project` (Quản lý dự án)
*Dành cho quản lý Task (Jira) & Commit (GitHub) của 1 nhóm cụ thể.*
- **Dành cho Giảng viên (LecturerProjectController)**
  - `GET /api/v1/lecturer/teams/{teamId}/project/metrics`: Các chỉ số chung (Burndown, Velocity...).
  - `GET /api/v1/lecturer/teams/{teamId}/project/tasks`: Danh sách Task của nhóm.
  - `GET /api/v1/lecturer/teams/{teamId}/project/commits`: Danh sách Commit của nhóm.
- **Dành cho Sinh viên (StudentProjectController)**
  - `GET /api/v1/student/teams/{teamId}/project/metrics`
  - `GET /api/v1/student/teams/{teamId}/project/tasks`
  - `GET /api/v1/student/teams/{teamId}/project/commits`

### E. Feature: `integrations` (Kết nối Jira/GitHub)
*Lưu ý: API dùng OAuth2 flow để lấy quyền truy cập.*
- **Base Path:** `/api/v1/integrations`
- `GET /api/v1/integrations/jira/connect`: Bắt đầu luồng OAuth Jira.
- `GET /api/v1/integrations/jira/projects`: Lấy các dự án trên Jira.
- `POST /api/v1/integrations/jira/confirm`: Chốt dự án Jira liên kết.
- `POST /api/v1/integrations/jira/sync`: Nút bấm "Sync ngay".
- `DELETE /api/v1/integrations/jira`: Hủy liên kết Jira.
- *(Tương tự với `/github/install`, `/github/confirm`, `/github/sync`, `/github/delete`...)*

### F. Feature: `graph` (Vẽ đồ thị Neo4j)
*Kéo JSON vẽ bằng Cytoscape.*
- `GET /api/v1/lecturer/teams/{teamId}/sprints/{sprintId}/graph`: Lấy nodes/edges.
- `GET /api/v1/lecturer/teams/{teamId}/sprints/{sprintId}/stats`: Thống kê Graph nội bộ.

### G. Feature: `dashboard`
- `GET /api/v1/dashboard/admin`: Số liệu tổng đếm cho admin.
- `GET /api/v1/dashboard/lecturer/{lecturerId}`: Dashboard GV.
- `GET /api/v1/dashboard/student/{studentId}`: Dashboard SV.

### H. Feature: `evaluation` (Đánh giá & Chấm điểm)
- **Giảng viên (LecturerEvaluationController)**
  - `PUT /api/v1/lecturer/courses/{courseId}/evaluation/weights`: Sửa cấu hình trọng số môn học.
  - `PUT /api/v1/lecturer/teams/{teamId}/evaluation/weights`: Sửa trọng số cụ thể nhóm.
  - `POST /api/v1/lecturer/sprints/{sprintId}/students/{studentId}/override`: GV ghi đè điểm cho SV.
  - `GET /api/v1/lecturer/teams/{teamId}/sprints/{sprintId}/report`: Lấy bảng báo cáo điểm cuối Sprint.
- **Sinh viên (StudentEvaluationController)**
  - `POST /api/v1/student/evaluation/peer-reviews`: Nộp form đánh giá chéo đồng đội.
  - `GET /api/v1/student/teams/{teamId}/sprints/{sprintId}/report`: Xem bảng điểm kết quả Sprint.

### I. Feature: `notifications` & `webhooks`
- **Notification API (Dùng cho Notification Dropdown)**
  - `GET /api/v1/notifications/unread-count`: Đếm thông báo chưa đọc.
  - `PUT /api/v1/notifications/{id}/read`: Đánh dấu đọc 1 tin.
  - `PUT /api/v1/notifications/read-all`: Đánh dấu đọc tất cả.
- **Webhooks API (KHÔNG DÙNG CHO FE - Github/Jira gọi)**
  - `POST /api/v1/webhooks/jira`
  - `POST /api/v1/webhooks/github`

---

## 📝 4. Hướng Dẫn FE Dev Viết API Call (TanStack Query)

Khi FE tạo tính năng mới, tuân thủ nguyên tắc: Không dùng `axios.get` lộn xộn trong Component. Khai báo hook riêng biệt:

```typescript
// src/features/project/api/useTasks.ts
import { useQuery } from '@tanstack/react-query';
import axiosClient from '@/lib/axiosClient'; // Đã gắn JWT Interceptor

export const useTasks = (teamId: string) => {
  return useQuery({
    queryKey: ['tasks', teamId],
    queryFn: async () => {
      const response = await axiosClient.get(`/api/v1/lecturer/teams/${teamId}/project/tasks`);
      return response.data; // Trả về data định dạng của BE
    },
  });
};
```

---

## ⚠️ 5. Nguyên Tắc Sống Còn Hiệu Năng Graph (Cytoscape.js)
- **Bắt buộc Lazy Load:** Canvas Component bắt buộc phải dùng `next/dynamic` với `ssr: false`.
- **Tách bạch State:**
  - `TanStack Query` = Lấy cục JSON Graph từ BE.
  - `Zustand` = Chỉ lưu bộ Lọc (Filter), Theme, Node đang bị click.
  - `Cytoscape Core` = Update nodes/edges thông qua `cy.batch()` trực tiếp. Không render lại toàn React Component.
- **Đồng bộ Real-time:** Khi có WebSocket message báo "Graph updated", gọi `queryClient.invalidateQueries(['graph', teamId])` để TanStack tự fetch lại ngầm và update lại giao diện mượt mà.
- **Cảnh báo AI Code Tệ:** Subscribe vào WebSocket `/topic/team.{teamId}`. Khi có event báo lỗi từ Grok/Gemini (Commit lởm), dùng thư viện shadcn/ui Toast bung ngay 1 cảnh báo đỏ chóe lên góc màn hình.
