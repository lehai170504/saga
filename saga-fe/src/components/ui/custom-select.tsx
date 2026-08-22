"use client";

import * as React from "react";
import { ChevronDown, Check } from "lucide-react";
import { cn } from "@/lib/utils";

export interface CustomSelectOption {
  value: string;
  label: string;
  sublabel?: string;
}

interface CustomSelectProps {
  options: CustomSelectOption[];
  value: string;
  onChange: (value: string) => void;
  placeholder?: string;
  icon?: React.ReactNode;
  disabled?: boolean;
  className?: string;
}

export function CustomSelect({
  options,
  value,
  onChange,
  placeholder = "-- Chọn --",
  icon,
  disabled = false,
  className,
}: CustomSelectProps) {
  const [isOpen, setIsOpen] = React.useState(false);
  const containerRef = React.useRef<HTMLDivElement>(null);

  const selectedOption = options.find((opt) => opt.value === value);

  React.useEffect(() => {
    function handleClickOutside(event: MouseEvent) {
      if (containerRef.current && !containerRef.current.contains(event.target as Node)) {
        setIsOpen(false);
      }
    }
    function handleKeyDown(event: KeyboardEvent) {
      if (event.key === "Escape") {
        setIsOpen(false);
      }
    }

    if (isOpen) {
      document.addEventListener("mousedown", handleClickOutside);
      document.addEventListener("keydown", handleKeyDown);
    }
    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
      document.removeEventListener("keydown", handleKeyDown);
    };
  }, [isOpen]);

  return (
    <div ref={containerRef} className={cn("relative w-full text-left", className)}>
      {/* Trigger Button */}
      <button
        type="button"
        disabled={disabled}
        onClick={() => setIsOpen((prev) => !prev)}
        className={cn(
          "w-full flex items-center justify-between pl-11 pr-11 py-3 text-sm rounded-full transition-all duration-200 outline-none cursor-pointer border shadow-2xs font-medium relative group",
          isOpen
            ? "bg-white dark:bg-slate-900 border-indigo-500 ring-4 ring-indigo-500/10 text-slate-900 dark:text-white"
            : "bg-slate-50/90 dark:bg-slate-800/90 border-slate-200 dark:border-slate-700 text-slate-900 dark:text-slate-100 hover:border-slate-300 dark:hover:border-slate-600",
          disabled && "opacity-50 cursor-not-allowed"
        )}
      >
        {/* Left Icon */}
        {icon && (
          <span className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400 group-hover:text-slate-600 dark:group-hover:text-slate-200 transition-colors pointer-events-none">
            {icon}
          </span>
        )}

        {/* Selected Value Text */}
        <span className={cn("truncate", !selectedOption && "text-slate-400 dark:text-slate-500")}>
          {selectedOption ? selectedOption.label : placeholder}
        </span>

        {/* Right Arrow Icon */}
        <ChevronDown
          className={cn(
            "w-4 h-4 absolute right-4 top-1/2 -translate-y-1/2 text-slate-400 transition-transform duration-300 pointer-events-none",
            isOpen && "rotate-180 text-indigo-500"
          )}
        />
      </button>

      {/* Dropdown Popup List */}
      {isOpen && (
        <div className="absolute left-0 right-0 top-full mt-2 z-50 p-2 bg-white/95 dark:bg-slate-900/95 backdrop-blur-xl border border-slate-200/90 dark:border-slate-800 rounded-3xl shadow-2xl shadow-indigo-500/10 max-h-60 overflow-y-auto space-y-1 scrollbar-thin scrollbar-thumb-slate-200 dark:scrollbar-thumb-slate-800 animate-in fade-in-0 zoom-in-95 duration-150">
          {options.length === 0 ? (
            <div className="px-4 py-3 text-xs text-center text-slate-400 dark:text-slate-500">
              Không có dữ liệu
            </div>
          ) : (
            options.map((option) => {
              const isSelected = option.value === value;
              return (
                <div
                  key={option.value}
                  onClick={() => {
                    onChange(option.value);
                    setIsOpen(false);
                  }}
                  className={cn(
                    "flex items-center justify-between px-4 py-2.5 rounded-2xl text-sm font-medium transition-all duration-150 cursor-pointer select-none",
                    isSelected
                      ? "bg-indigo-50 dark:bg-indigo-950/70 text-indigo-600 dark:text-indigo-400 font-bold border border-indigo-100 dark:border-indigo-800/40"
                      : "text-slate-700 dark:text-slate-200 hover:bg-slate-100/80 dark:hover:bg-slate-800/80 hover:text-slate-900 dark:hover:text-white"
                  )}
                >
                  <div className="flex flex-col min-w-0 pr-2">
                    <span className="truncate">{option.label}</span>
                    {option.sublabel && (
                      <span className="text-[11px] font-normal text-slate-400 dark:text-slate-500 truncate">
                        {option.sublabel}
                      </span>
                    )}
                  </div>

                  {isSelected && (
                    <Check className="w-4 h-4 text-indigo-600 dark:text-indigo-400 shrink-0 ml-2" />
                  )}
                </div>
              );
            })
          )}
        </div>
      )}
    </div>
  );
}
