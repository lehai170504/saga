"use client";

import { useState } from "react";
import { useSubjects } from "@/features/academic/api/useAcademic";
import { Subject } from "@/features/academic/types";
import { CreateSubjectModal } from "@/features/academic/components/CreateSubjectModal";
import { EditSubjectModal } from "@/features/academic/components/EditSubjectModal";
import { DeleteSubjectModal } from "@/features/academic/components/DeleteSubjectModal";
import { Card, CardContent } from "@/components/ui/card";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { Button } from "@/components/ui/button";
import {
  BookOpen,
  Sparkles,
  Plus,
  Search,
  RefreshCw,
  ChevronLeft,
  ChevronRight,
  Edit2,
  Trash2,
  X,
  Book,
} from "lucide-react";

export default function SubjectsAdminPage() {
  const [page, setPage] = useState(0);
  const [search, setSearch] = useState("");
  const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
  const [editingSubject, setEditingSubject] = useState<Subject | null>(null);
  const [deletingSubject, setDeletingSubject] = useState<Subject | null>(null);

  const { data: subjectData, isLoading: loadingSubjects, refetch, isFetching } = useSubjects(page, 10, search);

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
          <h1 className="text-2xl md:text-3xl font-extrabold tracking-tight text-slate-900 dark:text-white">Quản Lý Môn Học</h1>
          <p className="text-slate-600 dark:text-slate-300 text-sm max-w-xl">Quản lý danh mục các môn học chính thức và mã môn học trong toàn bộ chương trình đào tạo.</p>
        </div>

        <div className="relative z-10 flex items-center gap-3">
          <Button
            onClick={() => setIsCreateModalOpen(true)}
            className="group relative inline-flex items-center gap-2 px-5 py-2.5 rounded-2xl bg-gradient-to-r from-indigo-600 via-purple-600 to-indigo-600 hover:from-indigo-500 hover:to-purple-500 text-white font-bold text-xs sm:text-sm shadow-lg shadow-indigo-500/25 hover:shadow-xl hover:shadow-indigo-500/40 hover:scale-[1.02] active:scale-[0.98] transition-all duration-200 border border-white/20 cursor-pointer"
          >
            <Plus className="w-4 h-4 transition-transform duration-300 group-hover:rotate-90" />
            <span>Thêm Môn Học Mới</span>
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
              placeholder="Tìm kiếm theo mã môn hoặc tên môn..."
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
            Tổng cộng: <span className="font-bold text-slate-900 dark:text-slate-100">{subjectData?.totalElements ?? 0}</span> môn học
          </div>
        </div>

        <CardContent className="p-0">
          <Table>
            <TableHeader className="bg-slate-50/70 dark:bg-slate-800/50 border-b border-slate-100 dark:border-slate-800">
              <TableRow className="hover:bg-transparent border-b border-slate-100 dark:border-slate-800">
                <TableHead className="font-bold text-slate-700 dark:text-slate-300 text-xs uppercase tracking-wider pl-6 py-4">Mã Môn Học</TableHead>
                <TableHead className="font-bold text-slate-700 dark:text-slate-300 text-xs uppercase tracking-wider py-4">Tên Môn Học</TableHead>
                <TableHead className="text-right font-bold text-slate-700 dark:text-slate-300 text-xs uppercase tracking-wider pr-6 py-4">Thao Tác</TableHead>
              </TableRow>
            </TableHeader>

            <TableBody>
              {loadingSubjects ? (
                Array.from({ length: 5 }).map((_, i) => (
                  <TableRow key={i} className="animate-pulse border-b border-slate-100 dark:border-slate-800">
                    <TableCell className="pl-6 py-4"><div className="w-24 h-5 bg-slate-200 dark:bg-slate-800 rounded-md" /></TableCell>
                    <TableCell><div className="w-48 h-5 bg-slate-200 dark:bg-slate-800 rounded-md" /></TableCell>
                    <TableCell className="pr-6 text-right"><div className="w-24 h-8 bg-slate-200 dark:bg-slate-800 rounded-xl ml-auto" /></TableCell>
                  </TableRow>
                ))
              ) : !subjectData?.content || subjectData.content.length === 0 ? (
                <TableRow>
                  <TableCell colSpan={3} className="text-center py-16 text-slate-400 dark:text-slate-500">
                    Chưa có dữ liệu môn học nào.
                  </TableCell>
                </TableRow>
              ) : (
                subjectData.content.map((sub) => {
                  const code = sub.subjectCode || sub.code || "N/A";
                  const name = sub.subjectName || sub.name || "N/A";
                  return (
                    <TableRow key={sub.id} className="hover:bg-slate-50/80 dark:hover:bg-slate-800/60 transition-all duration-150 group border-b border-slate-100/60 dark:border-slate-800/60">
                      <TableCell className="pl-6 py-4">
                        <span className="px-3 py-1 text-xs font-mono font-bold bg-indigo-50 dark:bg-indigo-950/60 text-indigo-700 dark:text-indigo-300 rounded-xl border border-indigo-200/80 dark:border-indigo-800/40">
                          {code}
                        </span>
                      </TableCell>

                      <TableCell className="py-4">
                        <div className="flex items-center gap-3">
                          <div className="w-9 h-9 rounded-xl bg-blue-50 dark:bg-blue-950/60 text-blue-600 dark:text-blue-400 flex items-center justify-center font-bold text-xs border border-blue-100 dark:border-blue-800/40 group-hover:scale-105 transition-transform shrink-0">
                            <Book className="w-4 h-4" />
                          </div>
                          <span className="font-bold text-slate-900 dark:text-slate-100 text-sm group-hover:text-indigo-600 dark:group-hover:text-indigo-400 transition-colors">
                            {name}
                          </span>
                        </div>
                      </TableCell>

                      <TableCell className="pr-6 text-right">
                        <div className="flex items-center justify-end gap-2">
                          <Button
                            variant="ghost"
                            size="icon"
                            title="Chỉnh sửa môn học"
                            onClick={() => setEditingSubject(sub)}
                            className="group p-2 rounded-xl text-indigo-600 dark:text-indigo-400 bg-indigo-50/80 dark:bg-indigo-950/50 hover:bg-indigo-600 hover:text-white dark:hover:bg-indigo-600 dark:hover:text-white border border-indigo-200/60 dark:border-indigo-800/50 shadow-2xs hover:shadow-md hover:shadow-indigo-500/20 hover:scale-[1.08] active:scale-[0.95] transition-all duration-200 cursor-pointer"
                          >
                            <Edit2 className="w-4 h-4 group-hover:-rotate-12 transition-transform duration-200" />
                          </Button>
                          <Button
                            variant="ghost"
                            size="icon"
                            title="Xóa môn học"
                            onClick={() => setDeletingSubject(sub)}
                            className="group p-2 rounded-xl text-rose-600 dark:text-rose-400 bg-rose-50/80 dark:bg-rose-950/50 hover:bg-rose-600 hover:text-white dark:hover:bg-rose-600 dark:hover:text-white border border-rose-200/60 dark:border-rose-800/50 shadow-2xs hover:shadow-md hover:shadow-rose-500/20 hover:scale-[1.08] active:scale-[0.95] transition-all duration-200 cursor-pointer"
                          >
                            <Trash2 className="w-4 h-4 group-hover:scale-110 transition-transform duration-200" />
                          </Button>
                        </div>
                      </TableCell>
                    </TableRow>
                  );
                })
              )}
            </TableBody>
          </Table>

          {/* Pagination */}
          {subjectData && subjectData.totalPages > 0 && (
            <div className="p-5 flex items-center justify-between border-t border-slate-100 dark:border-slate-800 bg-white dark:bg-slate-900">
              <span className="text-xs text-slate-500 dark:text-slate-400 font-medium">
                Trang <span className="font-bold text-slate-900 dark:text-slate-100">{subjectData.pageable.pageNumber + 1}</span> / {subjectData.totalPages}
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
                  disabled={page >= subjectData.totalPages - 1}
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

      <CreateSubjectModal isOpen={isCreateModalOpen} onOpenChange={setIsCreateModalOpen} />
      <EditSubjectModal subject={editingSubject} isOpen={!!editingSubject} onOpenChange={(open) => !open && setEditingSubject(null)} />
      <DeleteSubjectModal subject={deletingSubject} isOpen={!!deletingSubject} onOpenChange={(open) => !open && setDeletingSubject(null)} />
    </div>
  );
}
