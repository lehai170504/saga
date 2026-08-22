import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { authApi, LoginLocalDTO } from './authApi';
import { useRouter } from 'next/navigation';
import { toast } from 'sonner';

const parseJwtRole = (token: string): string | undefined => {
  try {
    const base64Url = token.split('.')[1];
    if (!base64Url) return undefined;
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const jsonPayload = decodeURIComponent(
      atob(base64)
        .split('')
        .map((c) => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
        .join('')
    );
    const parsed = JSON.parse(jsonPayload);
    return parsed.role;
  } catch {
    return undefined;
  }
};

export const useAuth = () => {
  const queryClient = useQueryClient();
  const router = useRouter();

  const { data: user, isLoading: isUserLoading } = useQuery({
    queryKey: ['me'],
    queryFn: async () => {
      const userData = await authApi.getMe();
      if (userData && !userData.role) {
        const token = typeof window !== 'undefined' ? localStorage.getItem('accessToken') : null;
        if (token) {
          const role = parseJwtRole(token);
          if (role) {
            userData.role = role;
          }
        }
      }
      return userData;
    },
    retry: false,
    enabled: typeof window !== 'undefined' && !!localStorage.getItem('accessToken'),
  });

  const handleRedirect = (role?: string) => {
    const normalizedRole = role?.toUpperCase();
    if (normalizedRole === 'ADMIN') {
      router.push('/admin/academic');
    } else if (normalizedRole === 'LECTURER') {
      router.push('/lecturer/courses/default');
    } else {
      router.push('/');
    }
  };

  const loginMutation = useMutation({
    mutationFn: (idToken: string) => authApi.loginWithGoogle(idToken),
    onSuccess: async (response) => {
      const data = response.data?.data;
      if (data?.accessToken) {
        localStorage.setItem('accessToken', data.accessToken);
        if (data.refreshToken) {
          localStorage.setItem('refreshToken', data.refreshToken);
        }
        const userRole = data.role || parseJwtRole(data.accessToken);
        if (data.user) {
          queryClient.setQueryData(['me'], { ...data.user, role: userRole });
        }
        queryClient.invalidateQueries({ queryKey: ['me'] });
        toast.success('Đăng nhập thành công!');
        handleRedirect(userRole);
      }
    },
    onError: () => {
      toast.error('Đăng nhập thất bại!');
    }
  });

  const loginLocalMutation = useMutation({
    mutationFn: (data: LoginLocalDTO) => authApi.loginLocal(data),
    onSuccess: async (response) => {
      const data = response.data?.data;
      if (data?.accessToken) {
        localStorage.setItem('accessToken', data.accessToken);
        if (data.refreshToken) {
          localStorage.setItem('refreshToken', data.refreshToken);
        }
        const userRole = data.role || parseJwtRole(data.accessToken);
        if (data.user) {
          queryClient.setQueryData(['me'], { ...data.user, role: userRole });
        }
        queryClient.invalidateQueries({ queryKey: ['me'] });
        toast.success('Đăng nhập thành công!');
        handleRedirect(userRole);
      }
    },
    onError: (error: any) => {
      const message = error.response?.data?.message || 'Đăng nhập thất bại!';
      toast.error(message);
    }
  });

  const logoutMutation = useMutation({
    mutationFn: authApi.logout,
    onSuccess: () => {
      localStorage.removeItem('accessToken');
      localStorage.removeItem('refreshToken');
      queryClient.setQueryData(['me'], null);
      toast.success('Đã đăng xuất!');
      router.push('/');
    },
    onError: () => {
      localStorage.removeItem('accessToken');
      localStorage.removeItem('refreshToken');
      queryClient.setQueryData(['me'], null);
      router.push('/');
    }
  });

  return {
    user,
    isUserLoading,
    loginWithGoogle: loginMutation.mutateAsync,
    isLoginLoading: loginMutation.isPending,
    loginLocal: loginLocalMutation.mutateAsync,
    isLoginLocalLoading: loginLocalMutation.isPending,
    logout: logoutMutation.mutate,
  };
};
