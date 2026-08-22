import { Header } from "@/components/Header";
import Link from "next/link";
import { Sparkles, ArrowRight, Activity, Network, ShieldCheck } from "lucide-react";

export default function LandingPage() {
  return (
    <div className="min-h-screen bg-slate-50 dark:bg-slate-950 text-slate-900 dark:text-slate-100 selection:bg-blue-200 dark:selection:bg-blue-900 transition-colors duration-300">
      <Header />

      <main>
        {/* Hero Section */}
        <section className="relative pt-24 md:pt-32 pb-32 overflow-hidden">
          <div className="absolute inset-0 z-0">
            <div className="absolute -top-[30%] -right-[10%] w-[70%] h-[70%] rounded-full bg-blue-500/15 blur-3xl mix-blend-multiply dark:mix-blend-screen pointer-events-none" />
            <div className="absolute top-[20%] -left-[10%] w-[50%] h-[50%] rounded-full bg-indigo-500/15 blur-3xl mix-blend-multiply dark:mix-blend-screen pointer-events-none" />
          </div>

          <div className="container relative z-10 mx-auto px-4 text-center">
            <div className="inline-flex items-center gap-2 px-4 py-2 rounded-full bg-white/80 dark:bg-slate-900/80 border border-blue-200/80 dark:border-blue-800/40 shadow-md shadow-blue-500/5 text-blue-700 dark:text-blue-300 text-xs md:text-sm font-bold mb-8 animate-fade-in-up backdrop-blur-md">
              <span className="relative flex h-2 w-2">
                <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-blue-400 opacity-75"></span>
                <span className="relative inline-flex rounded-full h-2 w-2 bg-blue-600"></span>
              </span>
              <Sparkles className="w-4 h-4 text-blue-600 dark:text-blue-400" />
              SAGA 2.0 • System Student Assessment & Graph Analytics
            </div>

            <h1 className="text-4xl md:text-6xl lg:text-7xl font-black text-slate-900 dark:text-white tracking-tight mb-8 leading-tight max-w-5xl mx-auto">
              Đánh giá khối lượng công việc <br className="hidden md:block" />
              <span className="bg-clip-text text-transparent bg-gradient-to-r from-blue-600 via-indigo-600 to-purple-600 dark:from-blue-400 dark:via-indigo-400 dark:to-purple-400">
                Bằng Đồ thị Tương tác 3D
              </span>
            </h1>

            <p className="text-base md:text-xl text-slate-600 dark:text-slate-300 mb-10 max-w-2xl mx-auto leading-relaxed font-medium">
              Nền tảng đo lường hiệu suất làm việc nhóm sinh viên, phân tích lịch sử commit GitHub và Jira bằng thuật toán phân tích đồ thị Cytoscape.js thời gian thực.
            </p>

            <div className="flex flex-col sm:flex-row items-center justify-center gap-4">
              <Link
                href="/login"
                className="w-full sm:w-auto inline-flex items-center justify-center gap-2 px-8 py-4 text-base font-bold text-white transition-all bg-gradient-to-r from-blue-600 to-indigo-600 hover:from-blue-700 hover:to-indigo-700 rounded-full shadow-xl shadow-blue-600/25 hover:shadow-blue-600/40 hover:-translate-y-0.5"
              >
                Khám phá hệ thống ngay
                <ArrowRight className="w-5 h-5" />
              </Link>
            </div>
          </div>
        </section>

        {/* Feature Cards Section */}
        <section id="features" className="py-20 bg-white dark:bg-slate-900/60 relative border-t border-slate-100 dark:border-slate-800/80 transition-colors">
          <div className="container mx-auto px-4">
            <div className="text-center max-w-xl mx-auto mb-16 space-y-2">
              <span className="text-xs font-extrabold uppercase tracking-widest text-blue-600 dark:text-blue-400">Tính năng cốt lõi</span>
              <h2 className="text-3xl font-extrabold text-slate-900 dark:text-white tracking-tight">Tối ưu cho Đào tạo & Làm việc nhóm</h2>
            </div>

            <div className="grid md:grid-cols-3 gap-8 max-w-6xl mx-auto">
              <div className="p-8 rounded-3xl bg-white dark:bg-slate-800/90 border border-slate-200/80 dark:border-slate-700/80 hover:border-blue-200 dark:hover:border-blue-500/50 shadow-md dark:shadow-none hover:shadow-xl hover:shadow-blue-500/10 transition-all hover:-translate-y-1 group">
                <div className="w-12 h-12 rounded-2xl bg-blue-100 dark:bg-blue-950/80 text-blue-600 dark:text-blue-400 flex items-center justify-center font-bold mb-6 group-hover:scale-110 transition-transform">
                  <Activity className="w-6 h-6" />
                </div>
                <h3 className="text-xl font-bold text-slate-900 dark:text-white mb-3">Real-time Analytics</h3>
                <p className="text-slate-600 dark:text-slate-300 leading-relaxed text-sm">
                  Đồng bộ dữ liệu đóng góp công việc liên tục qua luồng Server-Sent Events (SSE). Đảm bảo kết quả chấm điểm luôn mới nhất.
                </p>
              </div>

              <div className="p-8 rounded-3xl bg-white dark:bg-slate-800/90 border border-slate-200/80 dark:border-slate-700/80 hover:border-indigo-200 dark:hover:border-indigo-500/50 shadow-md dark:shadow-none hover:shadow-xl hover:shadow-indigo-500/10 transition-all hover:-translate-y-1 group">
                <div className="w-12 h-12 rounded-2xl bg-indigo-100 dark:bg-indigo-950/80 text-indigo-600 dark:text-indigo-400 flex items-center justify-center font-bold mb-6 group-hover:scale-110 transition-transform">
                  <Network className="w-6 h-6" />
                </div>
                <h3 className="text-xl font-bold text-slate-900 dark:text-white mb-3">Interactive Graphs</h3>
                <p className="text-slate-600 dark:text-slate-300 leading-relaxed text-sm">
                  Trực quan hóa mạng lưới liên kết công việc bằng Cytoscape.js. Tự động phát hiện sinh viên làm gánh team hoặc sinh viên không đóng góp.
                </p>
              </div>

              <div className="p-8 rounded-3xl bg-white dark:bg-slate-800/90 border border-slate-200/80 dark:border-slate-700/80 hover:border-purple-200 dark:hover:border-purple-500/50 shadow-md dark:shadow-none hover:shadow-xl hover:shadow-purple-500/10 transition-all hover:-translate-y-1 group">
                <div className="w-12 h-12 rounded-2xl bg-purple-100 dark:bg-purple-950/80 text-purple-600 dark:text-purple-400 flex items-center justify-center font-bold mb-6 group-hover:scale-110 transition-transform">
                  <ShieldCheck className="w-6 h-6" />
                </div>
                <h3 className="text-xl font-bold text-slate-900 dark:text-white mb-3">Enterprise Security</h3>
                <p className="text-slate-600 dark:text-slate-300 leading-relaxed text-sm">
                  Tích hợp OAuth 2.0 Jira & GitHub, bảo mật JWT Token tự động làm mới (Auto Refresh Token) chống gián đoạn thao tác.
                </p>
              </div>
            </div>
          </div>
        </section>
      </main>
    </div>
  );
}
