import { axiosClient } from "@/lib/axios";
import { ApiResponse, PageData, UserItem, UserQueryParams } from "../types";

export const userAdminApi = {
  getUsers: async (params: UserQueryParams) => {
    const queryParams = new URLSearchParams();
    queryParams.append("page", (params.page ?? 0).toString());
    queryParams.append("size", (params.size ?? 10).toString());
    if (params.search && params.search.trim()) queryParams.append("search", params.search.trim());
    if (params.status && params.status.trim()) queryParams.append("status", params.status.trim());
    if (params.role && params.role.trim()) queryParams.append("role", params.role.trim());

    const { data } = await axiosClient.get<ApiResponse<PageData<UserItem>>>(
      `/admin/users?${queryParams.toString()}`
    );
    return data.data;
  },

  getStudents: async (params: UserQueryParams) => {
    const queryParams = new URLSearchParams();
    queryParams.append("page", (params.page ?? 0).toString());
    queryParams.append("size", (params.size ?? 10).toString());
    if (params.search && params.search.trim()) queryParams.append("search", params.search.trim());
    if (params.status && params.status.trim()) queryParams.append("status", params.status.trim());

    const { data } = await axiosClient.get<ApiResponse<PageData<UserItem>>>(
      `/admin/users/students?${queryParams.toString()}`
    );
    return data.data;
  },

  getLecturers: async (params: UserQueryParams) => {
    const queryParams = new URLSearchParams();
    queryParams.append("page", (params.page ?? 0).toString());
    queryParams.append("size", (params.size ?? 10).toString());
    if (params.search && params.search.trim()) queryParams.append("search", params.search.trim());
    if (params.status && params.status.trim()) queryParams.append("status", params.status.trim());

    const { data } = await axiosClient.get<ApiResponse<PageData<UserItem>>>(
      `/admin/users/lecturers?${queryParams.toString()}`
    );
    return data.data;
  },

  getAllLecturers: async () => {
    const { data } = await axiosClient.get<ApiResponse<UserItem[]>>(
      "/admin/users/lecturers/all"
    );
    return data.data;
  },

  updateUserStatus: async (userId: string, status: string) => {
    const { data } = await axiosClient.put<ApiResponse<UserItem>>(
      `/admin/users/${userId}/status?status=${encodeURIComponent(status)}`
    );
    return data.data;
  },
};
