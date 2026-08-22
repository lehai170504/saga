"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import { useAuth } from "@/features/auth/api/useAuth";
import { useState, useEffect } from "react";
import {
  GraduationCap,
  Users,
  User,
  UserCheck,
  LogOut,
  Menu,
  X,
  Shield,
  ChevronLeft,
  ChevronDown,
  ChevronRight,
  Calendar,
  BookOpen,
  Layers,
  School,
} from "lucide-react";
import { ProfileModal } from "@/features/auth/components/ProfileModal";
import { ThemeToggle } from "@/components/ThemeToggle";

export default function AdminLayout({ children }: { children: React.ReactNode }) {
  const pathname = usePathname();
  const { user, logout } = useAuth();
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const [isCollapsed, setIsCollapsed] = useState(false);
  const [isProfileOpen, setIsProfileOpen] = useState(false);

  // Submenu toggle states
  const isAcademicRouteActive = pathname.startsWith("/admin/academic");
  const [isAcademicDropdownOpen, setIsAcademicDropdownOpen] = useState(isAcademicRouteActive);

  const isUsersRouteActive = pathname.startsWith("/admin/users");
  const [isUsersDropdownOpen, setIsUsersDropdownOpen] = useState(isUsersRouteActive);

  useEffect(() => {
    if (isAcademicRouteActive) {
      setIsAcademicDropdownOpen(true);
    }
    if (isUsersRouteActive) {
      setIsUsersDropdownOpen(true);
    }
  }, [pathname, isAcademicRouteActive, isUsersRouteActive]);

  const menuItems = [
    {
      title: "Dữ Liệu Đào Tạo",
      icon: GraduationCap,
      isDropdown: true,
      isOpen: isAcademicDropdownOpen,
      isActive: isAcademicRouteActive,
      toggle: () => {
        if (isCollapsed) setIsCollapsed(false);
        setIsAcademicDropdownOpen((prev) => !prev);
      },
      subItems: [
        {
          title: "Học Kỳ",
          href: "/admin/academic",
          icon: Calendar,
        },
        {
          title: "Môn Học",
          href: "/admin/academic/subjects",
          icon: BookOpen,
        },
        {
          title: "Khóa Học",
          href: "/admin/academic/courses",
          icon: Layers,
        },
        {
          title: "Lớp Học",
          href: "/admin/academic/classes",
          icon: School,
        },
      ],
    },
    {
      title: "Quản Lý Người Dùng",
      icon: Users,
      isDropdown: true,
      isOpen: isUsersDropdownOpen,
      isActive: isUsersRouteActive,
      toggle: () => {
        if (isCollapsed) setIsCollapsed(false);
        setIsUsersDropdownOpen((prev) => !prev);
      },
      subItems: [
        {
          title: "Người Dùng",
          href: "/admin/users",
          icon: User,
        },
        {
          title: "Sinh Viên",
          href: "/admin/users/students",
          icon: GraduationCap,
        },
        {
          title: "Giảng Viên",
          href: "/admin/users/lecturers",
          icon: UserCheck,
        },
      ],
    },
  ];

  return (
    <div className="flex h-screen bg-slate-100 dark:bg-slate-950 overflow-hidden">
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
          <Link href="/admin/academic" className="flex items-center gap-3 group min-w-0">
            <div className="w-9 h-9 rounded-xl bg-gradient-to-br from-indigo-500 to-purple-600 flex items-center justify-center shadow-md shadow-indigo-500/20 group-hover:scale-105 transition-transform shrink-0">
              <Shield className="w-5 h-5 text-white" />
            </div>
            {!isCollapsed && (
              <div className="min-w-0 truncate">
                <span className="text-lg font-bold text-white tracking-tight">SAGA</span>
                <span className="block text-[10px] font-semibold uppercase tracking-widest text-indigo-400">Admin Portal</span>
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
              Quản trị Hệ thống
            </p>
          )}

          {menuItems.map((item, idx) => {
            const Icon = item.icon;

            // Handle Dropdown Submenu Header
            if (item.isDropdown) {
              const isParentActive = item.isActive;
              return (
                <div key={idx} className="space-y-1">
                  <button
                    type="button"
                    onClick={item.toggle}
                    title={isCollapsed ? item.title : undefined}
                    className={`w-full flex items-center rounded-xl text-sm font-medium transition-all ${
                      isCollapsed ? "justify-center p-3" : "justify-between px-3.5 py-2.5"
                    } ${
                      isParentActive
                        ? "bg-indigo-600/20 text-indigo-300 font-semibold"
                        : "text-slate-400 hover:bg-slate-800 hover:text-slate-200"
                    }`}
                  >
                    <div className="flex items-center gap-3">
                      <Icon className={`w-5 h-5 shrink-0 ${isParentActive ? "text-indigo-400" : "text-slate-400"}`} />
                      {!isCollapsed && <span className="truncate">{item.title}</span>}
                    </div>
                    {!isCollapsed && (
                      <ChevronDown
                        className={`w-4 h-4 text-slate-400 transition-transform duration-200 ${
                          item.isOpen ? "rotate-180" : ""
                        }`}
                      />
                    )}
                  </button>

                  {/* Submenu Items list */}
                  {!isCollapsed && item.isOpen && item.subItems && (
                    <div className="pl-4 pr-1 py-1 space-y-1 border-l-2 border-slate-800 ml-5 transition-all">
                      {item.subItems.map((sub) => {
                        const isSubActive = pathname === sub.href;
                        const SubIcon = sub.icon;
                        return (
                          <Link
                            key={sub.href}
                            href={sub.href}
                            className={`flex items-center gap-2.5 px-3 py-2 rounded-lg text-xs font-medium transition-all ${
                              isSubActive
                                ? "bg-indigo-600 text-white font-semibold shadow-md shadow-indigo-600/30"
                                : "text-slate-400 hover:bg-slate-800/80 hover:text-slate-200"
                            }`}
                          >
                            <SubIcon className={`w-4 h-4 ${isSubActive ? "text-white" : "text-slate-400"}`} />
                            <span className="truncate">{sub.title}</span>
                          </Link>
                        );
                      })}
                    </div>
                  )}
                </div>
              );
            }

            // Normal Non-Dropdown Item
            const isActive = pathname === (item as any).href;
            return (
              <Link
                key={(item as any).href}
                href={(item as any).href}
                title={isCollapsed ? item.title : undefined}
                className={`flex items-center gap-3 rounded-xl text-sm font-medium transition-all ${
                  isCollapsed ? "justify-center p-3" : "px-3.5 py-2.5"
                } ${
                  isActive
                    ? "bg-indigo-600 text-white font-semibold shadow-lg shadow-indigo-600/30"
                    : "text-slate-400 hover:bg-slate-800 hover:text-slate-200"
                }`}
              >
                <Icon className={`w-5 h-5 shrink-0 ${isActive ? "text-white" : "text-slate-400"}`} />
                {!isCollapsed && <span className="truncate">{item.title}</span>}
              </Link>
            );
          })}
        </div>

        {/* User Profile Footer */}
        <div className="p-3 border-t border-slate-800">
          <div className={`flex items-center gap-3 rounded-xl ${isCollapsed ? "justify-center p-2" : "p-2 bg-slate-800/50"}`}>
            <button
              onClick={() => setIsProfileOpen(true)}
              className="flex items-center gap-3 min-w-0 text-left cursor-pointer hover:opacity-80 transition-opacity"
              title="Xem thông tin cá nhân"
            >
              {user?.picture ? (
                <img
                  src={user.picture}
                  alt={user.name || "User"}
                  className="w-9 h-9 rounded-full object-cover shrink-0 border border-slate-700"
                />
              ) : (
                <div className="w-9 h-9 rounded-full bg-indigo-600 text-white flex items-center justify-center font-bold text-sm shrink-0">
                  {user?.name?.charAt(0) || "A"}
                </div>
              )}

              {!isCollapsed && (
                <div className="min-w-0 flex-1">
                  <p className="text-xs font-bold text-white truncate">{user?.name || "Administrator"}</p>
                  <p className="text-[10px] text-slate-400 truncate">{user?.email || "admin@fpt.edu.vn"}</p>
                </div>
              )}
            </button>

            {!isCollapsed && (
              <div className="flex items-center gap-1 shrink-0 ml-auto">
                <ThemeToggle />
                <button
                  onClick={() => logout()}
                  title="Đăng xuất"
                  className="p-2 text-slate-400 hover:text-red-400 hover:bg-slate-800 rounded-lg transition-colors shrink-0"
                >
                  <LogOut className="w-5 h-5" />
                </button>
              </div>
            )}
          </div>
        </div>
      </aside>

      {/* Main Content Wrapper */}
      <div className="flex-1 flex flex-col min-w-0 overflow-hidden">
        {/* Top Header Bar for Mobile */}
        <header className="lg:hidden h-16 bg-white dark:bg-slate-900 border-b border-slate-200 dark:border-slate-800 px-4 flex items-center justify-between">
          <button
            onClick={() => setSidebarOpen(true)}
            className="p-2 text-slate-600 dark:text-slate-300 hover:bg-slate-100 dark:hover:bg-slate-800 rounded-lg"
          >
            <Menu className="w-6 h-6" />
          </button>
          <span className="font-bold text-slate-800 dark:text-white">SAGA Admin</span>
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
