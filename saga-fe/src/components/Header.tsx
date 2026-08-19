"use client";

import Link from 'next/link';
import { useState, useEffect } from 'react';
import { useAuth } from '@/features/auth/api/useAuth';
import { ProfileModal } from '@/features/auth/components/ProfileModal';

export function Header() {
  const { user, isUserLoading, logout } = useAuth();
  const [isProfileOpen, setIsProfileOpen] = useState(false);
  const [mounted, setMounted] = useState(false);
  useEffect(() => setMounted(true), []);

  return (
    <header className="sticky top-0 z-50 w-full border-b border-gray-200/50 bg-white/70 backdrop-blur-xl">
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
          <Link href="#features" className="text-sm font-semibold text-slate-600 hover:text-blue-600 transition-colors">Tính Năng</Link>
          <Link href="#analytics" className="text-sm font-semibold text-slate-600 hover:text-blue-600 transition-colors">Đồ Thị</Link>
        </nav>

        <div className="flex items-center gap-4">
          {(!mounted || isUserLoading) ? (
            <div className="w-8 h-8 rounded-full bg-slate-200 animate-pulse"></div>
          ) : user ? (
            <div className="flex items-center gap-4">
              <button onClick={() => setIsProfileOpen(true)} className="flex items-center gap-2 hover:opacity-80 transition-opacity text-left">
                {user.picture ? (
                  <img src={user.picture} alt={user.name} className="w-8 h-8 rounded-full border border-gray-200" />
                ) : (
                  <div className="w-8 h-8 rounded-full bg-blue-100 flex items-center justify-center text-blue-600 font-bold">
                    {user.name?.charAt(0) || user.email?.charAt(0) || 'U'}
                  </div>
                )}
                <span className="text-sm font-medium text-slate-700 hidden sm:block">{user.name || user.email}</span>
              </button>
              <button
                onClick={() => logout()}
                className="text-sm font-semibold text-red-600 hover:text-red-700 transition-colors px-3 py-1.5 rounded-md hover:bg-red-50"
              >
                Đăng Xuất
              </button>
            </div>
          ) : (
            <Link
              href="/login"
              className="inline-flex items-center justify-center px-6 py-2.5 text-sm font-semibold text-white transition-all bg-slate-900 border border-transparent rounded-full hover:bg-slate-800 hover:scale-105 active:scale-95 shadow-lg shadow-slate-900/20"
            >
              Đăng Nhập
            </Link>
          )}
        </div>
      </div>
      <ProfileModal isOpen={isProfileOpen} onOpenChange={setIsProfileOpen} />
    </header>
  );
}
