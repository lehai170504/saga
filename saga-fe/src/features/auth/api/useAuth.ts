import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { authApi } from './authApi';
import { useRouter } from 'next/navigation';
import { toast } from 'sonner';

export const useAuth = () => {
  const queryClient = useQueryClient();
  const router = useRouter();

  const { data: user, isLoading: isUserLoading } = useQuery({
    queryKey: ['me'],
    queryFn: () => authApi.getMe(),
    retry: false,
    enabled: typeof window !== 'undefined' && !!localStorage.getItem('accessToken'),
  });

  const loginMutation = useMutation({
    mutationFn: (idToken: string) => authApi.loginWithGoogle(idToken),
    onSuccess: async (response) => {
      if (response.data.data.accessToken) {
        localStorage.setItem('accessToken', response.data.data.accessToken);
        queryClient.invalidateQueries({ queryKey: ['me'] });
        toast.success('Đăng nhập thành công!');
        router.push('/');
      }
    },
    onError: () => {
      toast.error('Đăng nhập thất bại!');
    }
  });

  const logoutMutation = useMutation({
    mutationFn: authApi.logout,
    onSuccess: () => {
      localStorage.removeItem('accessToken');
      queryClient.setQueryData(['me'], null);
      toast.success('Đã đăng xuất!');
      router.push('/');
    },
    onError: () => {
      localStorage.removeItem('accessToken');
      queryClient.setQueryData(['me'], null);
      router.push('/');
    }
  });

  return {
    user,
    isUserLoading,
    loginWithGoogle: loginMutation.mutateAsync,
    isLoginLoading: loginMutation.isPending,
    logout: logoutMutation.mutate,
  };
};
