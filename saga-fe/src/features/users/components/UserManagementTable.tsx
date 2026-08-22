"use client";

import { useState } from "react";
import { useAdminUsers, useAdminStudents, useAdminLecturers } from "../api/useUserAdmin";
import { UserStatusDropdown } from "./UserStatusDropdown";
import { Card, CardContent } from "@/components/ui/card";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { Button } from "@/components/ui/button";
import {
  Search,
  Users,
  GraduationCap,
  Briefcase,
  RefreshCw,
  ChevronLeft,
  ChevronRight,
  Sparkles,
  Filter,
  CheckCircle2,
  XCircle,
  MoreVertical,
  X,
} from "lucide-react";

interface UserManagementTableProps {
  title: string;
  description: string;
  fixedRole?: "STUDENT" | "LECTURER";
  apiType?: "ALL" | "STUDENTS" | "LECTURERS";
}

export function UserManagementTable({ title, description, fixedRole, apiType = "ALL" }: UserManagementTableProps) {
  const [page, setPage] = useState(0);
  const [size] = useState(10);
  const [search, setSearch] = useState("");
  const [status, setStatus] = useState("");
  const [role, setRole] = useState<string>(fixedRole || "");

  const isStudentsApi = apiType === "STUDENTS";
  const isLecturersApi = apiType === "LECTURERS";
  const isUsersApi = apiType === "ALL";

  const usersQuery = useAdminUsers(
    { page, size, search, status, role: fixedRole || role },
    { enabled: isUsersApi }
  );

  const studentsQuery = useAdminStudents(
    { page, size, search, status },
    { enabled: isStudentsApi }
  );

  const lecturersQuery = useAdminLecturers(
    { page, size, search, status },
    { enabled: isLecturersApi }
  );

  const activeQuery = isStudentsApi
    ? studentsQuery
    : isLecturersApi
    ? lecturersQuery
    : usersQuery;

  const { data: pageData, isLoading, isError, isFetching, refetch } = activeQuery;

  const displayUsers = pageData?.content?.filter(
    (u) => u.role?.toUpperCase() !== "ADMIN"
  ) || [];

  const totalCount = pageData?.totalElements ?? 0;

  const getRoleBadge = (userRole?: string) => {
    switch (userRole?.toUpperCase()) {
      case "LECTURER":
        return (
          <span className="inline-flex items-center gap-1.5 px-3 py-1 text-xs font-semibold bg-blue-500/10 text-blue-700 dark:text-blue-400 rounded-full border border-blue-200/80 dark:border-blue-800/40 shadow-xs">
            <span className="w-1.5 h-1.5 rounded-full bg-blue-600 dark:bg-blue-400"></span>
            Giảng viên
          </span>
        );
      case "STUDENT":
        return (
          <span className="inline-flex items-center gap-1.5 px-3 py-1 text-xs font-semibold bg-emerald-500/10 text-emerald-700 dark:text-emerald-400 rounded-full border border-emerald-200/80 dark:border-emerald-800/40 shadow-xs">
            <span className="w-1.5 h-1.5 rounded-full bg-emerald-600 dark:bg-emerald-400"></span>
            Sinh viên
          </span>
        );
      default:
        return (
          <span className="inline-flex items-center gap-1.5 px-3 py-1 text-xs font-medium bg-slate-100 dark:bg-slate-800 text-slate-600 dark:text-slate-300 rounded-full">
            {userRole || "Thành viên"}
          </span>
        );
    }
  };

  const getStatusBadge = (userStatus?: string) => {
    switch (userStatus?.toUpperCase()) {
      case "ACTIVE":
        return (
          <span className="inline-flex items-center gap-1.5 px-3 py-1 text-xs font-semibold bg-emerald-50 dark:bg-emerald-950/40 text-emerald-700 dark:text-emerald-300 rounded-full border border-emerald-200/60 dark:border-emerald-800/40">
            <span className="relative flex h-2 w-2">
              <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-emerald-400 opacity-75"></span>
              <span className="relative inline-flex rounded-full h-2 w-2 bg-emerald-500"></span>
            </span>
            Đang hoạt động
          </span>
        );
      case "INACTIVE":
      case "BLOCKED":
        return (
          <span className="inline-flex items-center gap-1.5 px-3 py-1 text-xs font-semibold bg-rose-50 dark:bg-rose-950/40 text-rose-600 dark:text-rose-400 rounded-full border border-rose-200/60 dark:border-rose-800/40">
            <span className="h-2 w-2 rounded-full bg-rose-500"></span>
            Tạm khóa
          </span>
        );
      default:
        return (
          <span className="inline-flex items-center gap-1.5 px-3 py-1 text-xs font-medium bg-slate-100 dark:bg-slate-800 text-slate-600 dark:text-slate-300 rounded-full">
            <span className="h-2 w-2 rounded-full bg-slate-400"></span>
            {userStatus || "Hoạt động"}
          </span>
        );
    }
  };

  const studentCount = fixedRole === "STUDENT"
    ? totalCount
    : displayUsers.filter((u) => u.role?.toUpperCase() === "STUDENT").length;

  const lecturerCount = fixedRole === "LECTURER"
    ? totalCount
    : displayUsers.filter((u) => u.role?.toUpperCase() === "LECTURER").length;

  return (
    <div className="p-6 md:p-8 w-full space-y-8 bg-slate-50/50 dark:bg-slate-950 min-h-screen transition-colors duration-300">
      {/* Top Header Banner */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 bg-gradient-to-r from-indigo-50/90 via-blue-50/80 to-indigo-50/90 dark:from-slate-950 dark:via-indigo-950/90 dark:to-slate-950 p-6 md:p-8 rounded-3xl text-slate-900 dark:text-white shadow-md shadow-indigo-500/5 dark:shadow-xl dark:shadow-slate-900/10 border border-indigo-100/80 dark:border-slate-800/80 relative overflow-hidden transition-colors duration-300">
        {/* Glowing Decorative Backdrop Blur */}
        <div className="absolute top-0 right-0 w-72 h-72 bg-indigo-500/10 dark:bg-indigo-500/20 rounded-full blur-3xl pointer-events-none" />
        <div className="absolute bottom-0 left-1/3 w-60 h-60 bg-blue-500/10 dark:bg-blue-500/15 rounded-full blur-3xl pointer-events-none" />

        <div className="relative z-10 space-y-1">
          <div className="flex items-center gap-2">
            <span className="px-3 py-0.5 text-xs font-bold uppercase tracking-widest bg-indigo-100 dark:bg-indigo-500/30 text-indigo-700 dark:text-indigo-300 rounded-full border border-indigo-200 dark:border-indigo-400/30 flex items-center gap-1.5 w-fit">
              <Sparkles className="w-3.5 h-3.5" />
              SAGA Management
            </span>
          </div>
          <h1 className="text-2xl md:text-3xl font-extrabold tracking-tight text-slate-900 dark:text-white">{title}</h1>
          <p className="text-slate-600 dark:text-slate-300 text-sm max-w-xl">{description}</p>
        </div>
      </div>

      {/* KPI Stats Summary Cards (Only show on Main All-Users page) */}
      {!fixedRole && apiType === "ALL" && (
        <div className="grid grid-cols-1 sm:grid-cols-3 gap-5">
          <div className="p-5 rounded-2xl bg-white dark:bg-slate-900 border border-slate-200/80 dark:border-slate-800 shadow-sm flex items-center gap-4 hover:shadow-md transition-all">
            <div className="w-12 h-12 rounded-2xl bg-indigo-50 dark:bg-indigo-950/60 text-indigo-600 dark:text-indigo-400 flex items-center justify-center font-bold shadow-xs border border-indigo-100 dark:border-indigo-800/40 shrink-0">
              <Users className="w-6 h-6" />
            </div>
            <div>
              <p className="text-xs font-semibold text-slate-400 dark:text-slate-500 uppercase tracking-wider">
                Tổng số người dùng
              </p>
              <p className="text-2xl font-black text-slate-900 dark:text-slate-100 mt-0.5">{totalCount}</p>
            </div>
          </div>

          <div className="p-5 rounded-2xl bg-white dark:bg-slate-900 border border-slate-200/80 dark:border-slate-800 shadow-sm flex items-center gap-4 hover:shadow-md transition-all">
            <div className="w-12 h-12 rounded-2xl bg-emerald-50 dark:bg-emerald-950/60 text-emerald-600 dark:text-emerald-400 flex items-center justify-center font-bold shadow-xs border border-emerald-100 dark:border-emerald-800/40 shrink-0">
              <GraduationCap className="w-6 h-6" />
            </div>
            <div>
              <p className="text-xs font-semibold text-slate-400 dark:text-slate-500 uppercase tracking-wider">
                Sinh viên hệ thống
              </p>
              <p className="text-2xl font-black text-slate-900 dark:text-slate-100 mt-0.5">{studentCount}</p>
            </div>
          </div>

          <div className="p-5 rounded-2xl bg-white dark:bg-slate-900 border border-slate-200/80 dark:border-slate-800 shadow-sm flex items-center gap-4 hover:shadow-md transition-all">
            <div className="w-12 h-12 rounded-2xl bg-blue-50 dark:bg-blue-950/60 text-blue-600 dark:text-blue-400 flex items-center justify-center font-bold shadow-xs border border-blue-100 dark:border-blue-800/40 shrink-0">
              <Briefcase className="w-6 h-6" />
            </div>
            <div>
              <p className="text-xs font-semibold text-slate-400 dark:text-slate-500 uppercase tracking-wider">
                Giảng viên bộ môn
              </p>
              <p className="text-2xl font-black text-slate-900 dark:text-slate-100 mt-0.5">{lecturerCount}</p>
            </div>
          </div>
        </div>
      )}

      {/* Main Table Card Container */}
      <Card className="border border-slate-200/80 dark:border-slate-800 shadow-md shadow-slate-200/40 dark:shadow-none rounded-3xl overflow-hidden bg-white dark:bg-slate-900 transition-colors">
        {/* Filter Toolbar */}
        <div className="p-5 md:p-6 bg-slate-50/40 dark:bg-slate-900/60 border-b border-slate-100 dark:border-slate-800 flex flex-col lg:flex-row lg:items-center justify-between gap-4">
          <div className="relative flex-1 max-w-md">
            <Search className="w-4 h-4 absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-400" />
            <input
              type="text"
              placeholder="Tìm kiếm theo họ tên hoặc email..."
              value={search}
              onChange={(e) => {
                setSearch(e.target.value);
                setPage(0);
              }}
              className="w-full pl-10 pr-9 py-2.5 text-sm bg-white dark:bg-slate-800 border border-slate-200 dark:border-slate-700 text-slate-900 dark:text-slate-100 rounded-2xl outline-none focus:border-indigo-500 focus:ring-4 focus:ring-indigo-500/10 transition-all placeholder:text-slate-400 dark:placeholder:text-slate-500 shadow-xs"
            />
            {search && (
              <button
                onClick={() => setSearch("")}
                className="absolute right-3 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-600 dark:hover:text-slate-200"
              >
                <X className="w-4 h-4" />
              </button>
            )}
          </div>

          <div className="flex flex-wrap items-center gap-3">
            <div className="flex items-center bg-slate-200/60 dark:bg-slate-800 p-1 rounded-2xl text-xs font-semibold text-slate-600 dark:text-slate-300">
              <button
                onClick={() => { setStatus(""); setPage(0); }}
                className={`px-3 py-1.5 rounded-xl transition-all cursor-pointer ${status === "" ? "bg-white dark:bg-slate-700 text-slate-900 dark:text-white shadow-xs font-bold" : "hover:text-slate-900 dark:hover:text-white"}`}
              >
                Tất cả
              </button>
              <button
                onClick={() => { setStatus("ACTIVE"); setPage(0); }}
                className={`px-3 py-1.5 rounded-xl transition-all cursor-pointer ${status === "ACTIVE" ? "bg-white dark:bg-slate-700 text-emerald-700 dark:text-emerald-400 shadow-xs font-bold" : "hover:text-slate-900 dark:hover:text-white"}`}
              >
                Hoạt động
              </button>
              <button
                onClick={() => { setStatus("INACTIVE"); setPage(0); }}
                className={`px-3 py-1.5 rounded-xl transition-all cursor-pointer ${status === "INACTIVE" ? "bg-white dark:bg-slate-700 text-rose-700 dark:text-rose-400 shadow-xs font-bold" : "hover:text-slate-900 dark:hover:text-white"}`}
              >
                Tạm khóa
              </button>
            </div>

            {!fixedRole && (
              <div className="flex items-center bg-slate-200/60 dark:bg-slate-800 p-1 rounded-2xl text-xs font-semibold text-slate-600 dark:text-slate-300">
                <button
                  onClick={() => { setRole(""); setPage(0); }}
                  className={`px-3 py-1.5 rounded-xl transition-all cursor-pointer ${role === "" ? "bg-white dark:bg-slate-700 text-indigo-700 dark:text-indigo-300 shadow-xs font-bold" : "hover:text-slate-900 dark:hover:text-white"}`}
                >
                  Tất cả vai trò
                </button>
                <button
                  onClick={() => { setRole("STUDENT"); setPage(0); }}
                  className={`px-3 py-1.5 rounded-xl transition-all cursor-pointer ${role === "STUDENT" ? "bg-white dark:bg-slate-700 text-emerald-700 dark:text-emerald-400 shadow-xs font-bold" : "hover:text-slate-900 dark:hover:text-white"}`}
                >
                  Sinh viên
                </button>
                <button
                  onClick={() => { setRole("LECTURER"); setPage(0); }}
                  className={`px-3 py-1.5 rounded-xl transition-all cursor-pointer ${role === "LECTURER" ? "bg-white dark:bg-slate-700 text-blue-700 dark:text-blue-400 shadow-xs font-bold" : "hover:text-slate-900 dark:hover:text-white"}`}
                >
                  Giảng viên
                </button>
              </div>
            )}
          </div>
        </div>

        {/* Table Content */}
        <CardContent className="p-0">
          <Table>
            <TableHeader className="bg-slate-50/70 dark:bg-slate-800/50 border-b border-slate-100 dark:border-slate-800">
              <TableRow className="hover:bg-transparent border-b border-slate-100 dark:border-slate-800">
                <TableHead className="font-bold text-slate-700 dark:text-slate-300 text-xs uppercase tracking-wider pl-6 py-4">Tài Khoản Người Dùng</TableHead>
                <TableHead className="font-bold text-slate-700 dark:text-slate-300 text-xs uppercase tracking-wider py-4">Email</TableHead>
                <TableHead className="font-bold text-slate-700 dark:text-slate-300 text-xs uppercase tracking-wider py-4">Vai Trò</TableHead>
                <TableHead className="font-bold text-slate-700 dark:text-slate-300 text-xs uppercase tracking-wider pr-6 py-4">Trạng Thái</TableHead>
              </TableRow>
            </TableHeader>

            <TableBody>
              {isLoading ? (
                Array.from({ length: 5 }).map((_, i) => (
                  <TableRow key={i} className="animate-pulse border-b border-slate-100 dark:border-slate-800">
                    <TableCell className="pl-6 py-4">
                      <div className="flex items-center gap-3">
                        <div className="w-10 h-10 rounded-full bg-slate-200 dark:bg-slate-800" />
                        <div className="space-y-1">
                          <div className="w-32 h-4 bg-slate-200 dark:bg-slate-800 rounded-md" />
                          <div className="w-20 h-3 bg-slate-100 dark:bg-slate-850 rounded-md" />
                        </div>
                      </div>
                    </TableCell>
                    <TableCell><div className="w-40 h-4 bg-slate-200 dark:bg-slate-800 rounded-md" /></TableCell>
                    <TableCell><div className="w-24 h-6 bg-slate-200 dark:bg-slate-800 rounded-full" /></TableCell>
                    <TableCell className="pr-6"><div className="w-24 h-6 bg-slate-200 dark:bg-slate-800 rounded-full" /></TableCell>
                  </TableRow>
                ))
              ) : isError ? (
                <TableRow>
                  <TableCell colSpan={4} className="text-center py-16 text-slate-500 dark:text-slate-400">
                    <div className="flex flex-col items-center gap-2 max-w-sm mx-auto">
                      <XCircle className="w-10 h-10 text-rose-500" />
                      <p className="font-bold text-slate-800 dark:text-slate-200 text-base">Lỗi kết nối máy chủ</p>
                      <p className="text-xs text-slate-500 dark:text-slate-400">Không thể tải danh sách người dùng. Vui lòng kiểm tra lại mạng hoặc thử lại.</p>
                      <Button size="sm" onClick={() => refetch()} className="mt-2 rounded-xl bg-indigo-600 hover:bg-indigo-700 text-white">Thử lại</Button>
                    </div>
                  </TableCell>
                </TableRow>
              ) : displayUsers.length === 0 ? (
                <TableRow>
                  <TableCell colSpan={4} className="text-center py-16 text-slate-500 dark:text-slate-400">
                    <div className="flex flex-col items-center gap-2 max-w-sm mx-auto">
                      <div className="w-12 h-12 rounded-2xl bg-slate-100 dark:bg-slate-800 flex items-center justify-center text-slate-400 dark:text-slate-500">
                        <Filter className="w-6 h-6" />
                      </div>
                      <p className="font-bold text-slate-800 dark:text-slate-200 text-base">Không có dữ liệu</p>
                      <p className="text-xs text-slate-400 dark:text-slate-500">Không tìm thấy tài khoản nào khớp với từ khóa tìm kiếm hoặc bộ lọc.</p>
                    </div>
                  </TableCell>
                </TableRow>
              ) : (
                displayUsers.map((user) => (
                  <TableRow
                    key={user.id}
                    className="hover:bg-slate-50/80 dark:hover:bg-slate-800/60 transition-all duration-150 group border-b border-slate-100/60 dark:border-slate-800/60"
                  >
                    <TableCell className="pl-6 py-4">
                      <div className="flex items-center gap-3.5">
                        <div className="relative shrink-0">
                          {user.picture ? (
                            <img
                              src={user.picture}
                              alt={user.name || "User Avatar"}
                              className="w-10 h-10 rounded-full object-cover ring-2 ring-slate-100 dark:ring-slate-800 shadow-sm group-hover:scale-105 transition-transform"
                            />
                          ) : (
                            <div className="w-10 h-10 rounded-full bg-gradient-to-br from-indigo-500 to-purple-600 text-white flex items-center justify-center font-bold text-sm shadow-sm group-hover:scale-105 transition-transform">
                              {user.name?.charAt(0) || user.email?.charAt(0) || "U"}
                            </div>
                          )}
                        </div>
                        <div>
                          <p className="font-semibold text-slate-900 dark:text-slate-100 text-sm group-hover:text-indigo-600 dark:group-hover:text-indigo-400 transition-colors">
                            {user.name || "Chưa đặt tên"}
                          </p>
                          <span className="text-[11px] text-slate-400 dark:text-slate-500 font-mono">ID: {user.id.slice(0, 8)}</span>
                        </div>
                      </div>
                    </TableCell>

                    <TableCell className="text-sm font-medium text-slate-700 dark:text-slate-300">
                      <span className="font-mono bg-slate-100/80 dark:bg-slate-800 px-2.5 py-1 rounded-lg text-slate-800 dark:text-slate-200 border border-slate-200/60 dark:border-slate-700 text-xs">
                        {user.email}
                      </span>
                    </TableCell>

                    <TableCell>{getRoleBadge(user.role)}</TableCell>

                    <TableCell className="pr-6">
                      <UserStatusDropdown userId={user.id} currentStatus={user.status} />
                    </TableCell>
                  </TableRow>
                ))
              )}
            </TableBody>
          </Table>

          {/* Modern Pagination Footer */}
          {pageData && pageData.totalPages > 0 && (
            <div className="p-5 flex flex-col sm:flex-row items-center justify-between gap-4 border-t border-slate-100 dark:border-slate-800 bg-white dark:bg-slate-900">
              <span className="text-xs text-slate-500 dark:text-slate-400 font-medium">
                Đang hiển thị trang <span className="font-bold text-slate-900 dark:text-slate-100">{pageData.number + 1}</span> / {pageData.totalPages} ({pageData.totalElements} tài khoản)
              </span>

              <div className="flex items-center gap-2">
                <Button
                  variant="outline"
                  size="sm"
                  disabled={pageData.first || page === 0}
                  onClick={() => setPage((p) => Math.max(0, p - 1))}
                  className="rounded-xl border-slate-200 dark:border-slate-700 text-xs font-semibold gap-1 hover:bg-slate-50 dark:hover:bg-slate-800 dark:text-slate-200"
                >
                  <ChevronLeft className="w-4 h-4" />
                  Trước
                </Button>

                <div className="hidden sm:flex items-center gap-1">
                  {Array.from({ length: Math.min(5, pageData.totalPages) }, (_, idx) => {
                    const pageNum = idx;
                    const isActive = pageNum === pageData.number;
                    return (
                      <button
                        key={pageNum}
                        onClick={() => setPage(pageNum)}
                        className={`w-8 h-8 rounded-xl text-xs font-bold transition-all cursor-pointer ${
                          isActive
                            ? "bg-indigo-600 text-white shadow-md shadow-indigo-600/30"
                            : "text-slate-600 dark:text-slate-400 hover:bg-slate-100 dark:hover:bg-slate-800"
                        }`}
                      >
                        {pageNum + 1}
                      </button>
                    );
                  })}
                </div>

                <Button
                  variant="outline"
                  size="sm"
                  disabled={pageData.last || page >= pageData.totalPages - 1}
                  onClick={() => setPage((p) => p + 1)}
                  className="rounded-xl border-slate-200 dark:border-slate-700 text-xs font-semibold gap-1 hover:bg-slate-50 dark:hover:bg-slate-800 dark:text-slate-200"
                >
                  Sau
                  <ChevronRight className="w-4 h-4" />
                </Button>
              </div>
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  );
}
