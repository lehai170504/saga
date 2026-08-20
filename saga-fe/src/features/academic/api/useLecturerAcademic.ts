
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { axiosClient } from '@/lib/axios';

export const useCourseRoster = (courseId: string) => {
  return useQuery({
    queryKey: ['roster', courseId],
    queryFn: async () => {
      // Stub
      return [];
    }
  });
};

export const useImportRoster = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async ({ courseId, file }: { courseId: string; file: File }) => {
      const formData = new FormData();
      formData.append('file', file);
      const { data } = await axiosClient.post(`/academic/courses/${courseId}/roster/import`, formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      });
      return data;
    },
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: ['roster', variables.courseId] });
    }
  });
};
