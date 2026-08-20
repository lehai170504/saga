
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { academicApi } from './academicApi';

export const useSemesters = (page: number, size: number) => {
  return useQuery({
    queryKey: ['semesters', page, size],
    queryFn: () => academicApi.getSemesters(page, size)
  });
};

export const useSubjects = (page: number, size: number) => {
  return useQuery({
    queryKey: ['subjects', page, size],
    queryFn: () => academicApi.getSubjects(page, size)
  });
};
