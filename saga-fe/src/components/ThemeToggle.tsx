"use client";

import { useTheme } from "next-themes";
import { useState, useEffect } from "react";
import { Sun, Moon } from "lucide-react";

interface ThemeToggleProps {
  className?: string;
  showText?: boolean;
}

export function ThemeToggle({ className = "", showText = false }: ThemeToggleProps) {
  const { theme, setTheme } = useTheme();
  const [mounted, setMounted] = useState(false);

  useEffect(() => {
    setMounted(true);
  }, []);

  if (!mounted) {
    return <div className={`w-9 h-9 rounded-2xl bg-slate-200/50 ${className}`} />;
  }

  const isDark = theme === "dark";

  return (
    <button
      onClick={() => setTheme(isDark ? "light" : "dark")}
      title={isDark ? "Chuyển sang chế độ Sáng" : "Chuyển sang chế độ Tối"}
      className={`relative inline-flex items-center justify-center gap-2 p-2 rounded-2xl transition-all duration-300 cursor-pointer border shadow-sm ${
        isDark
          ? "bg-slate-800/90 border-slate-700 text-amber-400 hover:bg-slate-700 hover:text-amber-300"
          : "bg-white/90 border-slate-200/80 text-indigo-600 hover:bg-slate-100 hover:text-indigo-700"
      } ${className}`}
    >
      {isDark ? (
        <Sun className="w-4 h-4 transition-transform duration-300 rotate-0 hover:rotate-90 text-amber-400" />
      ) : (
        <Moon className="w-4 h-4 transition-transform duration-300 rotate-0 hover:-rotate-12 text-indigo-600" />
      )}
      {showText && (
        <span className="text-xs font-semibold text-slate-300">
          {isDark ? "Giao diện Sáng" : "Giao diện Tối"}
        </span>
      )}
    </button>
  );
}
