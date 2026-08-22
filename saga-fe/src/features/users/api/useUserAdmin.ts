import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { userAdminApi } from "./userAdminApi";
import { UserQueryParams } from "../types";

export const useAdminUsers = (params: UserQueryParams, options?: { enabled?: boolean }) => {
  return useQuery({
    queryKey: ["admin-users", params.page, params.size, params.search, params.status, params.role],
    queryFn: () => userAdminApi.getUsers(params),
    enabled: options?.enabled ?? true,
  });
};

export const useAdminStudents = (params: UserQueryParams, options?: { enabled?: boolean }) => {
  return useQuery({
    queryKey: ["admin-students", params.page, params.size, params.search, params.status],
    queryFn: () => userAdminApi.getStudents(params),
    enabled: options?.enabled ?? true,
  });
};

export const useAdminLecturers = (params: UserQueryParams, options?: { enabled?: boolean }) => {
  return useQuery({
    queryKey: ["admin-lecturers", params.page, params.size, params.search, params.status],
    queryFn: () => userAdminApi.getLecturers(params),
    enabled: options?.enabled ?? true,
  });
};

export const useUpdateUserStatus = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ userId, status }: { userId: string; status: string }) =>
      userAdminApi.updateUserStatus(userId, status),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["admin-users"] });
      queryClient.invalidateQueries({ queryKey: ["admin-students"] });
      queryClient.invalidateQueries({ queryKey: ["admin-lecturers"] });
    },
  });
};
