import { GoogleLoginButton } from '@/features/auth/components/GoogleLoginButton';

export default function LoginPage() {
  return (
    <main className="flex min-h-screen items-center justify-center bg-gray-50 p-4">
      <div className="w-full max-w-md bg-white rounded-2xl shadow-xl overflow-hidden">
        <div className="p-8">
          <div className="text-center mb-10">
            <h1 className="text-3xl font-bold text-gray-900 mb-2">SAGA</h1>
            <p className="text-gray-500 text-sm">Student Assessment & Graph Analytics</p>
          </div>
          
          <div className="space-y-6">
            <div className="text-center">
              <h2 className="text-xl font-semibold text-gray-800 mb-2">Chào mừng trở lại</h2>
              <p className="text-gray-600 text-sm mb-8">Đăng nhập để tiếp tục</p>
            </div>
            
            <GoogleLoginButton />
            
            <div className="mt-8 text-center text-xs text-gray-500">
              Chỉ hỗ trợ tài khoản email @fpt.edu.vn hoặc @fe.edu.vn
            </div>
          </div>
        </div>
      </div>
    </main>
  );
}
