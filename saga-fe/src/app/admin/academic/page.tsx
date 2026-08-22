"use client";

import { useState } from "react";
import { useSemesters } from "@/features/academic/api/useAcademic";
import { CreateSemesterModal } from "@/features/academic/components/CreateSemesterModal";
import { Card, CardContent } from "@/components/ui/card";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { Button } from "@/components/ui/button";
import {
  GraduationCap,
  Calendar,
  Plus,
  Sparkles,
  ChevronLeft,
  ChevronRight,
  RefreshCw,
  Clock,
  Edit2,
  BookOpen,
  CheckCircle2,
  XCircle,
} from "lucide-react";

export default function AcademicAdminPage() {
  const [page, setPage] = useState(0);
  const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
  const { data: semesterData, isLoading: loadingSemesters, refetch } = useSemesters(page, 10);

  return (
    <div className="p-6 md:p-8 w-full space-y-8 bg-slate-50/50 dark:bg-slate-950 min-h-screen transition-colors duration-300">
      {/* Top Banner */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 bg-gradient-to-r from-indigo-50/90 via-blue-50/80 to-indigo-50/90 dark:from-slate-950 dark:via-indigo-950/90 dark:to-slate-950 p-6 md:p-8 rounded-3xl text-slate-900 dark:text-white shadow-md shadow-indigo-500/5 dark:shadow-xl dark:shadow-slate-900/10 border border-indigo-100/80 dark:border-slate-800/80 relative overflow-hidden transition-colors duration-300">
        <div className="absolute top-0 right-0 w-72 h-72 bg-indigo-500/10 dark:bg-indigo-500/20 rounded-full blur-3xl pointer-events-none" />
        <div className="absolute bottom-0 left-1/3 w-60 h-60 bg-blue-500/10 dark:bg-blue-500/15 rounded-full blur-3xl pointer-events-none" />

        <div className="relative z-10 space-y-1">
          <div className="flex items-center gap-2">
            <span className="px-3 py-0.5 text-xs font-bold uppercase tracking-widest bg-indigo-100 dark:bg-indigo-500/30 text-indigo-700 dark:text-indigo-300 rounded-full border border-indigo-200 dark:border-indigo-400/30 flex items-center gap-1.5 w-fit">
              <Sparkles className="w-3.5 h-3.5" />
              Academic Master Data
            </span>
          </div>
          <h1 className="text-2xl md:text-3xl font-extrabold tracking-tight text-slate-900 dark:text-white">Quản Lý Học Kỳ & Đào Tạo</h1>
          <p className="text-slate-600 dark:text-slate-300 text-sm max-w-xl">Cấu hình thời gian học kỳ, môn học chính thức và dữ liệu đào tạo cho toàn bộ các lớp.</p>
        </div>

        <div className="relative z-10 flex items-center gap-3">
          <Button
            onClick={() => setIsCreateModalOpen(true)}
            className="group relative inline-flex items-center gap-2 px-5 py-2.5 rounded-2xl bg-gradient-to-r from-indigo-600 via-purple-600 to-indigo-600 hover:from-indigo-500 hover:to-purple-500 text-white font-bold text-xs sm:text-sm shadow-lg shadow-indigo-500/25 hover:shadow-xl hover:shadow-indigo-500/40 hover:scale-[1.02] active:scale-[0.98] transition-all duration-200 border border-white/20 cursor-pointer"
          >
            <Plus className="w-4 h-4 transition-transform duration-300 group-hover:rotate-90" />
            <span>Thêm Học Kỳ Mới</span>
          </Button>
        </div>
      </div>

      {/* Main Data Table */}
      <Card className="border border-slate-200/80 dark:border-slate-800 shadow-md shadow-slate-200/40 dark:shadow-none rounded-3xl overflow-hidden bg-white dark:bg-slate-900 transition-colors">
        <div className="p-5 md:p-6 bg-slate-50/40 dark:bg-slate-900/60 border-b border-slate-100 dark:border-slate-800 flex items-center justify-between">
          <h2 className="text-base font-bold text-slate-800 dark:text-slate-200 flex items-center gap-2">
            <Calendar className="w-5 h-5 text-indigo-600 dark:text-indigo-400" />
            Danh Sách Các Học Kỳ ({semesterData?.totalElements ?? 0})
          </h2>
        </div>

        <CardContent className="p-0">
          <Table>
            <TableHeader className="bg-slate-50/70 dark:bg-slate-800/50 border-b border-slate-100 dark:border-slate-800">
              <TableRow className="hover:bg-transparent border-b border-slate-100 dark:border-slate-800">
                <TableHead className="font-bold text-slate-700 dark:text-slate-300 text-xs uppercase tracking-wider pl-6 py-4">Mã Học Kỳ</TableHead>
                <TableHead className="font-bold text-slate-700 dark:text-slate-300 text-xs uppercase tracking-wider py-4">Tên Học Kỳ</TableHead>
                <TableHead className="font-bold text-slate-700 dark:text-slate-300 text-xs uppercase tracking-wider py-4">Bắt Đầu</TableHead>
                <TableHead className="font-bold text-slate-700 dark:text-slate-300 text-xs uppercase tracking-wider py-4">Kết Thúc</TableHead>
                <TableHead className="font-bold text-slate-700 dark:text-slate-300 text-xs uppercase tracking-wider pr-6 py-4">Trạng Thái</TableHead>
              </TableRow>
            </TableHeader>

            <TableBody>
              {loadingSemesters ? (
                Array.from({ length: 4 }).map((_, i) => (
                  <TableRow key={i} className="animate-pulse border-b border-slate-100 dark:border-slate-800">
                    <TableCell className="pl-6 py-4"><div className="w-20 h-4 bg-slate-200 dark:bg-slate-800 rounded-md" /></TableCell>
                    <TableCell><div className="w-32 h-4 bg-slate-200 dark:bg-slate-800 rounded-md" /></TableCell>
                    <TableCell><div className="w-24 h-4 bg-slate-200 dark:bg-slate-800 rounded-md" /></TableCell>
                    <TableCell><div className="w-24 h-4 bg-slate-200 dark:bg-slate-800 rounded-md" /></TableCell>
                    <TableCell className="pr-6"><div className="w-20 h-6 bg-slate-200 dark:bg-slate-800 rounded-full" /></TableCell>
                  </TableRow>
                ))
              ) : !semesterData?.content || semesterData.content.length === 0 ? (
                <TableRow>
                  <TableCell colSpan={5} className="text-center py-16 text-slate-400 dark:text-slate-500">
                    Chưa có dữ liệu học kỳ nào.
                  </TableCell>
                </TableRow>
              ) : (
                semesterData.content.map((sem) => (
                  <TableRow key={sem.id} className="hover:bg-slate-50/80 dark:hover:bg-slate-800/60 transition-all duration-150 group border-b border-slate-100/60 dark:border-slate-800/60">
                    <TableCell className="pl-6 py-4">
                      <span className="px-2.5 py-1 text-xs font-mono font-bold bg-slate-100 dark:bg-slate-800 text-slate-700 dark:text-slate-300 rounded-lg border border-slate-200 dark:border-slate-700">
                        {sem.code || "N/A"}
                      </span>
                    </TableCell>

                    <TableCell className="py-4">
                      <div className="flex items-center gap-3">
                        <div className="w-9 h-9 rounded-xl bg-indigo-50 dark:bg-indigo-950/60 text-indigo-600 dark:text-indigo-400 flex items-center justify-center font-bold text-xs border border-indigo-100 dark:border-indigo-800/40 group-hover:scale-105 transition-transform shrink-0">
                          {sem.code?.substring(0, 2) || sem.name?.substring(0, 2) || "HK"}
                        </div>
                        <span className="font-bold text-slate-900 dark:text-slate-100 text-sm group-hover:text-indigo-600 dark:group-hover:text-indigo-400 transition-colors">
                          {sem.name}
                        </span>
                      </div>
                    </TableCell>

                    <TableCell className="text-sm font-medium text-slate-600 dark:text-slate-300 font-mono">
                      {sem.startDate || "---"}
                    </TableCell>

                    <TableCell className="text-sm font-medium text-slate-600 dark:text-slate-300 font-mono">
                      {sem.endDate || "---"}
                    </TableCell>

                    <TableCell className="pr-6">
                      {sem.active ? (
                        <span className="inline-flex items-center gap-1.5 px-3 py-1 text-xs font-semibold bg-emerald-50 dark:bg-emerald-950/40 text-emerald-700 dark:text-emerald-300 rounded-full border border-emerald-200/60 dark:border-emerald-800/40">
                          <CheckCircle2 className="w-3.5 h-3.5 text-emerald-500" />
                          Đang hoạt động
                        </span>
                      ) : (
                        <span className="inline-flex items-center gap-1.5 px-3 py-1 text-xs font-semibold bg-slate-100 dark:bg-slate-800 text-slate-600 dark:text-slate-400 rounded-full border border-slate-200 dark:border-slate-700">
                          <XCircle className="w-3.5 h-3.5 text-slate-400" />
                          Đã đóng
                        </span>
                      )}
                    </TableCell>
                  </TableRow>
                ))
              )}
            </TableBody>
          </Table>

          {/* Pagination */}
          {semesterData && semesterData.totalPages > 0 && (
            <div className="p-5 flex items-center justify-between border-t border-slate-100 dark:border-slate-800 bg-white dark:bg-slate-900">
              <span className="text-xs text-slate-500 dark:text-slate-400 font-medium">
                Trang <span className="font-bold text-slate-900 dark:text-slate-100">{semesterData.pageable.pageNumber + 1}</span> / {semesterData.totalPages}
              </span>

              <div className="flex items-center gap-2">
                <Button
                  variant="outline"
                  size="sm"
                  disabled={page === 0}
                  onClick={() => setPage((p) => Math.max(0, p - 1))}
                  className="rounded-xl border-slate-200 dark:border-slate-700 text-xs font-semibold gap-1 hover:bg-slate-50 dark:hover:bg-slate-800 dark:text-slate-200 cursor-pointer"
                >
                  <ChevronLeft className="w-4 h-4" />
                  Trước
                </Button>
                <Button
                  variant="outline"
                  size="sm"
                  disabled={page >= semesterData.totalPages - 1}
                  onClick={() => setPage((p) => p + 1)}
                  className="rounded-xl border-slate-200 dark:border-slate-700 text-xs font-semibold gap-1 hover:bg-slate-50 dark:hover:bg-slate-800 dark:text-slate-200 cursor-pointer"
                >
                  Sau
                  <ChevronRight className="w-4 h-4" />
                </Button>
              </div>
            </div>
          )}
        </CardContent>
      </Card>

      <CreateSemesterModal isOpen={isCreateModalOpen} onOpenChange={setIsCreateModalOpen} />
    </div>
  );
}
