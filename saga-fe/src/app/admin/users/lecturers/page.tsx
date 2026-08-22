import { UserManagementTable } from "@/features/users/components/UserManagementTable";

export default function AdminLecturersPage() {
  return (
    <UserManagementTable
      title="Quản Lý Giảng Viên"
      description="Danh sách các giảng viên phụ trách giảng dạy và quản lý môn học."
      fixedRole="LECTURER"
      apiType="LECTURERS"
    />
  );
}
