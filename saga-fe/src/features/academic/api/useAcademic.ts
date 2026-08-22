import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { academicApi } from "./academicApi";
import { CreateSemesterPayload, CreateSubjectPayload, CreateClassPayload } from "../types";

export const useSemesters = (page: number, size: number) => {
  return useQuery({
    queryKey: ["semesters", page, size],
    queryFn: () => academicApi.getSemesters(page, size),
  });
};

export const useCreateSemester = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (payload: CreateSemesterPayload) => academicApi.createSemester(payload),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["semesters"] });
    },
  });
};

export const useSubjects = (page: number, size: number, search?: string) => {
  return useQuery({
    queryKey: ["subjects", page, size, search],
    queryFn: () => academicApi.getSubjects(page, size, search),
  });
};

export const useCreateSubject = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (payload: CreateSubjectPayload) => academicApi.createSubject(payload),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["subjects"] });
    },
  });
};

export const useUpdateSubject = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ id, payload }: { id: string; payload: CreateSubjectPayload }) =>
      academicApi.updateSubject(id, payload),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["subjects"] });
    },
  });
};

export const useDeleteSubject = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (id: string) => academicApi.deleteSubject(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["subjects"] });
    },
  });
};

export const useClasses = (page: number, size: number, search?: string) => {
  return useQuery({
    queryKey: ["classes", page, size, search],
    queryFn: () => academicApi.getClasses(page, size, search),
  });
};

export const useCreateClass = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (payload: CreateClassPayload) => academicApi.createClass(payload),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["classes"] });
    },
  });
};

export const useUpdateClass = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ id, payload }: { id: string; payload: CreateClassPayload }) =>
      academicApi.updateClass(id, payload),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["classes"] });
    },
  });
};

export const useDeleteClass = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (id: string) => academicApi.deleteClass(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["classes"] });
    },
  });
};
