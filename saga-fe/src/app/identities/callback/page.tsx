'use client';

import { useIdentity } from '@/features/identity/api/useIdentity';
import { useRouter, useSearchParams } from 'next/navigation';
import { useEffect, useRef } from 'react';
import { toast } from 'sonner';

export default function IdentityCallbackPage() {
  const searchParams = useSearchParams();
  const router = useRouter();
  const { linkGithub, linkJira } = useIdentity();
  const processed = useRef(false);

  useEffect(() => {
    const code = searchParams.get('code');
    const state = searchParams.get('state');

    if (processed.current) return;
    processed.current = true;

    if (!code || !state) {
      toast('Không nhận được mã xác thực.');
      router.push('/');
      return;
    }

    const processLink = async () => {
      try {
        if (state === 'github') {
          await linkGithub(code);
        } else if (state === 'jira') {
          await linkJira(code);
        }
        toast('Liên kết tài khoản thành công!');
      } catch (err) {
        console.error('Lỗi khi liên kết:', err);
        toast('Liên kết thất bại. Vui lòng thử lại.');
      } finally {
        router.push('/');
      }
    };

    processLink();
  }, [searchParams, linkGithub, linkJira, router]);

  return (
    <div className="flex flex-col items-center justify-center h-screen bg-gray-50">
      <div className="w-16 h-16 border-4 border-blue-500 border-t-transparent rounded-full animate-spin"></div>
      <p className="mt-4 text-gray-600 font-medium text-lg">Đang đồng bộ danh tính...</p>
      <p className="text-gray-400 text-sm mt-2">Vui lòng không đóng trang này</p>
    </div>
  );
}
