"use client";

import { useState, FormEvent } from "react";
import {
  useCreateCourse,
  useSemesters,
  useSubjects,
  useClasses,
} from "../api/useAcademic";
import { useAllLecturers } from "@/features/users/api/useUserAdmin";
import { CustomSelect } from "@/components/ui/custom-select";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
} from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import {
  Calendar,
  BookOpen,
  School,
  UserCheck,
  Sparkles,
  Loader2,
} from "lucide-react";
import { toast } from "sonner";

interface CreateCourseModalProps {
  isOpen: boolean;
  onOpenChange: (open: boolean) => void;
}

export const CreateCourseModal = ({ isOpen, onOpenChange }: CreateCourseModalProps) => {
  const [semesterId, setSemesterId] = useState("");
  const [subjectId, setSubjectId] = useState("");
  const [classId, setClassId] = useState("");
  const [instructorId, setInstructorId] = useState("");

  const { data: semesterData } = useSemesters(0, 100);
  const { data: subjectData } = useSubjects(0, 100);
  const { data: classData } = useClasses(0, 100);
  const { data: lecturerList } = useAllLecturers();

  const { mutateAsync: createCourse, isPending } = useCreateCourse();

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();

    if (!semesterId || !subjectId || !classId || !instructorId) {
      toast.error("Vui lòng chọn đầy đủ Học Kỳ, Môn Học, Lớp Học và Giảng Viên.");
      return;
    }

    try {
      await createCourse({
        semesterId,
        subjectId,
        classId,
        instructorId,
      });

      toast.success("Tạo khóa học & phân công giảng viên thành công!");
      onOpenChange(false);
      // Reset form
      setSemesterId("");
      setSubjectId("");
      setClassId("");
      setInstructorId("");
    } catch (err: any) {
      const msg = err?.response?.data?.message || "Đã xảy ra lỗi khi tạo khóa học mới.";
      toast.error(msg);
    }
  };

  const semesterOptions =
    semesterData?.content?.map((sem) => ({
      value: sem.id,
      label: `${sem.name} (${sem.code})`,
    })) || [];

  const subjectOptions =
    subjectData?.content?.map((sub) => {
      const code = sub.subjectCode || sub.code || "";
      const name = sub.subjectName || sub.name || "";
      return {
        value: sub.id,
        label: `[${code}] ${name}`,
      };
    }) || [];

  const classOptions =
    classData?.content?.map((cls) => ({
      value: cls.id,
      label: cls.classCode,
    })) || [];

  const lecturerOptions =
    lecturerList?.map((lec) => ({
      value: lec.id,
      label: lec.name || "Giảng viên",
      sublabel: lec.email,
    })) || [];

  return (
    <Dialog open={isOpen} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-lg w-full max-h-[90vh] overflow-y-auto bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 md:p-8 shadow-2xl transition-colors">
        <DialogHeader className="space-y-2">
          <div className="flex items-center gap-2">
            <span className="px-3 py-0.5 text-xs font-bold uppercase tracking-widest bg-indigo-100 dark:bg-indigo-500/30 text-indigo-700 dark:text-indigo-300 rounded-full border border-indigo-200 dark:border-indigo-400/30 flex items-center gap-1.5 w-fit">
              <Sparkles className="w-3.5 h-3.5" />
              Phân công giảng dạy
            </span>
          </div>
          <DialogTitle className="text-xl font-extrabold text-slate-900 dark:text-white">
            Thêm Khóa Học Mới
          </DialogTitle>
          <DialogDescription className="text-slate-500 dark:text-slate-400 text-xs">
            Tạo lớp học phần mới và phân công giảng viên phụ trách theo từng học kỳ.
          </DialogDescription>
        </DialogHeader>

        <form onSubmit={handleSubmit} className="space-y-4 mt-2">
          {/* Học Kỳ Select */}
          <div className="space-y-1.5">
            <label className="block text-xs font-bold uppercase tracking-wider text-slate-700 dark:text-slate-300">
              Học Kỳ <span className="text-red-500">*</span>
            </label>
            <CustomSelect
              options={semesterOptions}
              value={semesterId}
              onChange={setSemesterId}
              placeholder="-- Chọn học kỳ --"
              icon={<Calendar className="w-4 h-4" />}
            />
          </div>

          {/* Môn Học Select */}
          <div className="space-y-1.5">
            <label className="block text-xs font-bold uppercase tracking-wider text-slate-700 dark:text-slate-300">
              Môn Học <span className="text-red-500">*</span>
            </label>
            <CustomSelect
              options={subjectOptions}
              value={subjectId}
              onChange={setSubjectId}
              placeholder="-- Chọn môn học --"
              icon={<BookOpen className="w-4 h-4" />}
            />
          </div>

          {/* Lớp Học Select */}
          <div className="space-y-1.5">
            <label className="block text-xs font-bold uppercase tracking-wider text-slate-700 dark:text-slate-300">
              Lớp Học <span className="text-red-500">*</span>
            </label>
            <CustomSelect
              options={classOptions}
              value={classId}
              onChange={setClassId}
              placeholder="-- Chọn lớp học --"
              icon={<School className="w-4 h-4" />}
            />
          </div>

          {/* Giảng Viên Select */}
          <div className="space-y-1.5">
            <label className="block text-xs font-bold uppercase tracking-wider text-slate-700 dark:text-slate-300">
              Giảng Viên Phụ Trách <span className="text-red-500">*</span>
            </label>
            <CustomSelect
              options={lecturerOptions}
              value={instructorId}
              onChange={setInstructorId}
              placeholder="-- Chọn giảng viên --"
              icon={<UserCheck className="w-4 h-4" />}
            />
          </div>

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
              type="submit"
              disabled={isPending}
              className="px-5 py-2.5 rounded-2xl bg-gradient-to-r from-indigo-600 via-purple-600 to-indigo-600 hover:from-indigo-500 hover:to-purple-500 text-white font-bold text-xs shadow-md shadow-indigo-500/25 hover:shadow-lg hover:shadow-indigo-500/35 hover:scale-[1.02] active:scale-[0.98] transition-all duration-200 border border-white/20 cursor-pointer gap-2"
            >
              {isPending ? (
                <>
                  <Loader2 className="w-4 h-4 animate-spin" />
                  <span>Đang xử lý...</span>
                </>
              ) : (
                <span>Xác Nhận Tạo</span>
              )}
            </Button>
          </div>
        </form>
      </DialogContent>
    </Dialog>
  );
};
