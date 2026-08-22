import { UserManagementTable } from "@/features/users/components/UserManagementTable";

export default function AdminUsersPage() {
  return (
    <UserManagementTable
      title="Quản Lý Tất Cả Người Dùng"
      description="Quản lý toàn bộ tài khoản sinh viên và giảng viên trong hệ thống SAGA."
    />
  );
}
