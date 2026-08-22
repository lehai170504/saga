"use client";

import { useDeleteSubject } from "../api/useAcademic";
import { Subject } from "../types";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
} from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { AlertTriangle, Loader2 } from "lucide-react";
import { toast } from "sonner";

interface DeleteSubjectModalProps {
  subject: Subject | null;
  isOpen: boolean;
  onOpenChange: (open: boolean) => void;
}

export const DeleteSubjectModal = ({ subject, isOpen, onOpenChange }: DeleteSubjectModalProps) => {
  const { mutateAsync: deleteSubject, isPending } = useDeleteSubject();

  const handleDelete = async () => {
    if (!subject) return;

    try {
      await deleteSubject(subject.id);
      toast.success("Xóa môn học thành công!");
      onOpenChange(false);
    } catch (err: any) {
      const msg = err?.response?.data?.message || "Đã xảy ra lỗi khi xóa môn học.";
      toast.error(msg);
    }
  };

  const code = subject?.subjectCode || subject?.code || "";
  const name = subject?.subjectName || subject?.name || "";

  return (
    <Dialog open={isOpen} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-md bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 shadow-2xl transition-colors">
        <DialogHeader className="space-y-3">
          <div className="w-12 h-12 rounded-2xl bg-red-50 dark:bg-red-950/60 text-red-600 dark:text-red-400 flex items-center justify-center border border-red-100 dark:border-red-800/40 shadow-xs">
            <AlertTriangle className="w-6 h-6" />
          </div>
          <DialogTitle className="text-xl font-extrabold text-slate-900 dark:text-white">
            Xác Nhận Xóa Môn Học
          </DialogTitle>
          <DialogDescription className="text-slate-500 dark:text-slate-400 text-sm">
            Bạn có chắc chắn muốn xóa môn học <span className="font-bold text-slate-900 dark:text-slate-100 font-mono">[{code}] {name}</span> khỏi hệ thống? Hành động này không thể hoàn tác.
          </DialogDescription>
        </DialogHeader>

        <div className="flex items-center justify-end gap-3 pt-4">
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
            type="button"
            onClick={handleDelete}
            disabled={isPending}
            className="px-5 py-2.5 rounded-2xl bg-gradient-to-r from-rose-600 to-red-600 hover:from-rose-500 hover:to-red-500 text-white font-bold text-xs shadow-md shadow-rose-500/25 hover:shadow-lg hover:shadow-rose-500/35 hover:scale-[1.02] active:scale-[0.98] transition-all duration-200 border border-white/20 cursor-pointer gap-2"
          >
            {isPending ? (
              <>
                <Loader2 className="w-4 h-4 animate-spin" />
                <span>Đang xóa...</span>
              </>
            ) : (
              <span>Xác Nhận Xóa</span>
            )}
          </Button>
        </div>
      </DialogContent>
    </Dialog>
  );
};
