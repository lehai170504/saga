"use client";

import { useAuth } from "@/features/auth/api/useAuth";
import { useIdentity } from "@/features/identity/api/useIdentity";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { toast } from "sonner";

import { useState, useEffect } from "react";
import { User, Settings, CheckCircle2, Sparkles, Mail, ShieldCheck, KeyRound } from "lucide-react";

interface ProfileModalProps {
  isOpen: boolean;
  onOpenChange: (open: boolean) => void;
  defaultTab?: "info" | "settings";
}

function formatDate(dateStr: string | null | undefined) {
  if (!dateStr) return null;
  const d = new Date(dateStr);
  const pad = (n: number) => String(n).padStart(2, "0");
  return `${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())} ${pad(d.getDate())}/${pad(d.getMonth() + 1)}/${d.getFullYear()}`;
}

export function ProfileModal({ isOpen, onOpenChange, defaultTab = "info" }: ProfileModalProps) {
  const { user } = useAuth();
  const { identities, isLoading: isIdentityLoading, unlinkIdentity, isUnlinking } = useIdentity();
  const [activeTab, setActiveTab] = useState<"info" | "settings">(defaultTab);

  useEffect(() => {
    if (isOpen) {
      setActiveTab(defaultTab);
    }
  }, [isOpen, defaultTab]);

  if (!user) return null;

  const handleUnlink = async (provider: "GITHUB" | "JIRA") => {
    try {
      await unlinkIdentity(provider);
      toast.success(`Đã ngắt kết nối với ${provider}`);
    } catch {
      toast.error("Lỗi khi ngắt kết nối");
    }
  };

  const handleLinkGithub = () => {
    const clientId = process.env.NEXT_PUBLIC_GITHUB_CLIENT_ID;
    const redirectUri = encodeURIComponent("http://localhost:3000/identities/callback");
    window.location.href = `https://github.com/login/oauth/authorize?client_id=${clientId}&redirect_uri=${redirectUri}&scope=user:email&state=github`;
  };

  const handleLinkJira = () => {
    const clientId = process.env.NEXT_PUBLIC_JIRA_CLIENT_ID;
    const redirectUri = encodeURIComponent("http://localhost:3000/identities/callback");
    window.location.href = `https://auth.atlassian.com/authorize?audience=api.atlassian.com&client_id=${clientId}&scope=read:me&redirect_uri=${redirectUri}&state=jira&response_type=code&prompt=consent`;
  };

  const githubIdentity = identities?.find((id) => id.externalProvider === "GITHUB");
  const jiraIdentity = identities?.find((id) => id.externalProvider === "JIRA");

  const getRoleLabel = (role?: string) => {
    switch (role?.toUpperCase()) {
      case "LECTURER":
        return "Giảng viên";
      case "ADMIN":
        return "Quản trị viên";
      case "STUDENT":
        return "Sinh viên";
      default:
        return role || "Sinh viên";
    }
  };

  const isStudent = user.role !== "ADMIN" && user.role !== "LECTURER";

  return (
    <Dialog open={isOpen} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-2xl overflow-hidden p-0 rounded-3xl border border-slate-200/80 dark:border-slate-800 shadow-2xl bg-white dark:bg-slate-900 transition-colors">
        {/* Header Modal */}
        <div className="p-6 pb-0 bg-slate-900 dark:bg-slate-950 text-white relative overflow-hidden">
          <div className="absolute top-0 right-0 w-48 h-48 bg-indigo-500/20 rounded-full blur-2xl pointer-events-none" />

          <DialogHeader className="mb-4 relative z-10">
            <DialogTitle className="text-xl font-extrabold text-white flex items-center gap-2">
              <Sparkles className="w-5 h-5 text-indigo-400" />
              {isStudent ? "Hồ Sơ & Cài Đặt Tích Hợp" : "Hồ Sơ Cá Nhân"}
            </DialogTitle>
          </DialogHeader>

          {/* Navigation Tabs */}
          {isStudent && (
            <div className="flex gap-2 border-b border-slate-800 relative z-10">
              <button
                type="button"
                onClick={() => setActiveTab("info")}
                className={`flex items-center gap-2 px-4 py-3 text-xs font-bold transition-all cursor-pointer rounded-t-xl border-b-2 ${
                  activeTab === "info"
                    ? "border-indigo-400 text-indigo-400 bg-slate-800/80"
                    : "border-transparent text-slate-400 hover:text-slate-200"
                }`}
              >
                <User className="w-4 h-4" />
                <span>Thông tin cá nhân</span>
              </button>
              <button
                type="button"
                onClick={() => setActiveTab("settings")}
                className={`flex items-center gap-2 px-4 py-3 text-xs font-bold transition-all cursor-pointer rounded-t-xl border-b-2 ${
                  activeTab === "settings"
                    ? "border-indigo-400 text-indigo-400 bg-slate-800/80"
                    : "border-transparent text-slate-400 hover:text-slate-200"
                }`}
              >
                <Settings className="w-4 h-4" />
                <span>Cài đặt & Tích hợp</span>
              </button>
            </div>
          )}
        </div>

        <div className="p-6 md:p-8">
          {/* TAB 1: THÔNG TIN CÁ NHÂN */}
          {activeTab === "info" && (
            <div className="space-y-6 animate-in fade-in-50 duration-150">
              {/* Profile Card Banner */}
              <div className="flex flex-col sm:flex-row items-center gap-5 p-5 rounded-3xl bg-gradient-to-r from-slate-900 via-indigo-950 to-slate-900 text-white shadow-lg relative overflow-hidden">
                <div className="absolute top-0 right-0 w-36 h-36 bg-indigo-500/20 rounded-full blur-xl pointer-events-none" />

                {user.picture ? (
                  <img
                    src={user.picture}
                    alt="Avatar"
                    className="w-20 h-20 rounded-full shadow-md object-cover ring-4 ring-indigo-500/30 shrink-0"
                  />
                ) : (
                  <div className="w-20 h-20 rounded-full bg-gradient-to-br from-indigo-500 to-purple-600 flex items-center justify-center text-white text-3xl font-black ring-4 ring-indigo-500/30 shadow-md shrink-0">
                    {user.name?.charAt(0) || user.email?.charAt(0) || "U"}
                  </div>
                )}
                <div className="space-y-1 text-center sm:text-left min-w-0">
                  <span className="inline-flex items-center gap-1.5 px-3 py-0.5 bg-emerald-500/20 text-emerald-300 text-xs font-semibold rounded-full border border-emerald-400/30">
                    <CheckCircle2 className="w-3.5 h-3.5" />
                    Tài khoản đã xác thực
                  </span>
                  <h2 className="text-xl font-extrabold text-white truncate">{user.name || "Chưa cập nhật tên"}</h2>
                  <p className="text-slate-300 text-xs font-mono truncate">{user.email}</p>
                  <div className="pt-1">
                    <span className="inline-block px-3 py-1 bg-indigo-500/30 text-indigo-200 text-xs font-bold rounded-full border border-indigo-400/30">
                      Vai trò: {getRoleLabel(user.role)}
                    </span>
                  </div>
                </div>
              </div>

              {/* Profile Details Grid */}
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div className="p-4 rounded-2xl border border-slate-200/80 dark:border-slate-800 bg-slate-50/50 dark:bg-slate-800/50 space-y-1">
                  <div className="flex items-center gap-1.5 text-xs text-slate-400 dark:text-slate-500 font-bold uppercase tracking-wider">
                    <User className="w-3.5 h-3.5" /> Họ và tên
                  </div>
                  <p className="text-sm font-bold text-slate-900 dark:text-slate-100">{user.name || "Chưa cập nhật"}</p>
                </div>

                <div className="p-4 rounded-2xl border border-slate-200/80 dark:border-slate-800 bg-slate-50/50 dark:bg-slate-800/50 space-y-1">
                  <div className="flex items-center gap-1.5 text-xs text-slate-400 dark:text-slate-500 font-bold uppercase tracking-wider">
                    <Mail className="w-3.5 h-3.5" /> Địa chỉ Email
                  </div>
                  <p className="text-sm font-bold text-slate-900 dark:text-slate-100 font-mono">{user.email}</p>
                </div>

                <div className="p-4 rounded-2xl border border-slate-200/80 dark:border-slate-800 bg-slate-50/50 dark:bg-slate-800/50 space-y-1">
                  <div className="flex items-center gap-1.5 text-xs text-slate-400 dark:text-slate-500 font-bold uppercase tracking-wider">
                    <ShieldCheck className="w-3.5 h-3.5" /> Vai trò hệ thống
                  </div>
                  <p className="text-sm font-bold text-slate-900 dark:text-slate-100">{getRoleLabel(user.role)}</p>
                </div>

                <div className="p-4 rounded-2xl border border-slate-200/80 dark:border-slate-800 bg-slate-50/50 dark:bg-slate-800/50 space-y-1">
                  <div className="flex items-center gap-1.5 text-xs text-slate-400 dark:text-slate-500 font-bold uppercase tracking-wider">
                    <KeyRound className="w-3.5 h-3.5" /> Phương thức xác thực
                  </div>
                  <p className="text-sm font-bold text-slate-900 dark:text-slate-100">OAuth 2.0 SAGA Authentication</p>
                </div>
              </div>
            </div>
          )}

          {/* TAB 2: CÀI ĐẶT & TÍCH HỢP */}
          {activeTab === "settings" && (
            <div className="space-y-5 animate-in fade-in-50 duration-150">
              <div>
                <h3 className="text-base font-bold text-slate-900 dark:text-white mb-1">Tích Hợp Dữ Liệu Công Việc</h3>
                <p className="text-xs text-slate-500 dark:text-slate-400">
                  Liên kết với Jira và GitHub để thuật toán SAGA tự động tính toán đồ thị đóng góp dự án.
                </p>
              </div>

              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                {/* Jira Card */}
                <div
                  className={`relative border rounded-3xl p-5 flex flex-col justify-between gap-4 transition-all ${
                    jiraIdentity
                      ? "border-blue-300 dark:border-blue-800 bg-blue-50/40 dark:bg-blue-950/30"
                      : "border-slate-200 dark:border-slate-800 bg-white dark:bg-slate-800/60"
                  }`}
                >
                  {jiraIdentity && (
                    <span className="absolute top-4 right-4 flex items-center gap-1 px-2.5 py-0.5 bg-emerald-100 dark:bg-emerald-950/80 text-emerald-700 dark:text-emerald-300 text-xs font-bold rounded-full">
                      <CheckCircle2 className="w-3.5 h-3.5" />
                      Đã kết nối
                    </span>
                  )}
                  <div className="flex items-center gap-3">
                    <img src="https://cdn.worldvectorlogo.com/logos/jira-3.svg" alt="Jira" className="w-10 h-10 shrink-0" />
                    <div>
                      <p className="font-extrabold text-slate-900 dark:text-white text-base">Jira Software</p>
                      <p className="text-xs text-slate-500 dark:text-slate-400">Đồng bộ Task & Sprint</p>
                    </div>
                  </div>

                  {jiraIdentity ? (
                    <div className="border-t border-blue-200/60 dark:border-blue-800/60 pt-3 space-y-2">
                      <div>
                        <p className="text-[11px] text-slate-400 dark:text-slate-500 font-bold uppercase">Tài khoản Jira</p>
                        <p className="text-xs font-bold text-slate-800 dark:text-slate-200 truncate">
                          {jiraIdentity.name || "Tài khoản Jira"}
                          {jiraIdentity.email ? ` (${jiraIdentity.email})` : ""}
                        </p>
                        {jiraIdentity.connectedAt && (
                          <p className="text-[10px] text-slate-400 dark:text-slate-500 mt-0.5">Xác thực: {formatDate(jiraIdentity.connectedAt)}</p>
                        )}
                      </div>
                      <Button
                        variant="destructive"
                        size="sm"
                        onClick={() => handleUnlink("JIRA")}
                        disabled={isUnlinking}
                        className="w-full rounded-2xl text-xs font-bold"
                      >
                        Ngắt kết nối Jira
                      </Button>
                    </div>
                  ) : (
                    <Button
                      variant="default"
                      size="sm"
                      onClick={handleLinkJira}
                      disabled={isIdentityLoading}
                      className="w-full bg-[#0052CC] hover:bg-[#0065FF] text-white rounded-2xl font-bold text-xs shadow-md shadow-blue-600/20"
                    >
                      + Liên kết với Jira
                    </Button>
                  )}
                </div>

                {/* GitHub Card */}
                <div
                  className={`relative border rounded-3xl p-5 flex flex-col justify-between gap-4 transition-all ${
                    githubIdentity
                      ? "border-slate-300 dark:border-slate-700 bg-slate-50 dark:bg-slate-800/60"
                      : "border-slate-200 dark:border-slate-800 bg-white dark:bg-slate-800/60"
                  }`}
                >
                  {githubIdentity && (
                    <span className="absolute top-4 right-4 flex items-center gap-1 px-2.5 py-0.5 bg-emerald-100 dark:bg-emerald-950/80 text-emerald-700 dark:text-emerald-300 text-xs font-bold rounded-full">
                      <CheckCircle2 className="w-3.5 h-3.5" />
                      Đã kết nối
                    </span>
                  )}
                  <div className="flex items-center gap-3">
                    <svg className="w-10 h-10 text-slate-900 dark:text-slate-100 shrink-0" viewBox="0 0 24 24" fill="currentColor">
                      <path
                        fillRule="evenodd"
                        clipRule="evenodd"
                        d="M12 2C6.477 2 2 6.477 2 12c0 4.42 2.865 8.166 6.839 9.489.5.092.682-.217.682-.482 0-.237-.008-.866-.013-1.7-2.782.603-3.369-1.34-3.369-1.34-.454-1.156-1.11-1.462-1.11-1.462-.908-.62.069-.608.069-.608 1.003.07 1.531 1.03 1.531 1.03.892 1.529 2.341 1.087 2.91.831.092-.646.35-1.086.636-1.336-2.22-.253-4.555-1.11-4.555-4.943 0-1.091.39-1.984 1.029-2.683-.103-.253-.446-1.27.098-2.647 0 0 .84-.269 2.75 1.025A9.578 9.578 0 0112 6.836c.85.004 1.705.114 2.504.336 1.909-1.294 2.747-1.025 2.747-1.025.546 1.377.203 2.394.1 2.647.64.699 1.028 1.592 1.028 2.683 0 3.842-2.339 4.687-4.566 4.935.359.309.678.919.678 1.852 0 1.336-.012 2.415-.012 2.743 0 .267.18.578.688.48C19.138 20.161 22 16.416 22 12c0-5.523-4.477-10-10-10z"
                      />
                    </svg>
                    <div>
                      <p className="font-extrabold text-slate-900 dark:text-white text-base">GitHub</p>
                      <p className="text-xs text-slate-500 dark:text-slate-400">Đồng bộ Commits & PRs</p>
                    </div>
                  </div>

                  {githubIdentity ? (
                    <div className="border-t border-slate-200 dark:border-slate-700 pt-3 space-y-2">
                      <div>
                        <p className="text-[11px] text-slate-400 dark:text-slate-500 font-bold uppercase">Tài khoản GitHub</p>
                        <p className="text-xs font-bold text-slate-800 dark:text-slate-200 truncate">
                          {githubIdentity.name || "Tài khoản GitHub"}
                          {githubIdentity.email ? ` (${githubIdentity.email})` : ""}
                        </p>
                        {githubIdentity.connectedAt && (
                          <p className="text-[10px] text-slate-400 dark:text-slate-500 mt-0.5">Xác thực: {formatDate(githubIdentity.connectedAt)}</p>
                        )}
                      </div>
                      <Button
                        variant="destructive"
                        size="sm"
                        onClick={() => handleUnlink("GITHUB")}
                        disabled={isUnlinking}
                        className="w-full rounded-2xl text-xs font-bold"
                      >
                        Ngắt kết nối GitHub
                      </Button>
                    </div>
                  ) : (
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={handleLinkGithub}
                      disabled={isIdentityLoading}
                      className="w-full border-slate-800 dark:border-slate-600 text-slate-800 dark:text-slate-200 hover:bg-slate-900 hover:text-white dark:hover:bg-slate-700 rounded-2xl font-bold text-xs shadow-xs"
                    >
                      + Liên kết với GitHub
                    </Button>
                  )}
                </div>
              </div>
            </div>
          )}
        </div>
      </DialogContent>
    </Dialog>
  );
}
