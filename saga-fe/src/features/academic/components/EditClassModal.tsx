"use client";

import { useState, useEffect, FormEvent } from "react";
import { useUpdateClass } from "../api/useAcademic";
import { AcademicClass } from "../types";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
} from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { School, Sparkles, Loader2 } from "lucide-react";
import { toast } from "sonner";

interface EditClassModalProps {
  cls: AcademicClass | null;
  isOpen: boolean;
  onOpenChange: (open: boolean) => void;
}

export const EditClassModal = ({ cls, isOpen, onOpenChange }: EditClassModalProps) => {
  const [classCode, setClassCode] = useState("");

  const { mutateAsync: updateClass, isPending } = useUpdateClass();

  useEffect(() => {
    if (cls) {
      setClassCode(cls.classCode || "");
    }
  }, [cls]);

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    if (!cls) return;

    if (!classCode) {
      toast.error("Vui lòng nhập mã lớp học.");
      return;
    }

    try {
      await updateClass({
        id: cls.id,
        payload: {
          classCode: classCode.trim(),
        },
      });

      toast.success("Cập nhật mã lớp học thành công!");
      onOpenChange(false);
    } catch (err: any) {
      const msg = err?.response?.data?.message || "Đã xảy ra lỗi khi cập nhật lớp học.";
      toast.error(msg);
    }
  };

  return (
    <Dialog open={isOpen} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-md bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 shadow-2xl transition-colors">
        <DialogHeader className="space-y-2">
          <div className="flex items-center gap-2">
            <span className="px-3 py-0.5 text-xs font-bold uppercase tracking-widest bg-indigo-100 dark:bg-indigo-500/30 text-indigo-700 dark:text-indigo-300 rounded-full border border-indigo-200 dark:border-indigo-400/30 flex items-center gap-1.5 w-fit">
              <Sparkles className="w-3.5 h-3.5" />
              Chỉnh sửa
            </span>
          </div>
          <DialogTitle className="text-xl font-extrabold text-slate-900 dark:text-white">
            Chỉnh Sửa Mã Lớp Học
          </DialogTitle>
          <DialogDescription className="text-slate-500 dark:text-slate-400 text-xs">
            Cập nhật lại thông tin mã lớp học chính thức.
          </DialogDescription>
        </DialogHeader>

        <form onSubmit={handleSubmit} className="space-y-4 mt-2">
          <div className="space-y-1.5">
            <label className="block text-xs font-bold uppercase tracking-wider text-slate-700 dark:text-slate-300">
              Mã Lớp Học <span className="text-red-500">*</span>
            </label>
            <div className="relative">
              <School className="w-4 h-4 absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-400 pointer-events-none" />
              <input
                type="text"
                placeholder="VD: SE1801, IA1702"
                value={classCode}
                onChange={(e) => setClassCode(e.target.value)}
                required
                className="w-full pl-10 pr-4 py-2.5 text-sm bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 text-slate-900 dark:text-slate-100 rounded-2xl outline-none focus:border-indigo-500 focus:ring-4 focus:ring-indigo-500/10 font-mono transition-all placeholder:text-slate-400 dark:placeholder:text-slate-500"
              />
            </div>
          </div>

          <div className="flex items-center justify-end gap-3 pt-3">
            <Button
              type="button"
              variant="outline"
              onClick={() => onOpenChange(false)}
              disabled={isPending}
              className="rounded-2xl border-slate-200 dark:border-slate-700 text-slate-700 dark:text-slate-300 text-xs font-semibold cursor-pointer"
            >
              Hủy bỏ
            </Button>
            <Button
              type="submit"
              disabled={isPending}
              className="px-5 py-2.5 rounded-2xl bg-gradient-to-r from-indigo-600 via-purple-600 to-indigo-600 hover:from-indigo-500 hover:to-purple-500 text-white font-bold text-xs shadow-md shadow-indigo-500/25 hover:shadow-lg hover:shadow-indigo-500/35 hover:scale-[1.02] active:scale-[0.98] transition-all duration-200 border border-white/20 cursor-pointer gap-2"
            >
              {isPending ? (
                <>
                  <Loader2 className="w-4 h-4 animate-spin" />
                  <span>Đang lưu...</span>
                </>
              ) : (
                <span>Lưu Thay Đổi</span>
              )}
            </Button>
          </div>
        </form>
      </DialogContent>
    </Dialog>
  );
};
