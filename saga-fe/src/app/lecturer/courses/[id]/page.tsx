"use client";

import { useParams } from "next/navigation";
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { useImportRoster } from "@/features/academic/api/useLecturerAcademic";
import { toast } from "sonner";
import {
  Upload,
  Download,
  Users,
  Sparkles,
  FileSpreadsheet,
  BookOpen,
} from "lucide-react";

export default function LecturerCoursePage() {
  const params = useParams();
  const courseId = params.id as string;
  const { mutate: importRoster, isPending } = useImportRoster();

  const handleFileUpload = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files[0]) {
      const file = e.target.files[0];
      if (file.size > 5 * 1024 * 1024) {
        toast.error("File quá lớn (giới hạn tối đa 5MB)");
        return;
      }
      importRoster(
        { courseId, file },
        {
          onSuccess: () => toast.success("Đã nhập Roster thành công!"),
          onError: (err: any) =>
            toast.error(err.response?.data?.message || "Lỗi khi tải danh sách danh sách Roster"),
        }
      );
    }
  };

  return (
    <div className="p-6 md:p-8 w-full space-y-8 bg-slate-50/50 dark:bg-slate-950 min-h-screen transition-colors duration-300">
      {/* Top Banner */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 bg-gradient-to-r from-blue-50/90 via-indigo-50/80 to-blue-50/90 dark:from-slate-950 dark:via-blue-950/90 dark:to-slate-950 p-6 md:p-8 rounded-3xl text-slate-900 dark:text-white shadow-md shadow-blue-500/5 dark:shadow-xl dark:shadow-slate-900/10 border border-blue-100/80 dark:border-slate-800/80 relative overflow-hidden transition-colors duration-300">
        <div className="absolute top-0 right-0 w-72 h-72 bg-blue-500/10 dark:bg-blue-500/20 rounded-full blur-3xl pointer-events-none" />
        <div className="absolute bottom-0 left-1/3 w-60 h-60 bg-indigo-500/10 dark:bg-indigo-500/15 rounded-full blur-3xl pointer-events-none" />

        <div className="relative z-10 space-y-1">
          <div className="flex items-center gap-2">
            <span className="px-3 py-0.5 text-xs font-bold uppercase tracking-widest bg-blue-100 dark:bg-blue-500/30 text-blue-700 dark:text-blue-300 rounded-full border border-blue-200 dark:border-blue-400/30 flex items-center gap-1.5 w-fit">
              <Sparkles className="w-3.5 h-3.5" />
              Lecturer Portal
            </span>
          </div>
          <h1 className="text-2xl md:text-3xl font-extrabold tracking-tight text-slate-900 dark:text-white">Quản Lý Lớp Học & Nhóm Sinh Viên</h1>
          <p className="text-slate-600 dark:text-slate-300 text-sm max-w-xl">Nhập danh sách sinh viên Roster (.xlsx) và phân chia nhóm đề tài tự động cho khóa học.</p>
        </div>

        <div className="relative z-10">
          <span className="px-4 py-2 rounded-2xl bg-white dark:bg-white/10 backdrop-blur-md border border-slate-200 dark:border-white/20 text-xs font-bold text-slate-700 dark:text-blue-200 shadow-xs flex items-center gap-2">
            <BookOpen className="w-4 h-4 text-blue-600 dark:text-blue-300" />
            Mã khóa học: {courseId}
          </span>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* Import Roster Card */}
        <Card className="col-span-1 border border-slate-200/80 dark:border-slate-800 shadow-md shadow-blue-500/5 rounded-3xl bg-gradient-to-br from-blue-600 to-indigo-700 dark:from-blue-700 dark:to-indigo-900 text-white overflow-hidden relative">
          <div className="absolute top-0 right-0 w-40 h-40 bg-white/10 rounded-full blur-2xl pointer-events-none" />

          <CardHeader className="relative z-10 pb-2">
            <div className="w-12 h-12 rounded-2xl bg-white/10 backdrop-blur-md border border-white/20 flex items-center justify-center mb-2 text-white">
              <FileSpreadsheet className="w-6 h-6" />
            </div>
            <CardTitle className="text-xl text-white font-extrabold">Tải Lên Roster (.xlsx)</CardTitle>
            <CardDescription className="text-blue-100 text-xs">
              Upload file danh sách sinh viên để hệ thống tự tạo tài khoản và chia nhóm.
            </CardDescription>
          </CardHeader>

          <CardContent className="relative z-10 space-y-4 pt-2">
            {/* Drag & Drop Box */}
            <div className="border-2 border-dashed border-white/30 hover:border-white/60 rounded-2xl p-6 text-center bg-white/10 hover:bg-white/15 transition-all relative group cursor-pointer">
              <input
                type="file"
                accept=".xlsx"
                onChange={handleFileUpload}
                className="absolute inset-0 w-full h-full opacity-0 cursor-pointer z-20"
                disabled={isPending}
              />
              <div className="space-y-3 pointer-events-none">
                <div className="w-12 h-12 rounded-2xl bg-white/20 mx-auto flex items-center justify-center group-hover:scale-110 transition-transform">
                  <Upload className="h-6 w-6 text-white" />
                </div>
                <div>
                  <p className="text-sm font-bold text-white">
                    {isPending ? "Đang tải file lên..." : "Kéo thả hoặc Chọn file Excel"}
                  </p>
                  <p className="text-xs text-blue-200 mt-1">Định dạng hỗ trợ .xlsx (Tối đa 5MB)</p>
                </div>
              </div>
            </div>

            {/* Template Download Button */}
            <Button
              variant="secondary"
              className="w-full bg-white text-blue-700 hover:bg-blue-50 rounded-2xl py-2.5 font-bold text-xs gap-2 shadow-sm transition-all cursor-pointer"
              onClick={() =>
                (window.location.href = `/api/v1/academic/courses/${courseId}/roster/template`)
              }
            >
              <Download className="w-4 h-4 text-blue-600" />
              Tải File Mẫu Excel (.xlsx)
            </Button>
          </CardContent>
        </Card>

        {/* Team Groupings Overview Card */}
        <Card className="lg:col-span-2 border border-slate-200/80 dark:border-slate-800 shadow-md shadow-slate-200/40 dark:shadow-none rounded-3xl bg-white dark:bg-slate-900 overflow-hidden transition-colors">
          <CardHeader className="bg-slate-50/50 dark:bg-slate-900/60 border-b border-slate-100 dark:border-slate-800 p-6 flex flex-row items-center justify-between">
            <div>
              <CardTitle className="text-lg font-bold text-slate-800 dark:text-slate-200 flex items-center gap-2">
                <Users className="w-5 h-5 text-blue-600 dark:text-blue-400" />
                Danh Sách Nhóm Sinh Viên
              </CardTitle>
              <CardDescription className="text-xs text-slate-500 dark:text-slate-400 mt-0.5">
                Các nhóm làm đề tài đã được sắp xếp trong khóa học.
              </CardDescription>
            </div>
            <span className="px-3 py-1 bg-blue-50 dark:bg-blue-950/60 text-blue-700 dark:text-blue-300 rounded-full text-xs font-bold border border-blue-100 dark:border-blue-800/40">
              0 Nhóm
            </span>
          </CardHeader>

          <CardContent className="p-12 text-center text-slate-500 dark:text-slate-400">
            <div className="max-w-sm mx-auto flex flex-col items-center gap-3">
              <div className="w-16 h-16 rounded-3xl bg-slate-100 dark:bg-slate-800 flex items-center justify-center text-slate-400 dark:text-slate-500 mb-1">
                <Users className="w-8 h-8 text-slate-400 dark:text-slate-500" />
              </div>
              <h3 className="font-bold text-slate-800 dark:text-slate-200 text-base">Chưa có nhóm nào</h3>
              <p className="text-xs text-slate-400 dark:text-slate-500 leading-relaxed">
                Vui lòng tải lên file Roster Excel (.xlsx) bên trái để hệ thống khởi tạo danh sách sinh viên và tạo nhóm làm đồ án.
              </p>
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
