import { Header } from '@/components/Header';
import Link from 'next/link';

export default function LandingPage() {
  return (
    <div className="min-h-screen bg-slate-50 selection:bg-blue-200">
      <Header />
      
      <main>
        {/* Hero Section */}
        <section className="relative pt-24 pb-32 overflow-hidden">
          <div className="absolute inset-0 z-0">
            <div className="absolute -top-[30%] -right-[10%] w-[70%] h-[70%] rounded-full bg-blue-400/20 blur-3xl mix-blend-multiply" />
            <div className="absolute top-[20%] -left-[10%] w-[50%] h-[50%] rounded-full bg-indigo-400/20 blur-3xl mix-blend-multiply" />
          </div>

          <div className="container relative z-10 mx-auto px-4 text-center">
            <div className="inline-flex items-center gap-2 px-3 py-1.5 rounded-full bg-blue-50 border border-blue-100 text-blue-600 text-sm font-medium mb-8 animate-fade-in-up">
              <span className="relative flex h-2 w-2">
                <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-blue-400 opacity-75"></span>
                <span className="relative inline-flex rounded-full h-2 w-2 bg-blue-500"></span>
              </span>
              Hệ thống Đánh giá & Phân tích Đồ thị Sinh viên
            </div>
            
            <h1 className="text-5xl md:text-7xl font-extrabold text-slate-900 tracking-tight mb-8 leading-tight max-w-4xl mx-auto">
              Kiểm soát tiến độ với <br className="hidden md:block"/>
              <span className="bg-clip-text text-transparent bg-gradient-to-r from-blue-600 via-indigo-500 to-purple-600">Sức mạnh của Đồ thị</span>
            </h1>
            
            <p className="text-lg md:text-xl text-slate-600 mb-10 max-w-2xl mx-auto leading-relaxed">
              SAGA (Student Assessment & Graph Analytics) mang đến cái nhìn trực quan, 
              chuyên sâu về quá trình đóng góp của sinh viên thông qua Cytoscape.js và phân tích thời gian thực.
            </p>
            
            <div className="flex flex-col sm:flex-row items-center justify-center gap-4">
              <Link 
                href="/login"
                className="w-full sm:w-auto inline-flex items-center justify-center px-8 py-3.5 text-base font-semibold text-white transition-all bg-blue-600 border border-transparent rounded-full hover:bg-blue-700 hover:shadow-xl hover:shadow-blue-600/30 hover:-translate-y-0.5"
              >
                Bắt đầu ngay
              </Link>
            </div>
          </div>
        </section>

        <section id="features" className="py-20 bg-white">
          <div className="container mx-auto px-4">
            <div className="grid md:grid-cols-3 gap-8 max-w-5xl mx-auto">
              <div className="p-8 rounded-3xl bg-slate-50 border border-slate-100 transition-transform hover:-translate-y-1">
                <h3 className="text-xl font-bold text-slate-900 mb-3">Real-time Analytics</h3>
                <p className="text-slate-600 leading-relaxed">Đồng bộ trạng thái cập nhật qua Server-Sent Events (SSE). Không lo giật lag hay mất kết nối.</p>
              </div>
              <div className="p-8 rounded-3xl bg-slate-50 border border-slate-100 transition-transform hover:-translate-y-1">
                <h3 className="text-xl font-bold text-slate-900 mb-3">Interactive Graphs</h3>
                <p className="text-slate-600 leading-relaxed">Hiển thị sơ đồ node cực khủng với Cytoscape.js. Áp dụng Lazy-load và tối ưu hoá bộ nhớ đỉnh cao.</p>
              </div>
              <div className="p-8 rounded-3xl bg-slate-50 border border-slate-100 transition-transform hover:-translate-y-1">
                <h3 className="text-xl font-bold text-slate-900 mb-3">Enterprise Grade</h3>
                <p className="text-slate-600 leading-relaxed">Kiến trúc Feature-Sliced Design siêu sạch. Mọi module đều được phân tách rạch ròi, chống bloat code.</p>
              </div>
            </div>
          </div>
        </section>
      </main>
    </div>
  );
}
