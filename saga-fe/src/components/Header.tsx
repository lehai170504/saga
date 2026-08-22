"use client";

import Link from 'next/link';
import { useState, useEffect } from 'react';
import { useAuth } from '@/features/auth/api/useAuth';
import { ProfileModal } from '@/features/auth/components/ProfileModal';
import {
  DropdownMenu,
  DropdownMenuTrigger,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuSeparator,
} from '@/components/ui/dropdown-menu';
import { User, Settings, LogOut, ChevronDown, LayoutDashboard } from 'lucide-react';
import { ThemeToggle } from '@/components/ThemeToggle';

export function Header() {
  const { user, isUserLoading, logout } = useAuth();
  const [isProfileOpen, setIsProfileOpen] = useState(false);
  const [profileTab, setProfileTab] = useState<'info' | 'settings'>('info');
  const [mounted, setMounted] = useState(false);
  useEffect(() => setMounted(true), []);

  const openProfileModal = (tab: 'info' | 'settings') => {
    setProfileTab(tab);
    setIsProfileOpen(true);
  };

  return (
    <header className="sticky top-0 z-50 w-full border-b border-gray-200/50 dark:border-slate-800 bg-white/70 dark:bg-slate-900/80 backdrop-blur-xl transition-colors">
      <div className="container mx-auto px-4 h-16 flex items-center justify-between">
        <div className="flex items-center gap-2">
          <Link href="/" className="flex items-center gap-2 group">
            <div className="w-9 h-9 rounded-xl bg-gradient-to-br from-blue-600 to-indigo-600 flex items-center justify-center shadow-md shadow-blue-500/20 group-hover:shadow-blue-500/40 transition-all duration-300">
              <span className="text-white font-bold text-lg leading-none">S</span>
            </div>
            <span className="text-xl font-bold bg-clip-text text-transparent bg-gradient-to-r from-blue-700 to-indigo-700 tracking-tight">
              SAGA
            </span>
          </Link>
        </div>

        <nav className="hidden md:flex items-center gap-8">
          <Link href="#features" className="text-sm font-semibold text-slate-600 dark:text-slate-300 hover:text-blue-600 transition-colors">Tính Năng</Link>
          <Link href="#analytics" className="text-sm font-semibold text-slate-600 dark:text-slate-300 hover:text-blue-600 transition-colors">Đồ Thị</Link>
        </nav>

        <div className="flex items-center gap-3">
          <ThemeToggle />
          {(!mounted || isUserLoading) ? (
            <div className="w-9 h-9 rounded-full bg-slate-200 animate-pulse"></div>
          ) : user ? (
            <DropdownMenu>
              <DropdownMenuTrigger className="flex items-center gap-2.5 p-1.5 pl-2 pr-3 rounded-full hover:bg-slate-100/80 dark:hover:bg-slate-800/80 border border-slate-200/60 dark:border-slate-700/60 transition-all outline-none focus-visible:ring-2 focus-visible:ring-blue-500 cursor-pointer">
                {user.picture ? (
                  <img src={user.picture} alt={user.name || 'User'} className="w-8 h-8 rounded-full object-cover border border-slate-200 dark:border-slate-700 shadow-sm" />
                ) : (
                  <div className="w-8 h-8 rounded-full bg-blue-100 dark:bg-blue-950 text-blue-600 dark:text-blue-400 font-bold text-sm shadow-sm flex items-center justify-center">
                    {user.name?.charAt(0) || user.email?.charAt(0) || 'U'}
                  </div>
                )}
                <div className="hidden sm:flex flex-col text-left">
                  <span className="text-xs font-semibold text-slate-800 dark:text-slate-200 max-w-[120px] truncate leading-tight">
                    {user.name || user.email}
                  </span>
                  {user.role && (
                    <span className="text-[10px] font-medium text-blue-600 dark:text-blue-400 leading-tight">
                      {user.role === 'LECTURER' ? 'Giảng viên' : user.role === 'ADMIN' ? 'Quản trị viên' : user.role === 'STUDENT' ? 'Sinh viên' : user.role}
                    </span>
                  )}
                </div>
                <ChevronDown className="w-4 h-4 text-slate-400 dark:text-slate-500" />
              </DropdownMenuTrigger>

              <DropdownMenuContent align="end" className="w-64">
                <div className="px-3 py-2.5 flex items-center gap-3">
                  {user.picture ? (
                    <img src={user.picture} alt={user.name || 'User'} className="w-10 h-10 rounded-full object-cover border border-slate-200 dark:border-slate-700" />
                  ) : (
                    <div className="w-10 h-10 rounded-full bg-blue-100 dark:bg-blue-950 text-blue-600 dark:text-blue-400 font-bold text-base flex items-center justify-center">
                      {user.name?.charAt(0) || user.email?.charAt(0) || 'U'}
                    </div>
                  )}
                  <div className="flex flex-col min-w-0">
                    <p className="text-sm font-semibold text-slate-900 dark:text-slate-100 truncate">{user.name || 'User'}</p>
                    <p className="text-xs text-slate-500 dark:text-slate-400 truncate">{user.email}</p>
                    {user.role && (
                      <span className="mt-1 text-[10px] font-bold px-2 py-0.5 rounded-full bg-blue-50 dark:bg-blue-950/60 text-blue-600 dark:text-blue-400 border border-blue-100 dark:border-blue-800/40 uppercase tracking-wider w-fit">
                        {user.role === 'LECTURER' ? 'Giảng viên' : user.role === 'ADMIN' ? 'Quản trị viên' : user.role === 'STUDENT' ? 'Sinh viên' : user.role}
                      </span>
                    )}
                  </div>
                </div>

                <DropdownMenuSeparator />

                <DropdownMenuItem onClick={() => openProfileModal('info')}>
                  <User className="w-4 h-4 text-slate-500 dark:text-slate-400" />
                  <span>Thông tin cá nhân</span>
                </DropdownMenuItem>

                {user.role !== 'ADMIN' && user.role !== 'LECTURER' && (
                  <DropdownMenuItem onClick={() => openProfileModal('settings')}>
                    <Settings className="w-4 h-4 text-slate-500 dark:text-slate-400" />
                    <span>Cài đặt & Tích hợp</span>
                  </DropdownMenuItem>
                )}

                {user.role === 'ADMIN' && (
                  <DropdownMenuItem onClick={() => window.location.href = '/admin/academic'}>
                    <LayoutDashboard className="w-4 h-4 text-slate-500 dark:text-slate-400" />
                    <span>Trang Quản Trị</span>
                  </DropdownMenuItem>
                )}

                <DropdownMenuSeparator />

                <DropdownMenuItem variant="destructive" onClick={() => logout()}>
                  <LogOut className="w-4 h-4" />
                  <span>Đăng Xuất</span>
                </DropdownMenuItem>
              </DropdownMenuContent>
            </DropdownMenu>
          ) : (
            <Link
              href="/login"
              className="inline-flex items-center justify-center px-6 py-2.5 text-sm font-bold transition-all duration-300 rounded-full bg-slate-900 text-white hover:bg-slate-800 dark:bg-indigo-600 dark:text-white dark:hover:bg-indigo-500 hover:scale-105 active:scale-95 shadow-lg shadow-slate-900/10 dark:shadow-indigo-600/30 border border-transparent cursor-pointer"
            >
              Đăng Nhập
            </Link>
          )}
        </div>
      </div>
      <ProfileModal isOpen={isProfileOpen} onOpenChange={setIsProfileOpen} defaultTab={profileTab} />
    </header>
  );
}
