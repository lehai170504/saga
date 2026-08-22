import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { identityApi } from './identityApi';

export const useIdentity = () => {
  const queryClient = useQueryClient();

  const { data: identities, isLoading } = useQuery({
    queryKey: ['identities'],
    queryFn: () => identityApi.getMyIdentities().then(res => res.data.data),
    enabled: typeof window !== 'undefined' && !!localStorage.getItem('accessToken'),
  });

  const linkGithubMutation = useMutation({
    mutationFn: identityApi.linkGithub,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['identities'] });
    }
  });

  const unlinkMutation = useMutation({
    mutationFn: (provider: 'GITHUB' | 'JIRA') => identityApi.unlinkIdentity(provider),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['identities'] });
    }
  });

  const linkJiraMutation = useMutation({
    mutationFn: identityApi.linkJira,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['identities'] });
    }
  });

  return {
    identities,
    isLoading,
    linkGithub: linkGithubMutation.mutateAsync,
    isLinkingGithub: linkGithubMutation.isPending,
    linkJira: linkJiraMutation.mutateAsync,
    unlinkIdentity: unlinkMutation.mutateAsync,
    isUnlinking: unlinkMutation.isPending,
    isLinkingJira: linkJiraMutation.isPending,
  };
};
