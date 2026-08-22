"use client";

import { useState } from "react";
import { useClasses } from "@/features/academic/api/useAcademic";
import { AcademicClass } from "@/features/academic/types";
import { CreateClassModal } from "@/features/academic/components/CreateClassModal";
import { EditClassModal } from "@/features/academic/components/EditClassModal";
import { DeleteClassModal } from "@/features/academic/components/DeleteClassModal";
import { Card, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import {
  Sparkles,
  School,
  Plus,
  Search,
  ChevronLeft,
  ChevronRight,
  X,
  Edit2,
  Trash2,
  Building2,
} from "lucide-react";

export default function ClassesAdminPage() {
  const [page, setPage] = useState(0);
  const [search, setSearch] = useState("");
  const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
  const [editingClass, setEditingClass] = useState<AcademicClass | null>(null);
  const [deletingClass, setDeletingClass] = useState<AcademicClass | null>(null);

  const { data: classData, isLoading: loadingClasses } = useClasses(page, 15, search);

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
          <h1 className="text-2xl md:text-3xl font-extrabold tracking-tight text-slate-900 dark:text-white">Quản Lý Lớp Học</h1>
          <p className="text-slate-600 dark:text-slate-300 text-sm max-w-xl">Cấu hình danh sách các lớp học chính thức và mã lớp học trong toàn bộ hệ thống đào tạo.</p>
        </div>

        <div className="relative z-10 flex items-center gap-3">
          <Button
            onClick={() => setIsCreateModalOpen(true)}
            className="group relative inline-flex items-center gap-2 px-5 py-2.5 rounded-2xl bg-gradient-to-r from-indigo-600 via-purple-600 to-indigo-600 hover:from-indigo-500 hover:to-purple-500 text-white font-bold text-xs sm:text-sm shadow-lg shadow-indigo-500/25 hover:shadow-xl hover:shadow-indigo-500/40 hover:scale-[1.02] active:scale-[0.98] transition-all duration-200 border border-white/20 cursor-pointer"
          >
            <Plus className="w-4 h-4 transition-transform duration-300 group-hover:rotate-90" />
            <span>Thêm Lớp Học Mới</span>
          </Button>
        </div>
      </div>

      {/* Main Container */}
      <Card className="border border-slate-200/80 dark:border-slate-800 shadow-md shadow-slate-200/40 dark:shadow-none rounded-3xl overflow-hidden bg-white dark:bg-slate-900 transition-colors">
        {/* Toolbar */}
        <div className="p-5 md:p-6 bg-slate-50/40 dark:bg-slate-900/60 border-b border-slate-100 dark:border-slate-800 flex flex-col md:flex-row md:items-center justify-between gap-4">
          <div className="relative flex-1 max-w-md">
            <Search className="w-4 h-4 absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-400" />
            <input
              type="text"
              placeholder="Tìm kiếm theo mã lớp học..."
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
            Tổng cộng: <span className="font-bold text-slate-900 dark:text-slate-100">{classData?.totalElements ?? 0}</span> lớp học
          </div>
        </div>

        <CardContent className="p-6 md:p-8">
          {loadingClasses ? (
            <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-4">
              {Array.from({ length: 10 }).map((_, i) => (
                <div
                  key={i}
                  className="animate-pulse p-4 rounded-2xl bg-slate-100 dark:bg-slate-800/60 border border-slate-200/50 dark:border-slate-800 flex items-center gap-3 h-20"
                >
                  <div className="w-10 h-10 rounded-xl bg-slate-200 dark:bg-slate-700 shrink-0" />
                  <div className="space-y-1.5 flex-1">
                    <div className="w-20 h-4 bg-slate-200 dark:bg-slate-700 rounded-md" />
                    <div className="w-12 h-3 bg-slate-200/60 dark:bg-slate-800 rounded-md" />
                  </div>
                </div>
              ))}
            </div>
          ) : !classData?.content || classData.content.length === 0 ? (
            <div className="text-center py-16 space-y-3">
              <div className="w-14 h-14 rounded-3xl bg-slate-100 dark:bg-slate-800 flex items-center justify-center mx-auto text-slate-400 dark:text-slate-500 border border-slate-200 dark:border-slate-700">
                <School className="w-7 h-7" />
              </div>
              <p className="text-base font-bold text-slate-800 dark:text-slate-200">Chưa có dữ liệu lớp học nào</p>
              <p className="text-xs text-slate-400 dark:text-slate-500 max-w-sm mx-auto">
                Nhấn nút "Thêm Lớp Học Mới" ở góc trên để khởi tạo danh sách mã lớp học đầu tiên.
              </p>
            </div>
          ) : (
            <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-4">
              {classData.content.map((cls) => (
                <div
                  key={cls.id}
                  className="group relative flex items-center justify-between p-4 rounded-2xl bg-white dark:bg-slate-900 border border-slate-200/80 dark:border-slate-800/80 hover:border-indigo-300 dark:hover:border-indigo-800/80 shadow-xs hover:shadow-lg hover:shadow-indigo-500/10 hover:-translate-y-0.5 transition-all duration-200"
                >
                  <div className="flex items-center gap-3.5 min-w-0">
                    <div className="w-10 h-10 rounded-xl bg-indigo-50 dark:bg-indigo-950/60 text-indigo-600 dark:text-indigo-400 flex items-center justify-center font-bold text-xs border border-indigo-100 dark:border-indigo-800/40 group-hover:scale-105 group-hover:bg-indigo-600 group-hover:text-white transition-all shrink-0">
                      <School className="w-5 h-5" />
                    </div>
                    <div className="truncate">
                      <p className="font-extrabold font-mono text-slate-900 dark:text-white text-base tracking-wide group-hover:text-indigo-600 dark:group-hover:text-indigo-400 transition-colors truncate">
                        {cls.classCode}
                      </p>
                      <span className="text-[10px] font-bold text-slate-400 dark:text-slate-500 uppercase tracking-wider block">
                        Academic Class
                      </span>
                    </div>
                  </div>

                  {/* Actions: Edit & Delete Icon Buttons */}
                  <div className="flex items-center gap-1 shrink-0">
                    <button
                      onClick={() => setEditingClass(cls)}
                      title="Chỉnh sửa mã lớp"
                      className="p-1.5 rounded-xl text-slate-400 hover:text-indigo-600 dark:hover:text-indigo-400 hover:bg-indigo-50 dark:hover:bg-indigo-950/60 transition-colors cursor-pointer"
                    >
                      <Edit2 className="w-4 h-4" />
                    </button>
                    <button
                      onClick={() => setDeletingClass(cls)}
                      title="Xóa lớp học"
                      className="p-1.5 rounded-xl text-slate-400 hover:text-rose-600 dark:hover:text-rose-400 hover:bg-rose-50 dark:hover:bg-rose-950/60 transition-colors cursor-pointer"
                    >
                      <Trash2 className="w-4 h-4" />
                    </button>
                  </div>
                </div>
              ))}
            </div>
          )}

          {/* Pagination */}
          {classData && classData.totalPages > 0 && (
            <div className="mt-8 pt-5 flex items-center justify-between border-t border-slate-100 dark:border-slate-800">
              <span className="text-xs text-slate-500 dark:text-slate-400 font-medium">
                Trang <span className="font-bold text-slate-900 dark:text-slate-100">{classData.pageable.pageNumber + 1}</span> / {classData.totalPages}
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
                  disabled={page >= classData.totalPages - 1}
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

      <CreateClassModal isOpen={isCreateModalOpen} onOpenChange={setIsCreateModalOpen} />
      <EditClassModal cls={editingClass} isOpen={!!editingClass} onOpenChange={(open) => !open && setEditingClass(null)} />
      <DeleteClassModal cls={deletingClass} isOpen={!!deletingClass} onOpenChange={(open) => !open && setDeletingClass(null)} />
    </div>
  );
}
