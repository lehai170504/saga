
'use client';

import { Button } from '@/components/ui/button';
import { axiosClient } from '@/lib/axios';
import { useRouter } from 'next/navigation';
import { toast } from 'sonner';

export const DevLoginBypass = () => {
  const router = useRouter();

  const devLogin = async (role: string) => {
    try {
      const { data } = await axiosClient.get(`/auth/dev-login?role=${role}`);
      if (data.data?.accessToken) {
        localStorage.setItem('accessToken', data.data.accessToken);
        toast.success(`Logged in as ${role}`);
        
        // Force reload to apply auth state globally if using contexts
        window.location.href = role === 'ADMIN' ? '/admin/academic' : '/lecturer/courses/default';
      }
    } catch (err) {
      toast.error('Dev login failed');
    }
  };

  return (
    <div className="mt-8 space-y-4 border-t pt-6 border-slate-100">
      <p className="text-sm text-center font-medium text-slate-500">Developer Bypass (Local Only)</p>
      <div className="grid grid-cols-2 gap-4">
        <Button 
          variant="outline" 
          className="w-full border-slate-200 hover:bg-slate-50 text-slate-700 font-medium h-12 rounded-xl"
          onClick={() => devLogin('ADMIN')}
        >
          Mock Admin
        </Button>
        <Button 
          variant="outline" 
          className="w-full border-slate-200 hover:bg-slate-50 text-slate-700 font-medium h-12 rounded-xl"
          onClick={() => devLogin('LECTURER')}
        >
          Mock Lecturer
        </Button>
      </div>
    </div>
  );
};
