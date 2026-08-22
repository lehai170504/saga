"use client";

import { useState } from "react";
import { useCourses, useDownloadRosterTemplate } from "@/features/academic/api/useAcademic";
import { Course } from "@/features/academic/types";
import { CreateCourseModal } from "@/features/academic/components/CreateCourseModal";
import { AddStudentModal } from "@/features/academic/components/AddStudentModal";
import { ImportRosterModal } from "@/features/academic/components/ImportRosterModal";
import { Card, CardContent } from "@/components/ui/card";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { Button } from "@/components/ui/button";
import {
  Sparkles,
  Plus,
  Search,
  ChevronLeft,
  ChevronRight,
  X,
  BookOpen,
  Calendar,
  UserCheck,
  Loader2,
  FileSpreadsheet,
  UserPlus,
  FileUp,
} from "lucide-react";
import { toast } from "sonner";

export default function CoursesAdminPage() {
  const [page, setPage] = useState(0);
  const [search, setSearch] = useState("");
  const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
  const [downloadingId, setDownloadingId] = useState<string | null>(null);
  const [addingStudentCourse, setAddingStudentCourse] = useState<Course | null>(null);
  const [importingRosterCourse, setImportingRosterCourse] = useState<Course | null>(null);

  const { data: courseData, isLoading: loadingCourses } = useCourses(page, 10, search);
  const { mutateAsync: downloadTemplate } = useDownloadRosterTemplate();

  const handleDownload = async (courseId: string, courseName: string) => {
    try {
      setDownloadingId(courseId);
      await downloadTemplate(courseId);
      toast.success(`Đã tải mẫu Excel cho khóa học: ${courseName}`);
    } catch (err: any) {
      const msg = err?.response?.data?.message || "Đã xảy ra lỗi khi tải xuống mẫu danh sách.";
      toast.error(msg);
    } finally {
      setDownloadingId(null);
    }
  };

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
          <h1 className="text-2xl md:text-3xl font-extrabold tracking-tight text-slate-900 dark:text-white">Quản Lý Khóa Học</h1>
          <p className="text-slate-600 dark:text-slate-300 text-sm max-w-xl">Quản lý danh sách các khóa học được phân công theo từng học kỳ, môn học, lớp và giảng viên phụ trách.</p>
        </div>

        <div className="relative z-10 flex items-center gap-3">
          <Button
            onClick={() => setIsCreateModalOpen(true)}
            className="group relative inline-flex items-center gap-2 px-5 py-2.5 rounded-2xl bg-gradient-to-r from-indigo-600 via-purple-600 to-indigo-600 hover:from-indigo-500 hover:to-purple-500 text-white font-bold text-xs sm:text-sm shadow-lg shadow-indigo-500/25 hover:shadow-xl hover:shadow-indigo-500/40 hover:scale-[1.02] active:scale-[0.98] transition-all duration-200 border border-white/20 cursor-pointer"
          >
            <Plus className="w-4 h-4 transition-transform duration-300 group-hover:rotate-90" />
            <span>Thêm Khóa Học Mới</span>
          </Button>
        </div>
      </div>

      {/* Main Data Table */}
      <Card className="border border-slate-200/80 dark:border-slate-800 shadow-md shadow-slate-200/40 dark:shadow-none rounded-3xl overflow-hidden bg-white dark:bg-slate-900 transition-colors">
        {/* Toolbar */}
        <div className="p-5 md:p-6 bg-slate-50/40 dark:bg-slate-900/60 border-b border-slate-100 dark:border-slate-800 flex flex-col md:flex-row md:items-center justify-between gap-4">
          <div className="relative flex-1 max-w-md">
            <Search className="w-4 h-4 absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-400" />
            <input
              type="text"
              placeholder="Tìm kiếm khóa học, môn học hoặc giảng viên..."
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

          <div className="text-xs font-semibold text-slate-500 dark:text-slate-400">
            Tổng cộng: <span className="font-bold text-slate-900 dark:text-slate-100">{courseData?.totalElements ?? 0}</span> khóa học
          </div>
        </div>

        <CardContent className="p-0">
          <Table>
            <TableHeader className="bg-slate-50/70 dark:bg-slate-800/50 border-b border-slate-100 dark:border-slate-800">
              <TableRow className="hover:bg-transparent border-b border-slate-100 dark:border-slate-800">
                <TableHead className="font-bold text-slate-700 dark:text-slate-300 text-xs uppercase tracking-wider pl-6 py-4">Tên Môn Học</TableHead>
                <TableHead className="font-bold text-slate-700 dark:text-slate-300 text-xs uppercase tracking-wider py-4">Mã Lớp Học</TableHead>
                <TableHead className="font-bold text-slate-700 dark:text-slate-300 text-xs uppercase tracking-wider py-4">Học Kỳ</TableHead>
                <TableHead className="font-bold text-slate-700 dark:text-slate-300 text-xs uppercase tracking-wider py-4">Giảng Viên Phụ Trách</TableHead>
                <TableHead className="font-bold text-slate-700 dark:text-slate-300 text-xs uppercase tracking-wider pr-6 py-4 text-right">Thao Tác</TableHead>
              </TableRow>
            </TableHeader>

            <TableBody>
              {loadingCourses ? (
                Array.from({ length: 5 }).map((_, i) => (
                  <TableRow key={i} className="animate-pulse border-b border-slate-100 dark:border-slate-800">
                    <TableCell className="pl-6 py-4"><div className="w-44 h-5 bg-slate-200 dark:bg-slate-800 rounded-md" /></TableCell>
                    <TableCell><div className="w-24 h-5 bg-slate-200 dark:bg-slate-800 rounded-md" /></TableCell>
                    <TableCell><div className="w-28 h-5 bg-slate-200 dark:bg-slate-800 rounded-md" /></TableCell>
                    <TableCell><div className="w-36 h-5 bg-slate-200 dark:bg-slate-800 rounded-md" /></TableCell>
                    <TableCell className="pr-6 text-right"><div className="w-28 h-9 bg-slate-200 dark:bg-slate-800 rounded-xl ml-auto" /></TableCell>
                  </TableRow>
                ))
              ) : !courseData?.content || courseData.content.length === 0 ? (
                <TableRow>
                  <TableCell colSpan={5} className="text-center py-16 text-slate-400 dark:text-slate-500">
                    Chưa có dữ liệu khóa học nào.
                  </TableCell>
                </TableRow>
              ) : (
                courseData.content.map((crs) => {
                  const isDownloading = downloadingId === crs.id;
                  const subjectTitle = crs.subjectName || "Khóa học";

                  return (
                    <TableRow key={crs.id} className="hover:bg-slate-50/80 dark:hover:bg-slate-800/60 transition-all duration-150 group border-b border-slate-100/60 dark:border-slate-800/60">
                      <TableCell className="pl-6 py-4">
                        <div className="flex items-center gap-3">
                          <div className="w-9 h-9 rounded-xl bg-indigo-50 dark:bg-indigo-950/60 text-indigo-600 dark:text-indigo-400 flex items-center justify-center font-bold text-xs border border-indigo-100 dark:border-indigo-800/40 group-hover:scale-105 transition-transform shrink-0">
                            <BookOpen className="w-4 h-4" />
                          </div>
                          <span className="font-bold text-slate-900 dark:text-slate-100 text-sm group-hover:text-indigo-600 dark:group-hover:text-indigo-400 transition-colors">
                            {subjectTitle}
                          </span>
                        </div>
                      </TableCell>

                      <TableCell className="py-4">
                        <span className="px-3 py-1 text-xs font-mono font-bold bg-indigo-50 dark:bg-indigo-950/60 text-indigo-700 dark:text-indigo-300 rounded-xl border border-indigo-200/80 dark:border-indigo-800/40">
                          {crs.classCode || "N/A"}
                        </span>
                      </TableCell>

                      <TableCell className="py-4">
                        <span className="inline-flex items-center gap-1.5 px-3 py-1 text-xs font-semibold bg-slate-100 dark:bg-slate-800 text-slate-700 dark:text-slate-300 rounded-full border border-slate-200 dark:border-slate-700">
                          <Calendar className="w-3.5 h-3.5 text-slate-400" />
                          {crs.semesterName || "N/A"}
                        </span>
                      </TableCell>

                      <TableCell className="py-4">
                        <div className="flex items-center gap-2">
                          <div className="w-7 h-7 rounded-full bg-blue-50 dark:bg-blue-950/60 text-blue-600 dark:text-blue-400 flex items-center justify-center font-bold text-xs border border-blue-100 dark:border-blue-800/40 shrink-0">
                            <UserCheck className="w-3.5 h-3.5" />
                          </div>
                          <span className="text-sm font-semibold text-slate-800 dark:text-slate-200">
                            {crs.instructorName || "Chưa phân công"}
                          </span>
                        </div>
                      </TableCell>

                      <TableCell className="pr-6 py-4 text-right">
                        <div className="inline-flex items-center gap-1.5">
                          <button
                            onClick={() => setAddingStudentCourse(crs)}
                            title="Thêm sinh viên thủ công"
                            className="inline-flex items-center justify-center p-2 rounded-xl text-indigo-600 dark:text-indigo-400 bg-indigo-50 dark:bg-indigo-950/60 hover:bg-indigo-100 dark:hover:bg-indigo-900/60 border border-indigo-200 dark:border-indigo-800/50 shadow-2xs hover:scale-105 active:scale-95 transition-all duration-150 cursor-pointer"
                          >
                            <UserPlus className="w-4 h-4" />
                          </button>

                          <button
                            onClick={() => setImportingRosterCourse(crs)}
                            title="Import Danh Sách Sinh Viên (Excel)"
                            className="inline-flex items-center justify-center p-2 rounded-xl text-teal-600 dark:text-teal-400 bg-teal-50 dark:bg-teal-950/60 hover:bg-teal-100 dark:hover:bg-teal-900/60 border border-teal-200 dark:border-teal-800/50 shadow-2xs hover:scale-105 active:scale-95 transition-all duration-150 cursor-pointer"
                          >
                            <FileUp className="w-4 h-4" />
                          </button>

                          <button
                            onClick={() => handleDownload(crs.id, subjectTitle)}
                            disabled={isDownloading}
                            title="Tải Mẫu Danh Sách Sinh Viên (Excel)"
                            className="inline-flex items-center justify-center p-2 rounded-xl text-emerald-600 dark:text-emerald-400 bg-emerald-50 dark:bg-emerald-950/60 hover:bg-emerald-100 dark:hover:bg-emerald-900/60 border border-emerald-200 dark:border-emerald-800/50 shadow-2xs hover:scale-105 active:scale-95 transition-all duration-150 cursor-pointer disabled:opacity-50"
                          >
                            {isDownloading ? (
                              <Loader2 className="w-4 h-4 animate-spin text-emerald-600 dark:text-emerald-400" />
                            ) : (
                              <FileSpreadsheet className="w-4 h-4" />
                            )}
                          </button>
                        </div>
                      </TableCell>
                    </TableRow>
                  );
                })
              )}
            </TableBody>
          </Table>

          {/* Pagination */}
          {courseData && courseData.totalPages > 0 && (
            <div className="p-5 flex items-center justify-between border-t border-slate-100 dark:border-slate-800 bg-white dark:bg-slate-900">
              <span className="text-xs text-slate-500 dark:text-slate-400 font-medium">
                Trang <span className="font-bold text-slate-900 dark:text-slate-100">{courseData.pageable.pageNumber + 1}</span> / {courseData.totalPages}
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
                  disabled={page >= courseData.totalPages - 1}
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

      <CreateCourseModal isOpen={isCreateModalOpen} onOpenChange={setIsCreateModalOpen} />
      <AddStudentModal course={addingStudentCourse} isOpen={!!addingStudentCourse} onOpenChange={(open) => !open && setAddingStudentCourse(null)} />
      <ImportRosterModal course={importingRosterCourse} isOpen={!!importingRosterCourse} onOpenChange={(open) => !open && setImportingRosterCourse(null)} />
    </div>
  );
}
