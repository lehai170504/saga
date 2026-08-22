"use client";

import { Card, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Sparkles, Layers, Plus } from "lucide-react";

export default function CoursesAdminPage() {
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
          <p className="text-slate-600 dark:text-slate-300 text-sm max-w-xl">Quản lý danh sách các khóa học được tạo theo từng học kỳ và giảng viên đảm nhận.</p>
        </div>

        <div className="relative z-10 flex items-center gap-3">
          <Button className="bg-indigo-600 hover:bg-indigo-500 text-white rounded-2xl px-5 py-2.5 shadow-lg shadow-indigo-600/30 font-semibold transition-all gap-2 cursor-pointer">
            <Plus className="w-4 h-4" />
            Thêm Khóa Học Mới
          </Button>
        </div>
      </div>

      {/* Content Container */}
      <Card className="border border-slate-200/80 dark:border-slate-800 shadow-md shadow-slate-200/40 dark:shadow-none rounded-3xl overflow-hidden bg-white dark:bg-slate-900 transition-colors">
        <CardContent className="p-8 text-center space-y-4">
          <div className="w-16 h-16 rounded-3xl bg-indigo-50 dark:bg-indigo-950/60 text-indigo-600 dark:text-indigo-400 flex items-center justify-center mx-auto border border-indigo-100 dark:border-indigo-800/40 shadow-xs">
            <Layers className="w-8 h-8" />
          </div>
          <h3 className="text-lg font-bold text-slate-900 dark:text-slate-100">Danh Sách Khóa Học</h3>
          <p className="text-slate-500 dark:text-slate-400 text-sm max-w-md mx-auto">
            Hệ thống đang sẵn sàng cho dữ liệu khóa học chính thức. Bạn có thể thêm khóa học mới hoặc liên kết giảng viên.
          </p>
        </CardContent>
      </Card>
    </div>
  );
}
