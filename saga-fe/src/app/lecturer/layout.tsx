"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import { useAuth } from "@/features/auth/api/useAuth";
import { useState } from "react";
import {
  BookOpen,
  LogOut,
  Menu,
  X,
  GraduationCap,
  ChevronLeft,
  ChevronRight,
} from "lucide-react";
import { ProfileModal } from "@/features/auth/components/ProfileModal";
import { ThemeToggle } from "@/components/ThemeToggle";

export default function LecturerLayout({ children }: { children: React.ReactNode }) {
  const pathname = usePathname();
  const { user, logout } = useAuth();
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const [isCollapsed, setIsCollapsed] = useState(false);
  const [isProfileOpen, setIsProfileOpen] = useState(false);

  const menuItems = [
    {
      title: "Quản Lý Khóa Học & Roster",
      href: "/lecturer/courses",
      icon: BookOpen,
    },
  ];

  return (
    <div className="flex h-screen bg-slate-100 overflow-hidden">
      {/* Mobile Backdrop */}
      {sidebarOpen && (
        <div
          className="fixed inset-0 bg-slate-900/50 backdrop-blur-sm z-40 lg:hidden"
          onClick={() => setSidebarOpen(false)}
        />
      )}

      {/* Sidebar */}
      <aside
        className={`relative fixed lg:static inset-y-0 left-0 z-50 bg-slate-900 text-slate-300 flex flex-col transition-all duration-300 ease-in-out ${
          isCollapsed ? "w-20" : "w-64"
        } ${sidebarOpen ? "translate-x-0" : "-translate-x-full lg:translate-x-0"}`}
      >
        {/* Toggle Button Floating on Middle Vertical Border */}
        <button
          onClick={() => setIsCollapsed(!isCollapsed)}
          className="hidden lg:flex items-center justify-center absolute top-1/2 -right-3.5 -translate-y-1/2 w-7 h-7 rounded-full bg-slate-800 text-slate-300 border border-slate-700 hover:bg-slate-700 hover:text-white shadow-md shadow-slate-950/50 cursor-pointer transition-all z-20"
          title={isCollapsed ? "Mở rộng Sidebar" : "Thu gọn Sidebar"}
        >
          <ChevronLeft className={`w-4 h-4 transition-transform duration-300 ${isCollapsed ? "rotate-180" : ""}`} />
        </button>

        {/* Brand Header */}
        <div className={`h-16 flex items-center border-b border-slate-800 transition-all ${isCollapsed ? "justify-center px-2" : "justify-between px-5"}`}>
          <Link href="/lecturer/courses/default" className="flex items-center gap-3 group min-w-0">
            <div className="w-9 h-9 rounded-xl bg-gradient-to-br from-blue-600 to-indigo-600 flex items-center justify-center shadow-md shadow-blue-500/20 group-hover:scale-105 transition-transform shrink-0">
              <GraduationCap className="w-5 h-5 text-white" />
            </div>
            {!isCollapsed && (
              <div className="min-w-0 truncate">
                <span className="text-lg font-bold text-white tracking-tight">SAGA</span>
                <span className="block text-[10px] font-semibold uppercase tracking-widest text-blue-400">Lecturer Portal</span>
              </div>
            )}
          </Link>

          {/* Close Button for Mobile */}
          <button
            onClick={() => setSidebarOpen(false)}
            className="lg:hidden text-slate-400 hover:text-white"
          >
            <X className="w-6 h-6" />
          </button>
        </div>

        {/* Navigation Links */}
        <div className="flex-1 py-6 px-3 space-y-1.5 overflow-y-auto">
          {!isCollapsed && (
            <p className="px-3 text-[10px] font-bold text-slate-500 uppercase tracking-wider mb-2">
              Giảng Dạy & Lớp Học
            </p>
          )}

          {menuItems.map((item) => {
            const isActive = pathname.startsWith(item.href);
            const Icon = item.icon;
            return (
              <Link
                key={item.href}
                href={item.href + "/default"}
                title={isCollapsed ? item.title : undefined}
                className={`flex items-center rounded-xl text-sm font-medium transition-all ${
                  isCollapsed ? "justify-center p-3" : "justify-between px-3.5 py-2.5"
                } ${
                  isActive
                    ? "bg-blue-600 text-white shadow-lg shadow-blue-600/30 font-semibold"
                    : "text-slate-400 hover:bg-slate-800 hover:text-slate-200"
                }`}
              >
                <div className="flex items-center gap-3">
                  <Icon className={`w-5 h-5 shrink-0 ${isActive ? "text-white" : "text-slate-400"}`} />
                  {!isCollapsed && <span className="truncate">{item.title}</span>}
                </div>
                {!isCollapsed && isActive && <ChevronRight className="w-4 h-4 text-white/80 shrink-0" />}
              </Link>
            );
          })}
        </div>

        {/* User Footer Profile */}
        <div className={`border-t border-slate-800 bg-slate-950/40 transition-all ${isCollapsed ? "p-2.5 flex flex-col items-center gap-3" : "p-4"}`}>
          <div className={`flex items-center transition-all ${isCollapsed ? "flex-col gap-2.5" : "justify-between w-full"}`}>
            <button
              onClick={() => setIsProfileOpen(true)}
              title={user?.name || "Lecturer Profile"}
              className="flex items-center gap-3 text-left min-w-0 hover:opacity-80 transition-opacity"
            >
              {user?.picture ? (
                <img
                  src={user.picture}
                  alt={user.name || "Lecturer"}
                  className="w-9 h-9 rounded-full object-cover border border-slate-700 shadow-sm shrink-0"
                />
              ) : (
                <div className="w-9 h-9 rounded-full bg-blue-500/20 text-blue-400 border border-blue-500/30 flex items-center justify-center font-bold text-sm shrink-0">
                  {user?.name?.charAt(0) || user?.email?.charAt(0) || "L"}
                </div>
              )}
              {!isCollapsed && (
                <div className="min-w-0 flex-1">
                  <p className="text-sm font-semibold text-slate-200 truncate">{user?.name || "Giảng viên"}</p>
                  <span className="inline-block text-[10px] font-bold text-blue-400 uppercase tracking-wider">Giảng viên</span>
                </div>
              )}
            </button>

            <div className="flex items-center gap-1 shrink-0">
              <ThemeToggle />
              <button
                onClick={() => logout()}
                title="Đăng xuất"
                className="p-2 text-slate-400 hover:text-red-400 hover:bg-slate-800 rounded-lg transition-colors shrink-0"
              >
                <LogOut className="w-5 h-5" />
              </button>
            </div>
          </div>
        </div>
      </aside>

      {/* Main Content Wrapper */}
      <div className="flex-1 flex flex-col min-w-0 overflow-hidden">
        {/* Top Header Bar for Mobile */}
        <header className="lg:hidden h-16 bg-white border-b border-slate-200 px-4 flex items-center justify-between">
          <button
            onClick={() => setSidebarOpen(true)}
            className="p-2 text-slate-600 hover:bg-slate-100 rounded-lg"
          >
            <Menu className="w-6 h-6" />
          </button>
          <span className="font-bold text-slate-800">SAGA Lecturer</span>
          <div className="w-6" />
        </header>

        {/* Dynamic Children Content */}
        <main className="flex-1 overflow-y-auto">
          {children}
        </main>
      </div>

      <ProfileModal isOpen={isProfileOpen} onOpenChange={setIsProfileOpen} />
    </div>
  );
}
