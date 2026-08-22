"use client";

import { useUpdateUserStatus } from "../api/useUserAdmin";
import {
  DropdownMenu,
  DropdownMenuTrigger,
  DropdownMenuContent,
  DropdownMenuItem,
} from "@/components/ui/dropdown-menu";
import { Check, ChevronDown, Loader2, ShieldAlert, ShieldCheck, Clock, Ban } from "lucide-react";
import { toast } from "sonner";

interface UserStatusDropdownProps {
  userId: string;
  currentStatus: string;
}

export const UserStatusDropdown = ({ userId, currentStatus }: UserStatusDropdownProps) => {
  const { mutateAsync: updateStatus, isPending } = useUpdateUserStatus();

  const handleStatusChange = async (newStatus: string) => {
    if (newStatus?.toUpperCase() === currentStatus?.toUpperCase()) return;

    try {
      await updateStatus({ userId, status: newStatus });
      toast.success(`Đã cập nhật trạng thái người dùng thành công.`);
    } catch (err: any) {
      const msg = err?.response?.data?.message || "Không thể cập nhật trạng thái người dùng.";
      toast.error(msg);
    }
  };

  const renderTriggerBadge = () => {
    if (isPending) {
      return (
        <span className="inline-flex items-center gap-1.5 px-3 py-1 text-xs font-semibold bg-slate-100 dark:bg-slate-800 text-slate-500 rounded-full border border-slate-200 dark:border-slate-700">
          <Loader2 className="w-3.5 h-3.5 animate-spin text-indigo-500" />
          Đang lưu...
        </span>
      );
    }

    switch (currentStatus?.toUpperCase()) {
      case "ACTIVE":
        return (
          <span className="inline-flex items-center gap-1.5 px-3 py-1 text-xs font-bold bg-emerald-50 dark:bg-emerald-950/40 text-emerald-700 dark:text-emerald-300 rounded-full border border-emerald-200/80 dark:border-emerald-800/40 hover:bg-emerald-100 dark:hover:bg-emerald-900/60 transition-colors shadow-2xs cursor-pointer">
            <span className="relative flex h-2 w-2">
              <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-emerald-400 opacity-75"></span>
              <span className="relative inline-flex rounded-full h-2 w-2 bg-emerald-500"></span>
            </span>
            Đang hoạt động
            <ChevronDown className="w-3.5 h-3.5 opacity-60 ml-0.5" />
          </span>
        );
      case "INACTIVE":
        return (
          <span className="inline-flex items-center gap-1.5 px-3 py-1 text-xs font-bold bg-amber-50 dark:bg-amber-950/40 text-amber-700 dark:text-amber-300 rounded-full border border-amber-200/80 dark:border-amber-800/40 hover:bg-amber-100 dark:hover:bg-amber-900/60 transition-colors shadow-2xs cursor-pointer">
            <span className="h-2 w-2 rounded-full bg-amber-500"></span>
            Tạm ngưng
            <ChevronDown className="w-3.5 h-3.5 opacity-60 ml-0.5" />
          </span>
        );
      case "BANNED":
      case "BLOCKED":
        return (
          <span className="inline-flex items-center gap-1.5 px-3 py-1 text-xs font-bold bg-rose-50 dark:bg-rose-950/40 text-rose-700 dark:text-rose-300 rounded-full border border-rose-200/80 dark:border-rose-800/40 hover:bg-rose-100 dark:hover:bg-rose-900/60 transition-colors shadow-2xs cursor-pointer">
            <span className="h-2 w-2 rounded-full bg-rose-500"></span>
            Bị khóa
            <ChevronDown className="w-3.5 h-3.5 opacity-60 ml-0.5" />
          </span>
        );
      case "PENDING":
        return (
          <span className="inline-flex items-center gap-1.5 px-3 py-1 text-xs font-bold bg-blue-50 dark:bg-blue-950/40 text-blue-700 dark:text-blue-300 rounded-full border border-blue-200/80 dark:border-blue-800/40 hover:bg-blue-100 dark:hover:bg-blue-900/60 transition-colors shadow-2xs cursor-pointer">
            <span className="h-2 w-2 rounded-full bg-blue-500"></span>
            Chờ duyệt
            <ChevronDown className="w-3.5 h-3.5 opacity-60 ml-0.5" />
          </span>
        );
      default:
        return (
          <span className="inline-flex items-center gap-1.5 px-3 py-1 text-xs font-bold bg-slate-100 dark:bg-slate-800 text-slate-700 dark:text-slate-300 rounded-full border border-slate-200 dark:border-slate-700 hover:bg-slate-200 dark:hover:bg-slate-700 transition-colors cursor-pointer">
            {currentStatus || "Hoạt động"}
            <ChevronDown className="w-3.5 h-3.5 opacity-60 ml-0.5" />
          </span>
        );
    }
  };

  const statuses = [
    { value: "ACTIVE", label: "Đang hoạt động", icon: ShieldCheck, color: "text-emerald-600 dark:text-emerald-400" },
    { value: "PENDING", label: "Chờ duyệt", icon: Clock, color: "text-blue-600 dark:text-blue-400" },
    { value: "INACTIVE", label: "Tạm ngưng", icon: ShieldAlert, color: "text-amber-600 dark:text-amber-400" },
    { value: "BANNED", label: "Bị khóa (Banned)", icon: Ban, color: "text-rose-600 dark:text-rose-400" },
  ];

  return (
    <DropdownMenu>
      <DropdownMenuTrigger disabled={isPending}>
        {renderTriggerBadge()}
      </DropdownMenuTrigger>
      <DropdownMenuContent align="start" className="w-48 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-xl rounded-2xl p-1.5 z-50">
        {statuses.map((st) => {
          const Icon = st.icon;
          const isSelected = currentStatus?.toUpperCase() === st.value;
          return (
            <DropdownMenuItem
              key={st.value}
              onClick={() => handleStatusChange(st.value)}
              className="flex items-center justify-between px-3 py-2 text-xs font-semibold text-slate-700 dark:text-slate-200 hover:bg-slate-100 dark:hover:bg-slate-800 rounded-xl cursor-pointer transition-colors"
            >
              <div className="flex items-center gap-2">
                <Icon className={`w-4 h-4 ${st.color}`} />
                <span>{st.label}</span>
              </div>
              {isSelected && <Check className="w-3.5 h-3.5 text-indigo-600 dark:text-indigo-400" />}
            </DropdownMenuItem>
          );
        })}
      </DropdownMenuContent>
    </DropdownMenu>
  );
};
