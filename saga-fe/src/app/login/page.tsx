import { GoogleLoginButton } from "@/features/auth/components/GoogleLoginButton";
import { LocalLoginForm } from "@/features/auth/components/LocalLoginForm";
import { Shield, Sparkles, ArrowLeft } from "lucide-react";
import Link from "next/link";

export default function LoginPage() {
  return (
    <main className="flex min-h-screen items-center justify-center bg-slate-50 dark:bg-slate-950 text-slate-900 dark:text-slate-100 p-4 relative overflow-hidden selection:bg-indigo-500 selection:text-white transition-colors duration-300">
      {/* Back to Home Button */}
      <Link
        href="/"
        className="absolute top-6 left-6 z-20 inline-flex items-center gap-2 px-4 py-2.5 rounded-2xl bg-slate-900/5 dark:bg-white/10 hover:bg-slate-900/10 dark:hover:bg-white/20 text-slate-800 dark:text-white text-xs font-bold backdrop-blur-md border border-slate-200/60 dark:border-white/10 shadow-sm transition-all hover:-translate-x-1"
      >
        <ArrowLeft className="w-4 h-4" />
        <span>Quay lại Trang chủ</span>
      </Link>

      {/* Ambient Decorative Glow Halos */}
      <div className="absolute -top-32 -left-32 w-96 h-96 rounded-full bg-indigo-600/15 dark:bg-indigo-600/20 blur-3xl pointer-events-none" />
      <div className="absolute -bottom-32 -right-32 w-96 h-96 rounded-full bg-blue-600/15 dark:bg-blue-600/20 blur-3xl pointer-events-none" />

      {/* Main Login Card */}
      <div className="w-full max-w-md bg-white dark:bg-slate-900 backdrop-blur-xl rounded-3xl shadow-xl dark:shadow-2xl shadow-slate-900/5 dark:shadow-slate-950/50 border border-slate-200/80 dark:border-slate-800 overflow-hidden relative z-10 transition-colors">
        <div className="p-8 md:p-10">
          {/* Header Brand */}
          <div className="text-center mb-8 space-y-2">
            <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-indigo-50 dark:bg-indigo-950/80 border border-indigo-100 dark:border-indigo-800/60 text-indigo-600 dark:text-indigo-300 text-xs font-bold mb-2">
              <Sparkles className="w-3.5 h-3.5" />
              SAGA Portal Authorization
            </div>
            <div className="flex items-center justify-center gap-3">
              <div className="w-10 h-10 rounded-2xl bg-gradient-to-br from-indigo-600 to-purple-600 text-white flex items-center justify-center shadow-lg shadow-indigo-600/30">
                <Shield className="w-6 h-6" />
              </div>
              <h1 className="text-3xl font-black text-slate-900 dark:text-white tracking-tight">SAGA</h1>
            </div>
            <p className="text-slate-500 dark:text-slate-400 text-xs font-medium">Student Assessment & Graph Analytics</p>
          </div>

          <div className="space-y-6">
            <GoogleLoginButton />

            <div className="relative my-6">
              <div className="absolute inset-0 flex items-center">
                <span className="w-full border-t border-slate-200 dark:border-slate-800" />
              </div>
              <div className="relative flex justify-center text-[11px] uppercase tracking-wider font-bold">
                <span className="bg-white dark:bg-slate-900 px-3 text-slate-400 dark:text-slate-500">Hoặc</span>
              </div>
            </div>

            <LocalLoginForm />

            <div className="pt-2 text-center text-xs text-slate-400 dark:text-slate-500 font-medium">
              Chỉ hỗ trợ tài khoản email <span className="font-semibold text-slate-600 dark:text-slate-300">@fpt.edu.vn</span> hoặc <span className="font-semibold text-slate-600 dark:text-slate-300">@fe.edu.vn</span>
            </div>
          </div>
        </div>
      </div>
    </main>
  );
}
