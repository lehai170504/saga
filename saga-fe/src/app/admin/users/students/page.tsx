import { UserManagementTable } from "@/features/users/components/UserManagementTable";

export default function AdminStudentsPage() {
  return (
    <UserManagementTable
      title="Quản Lý Sinh Viên"
      description="Danh sách tài khoản sinh viên đăng ký và tham gia các khóa học."
      fixedRole="STUDENT"
      apiType="STUDENTS"
    />
  );
}
