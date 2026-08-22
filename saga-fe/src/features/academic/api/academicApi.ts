import { axiosClient } from "@/lib/axios";
import {
  Semester,
  Subject,
  AcademicClass,
  Course,
  PageResponse,
  ApiResponse,
  CreateSemesterPayload,
  CreateSubjectPayload,
  CreateClassPayload,
} from "../types";

export const academicApi = {
  getSemesters: async (page = 0, size = 10, search?: string) => {
    const queryParams = new URLSearchParams();
    queryParams.append("page", page.toString());
    queryParams.append("size", size.toString());
    if (search && search.trim()) {
      queryParams.append("search", search.trim());
    }

    const { data } = await axiosClient.get<ApiResponse<PageResponse<Semester>>>(
      `/admin/academic/semesters?${queryParams.toString()}`
    );
    return data.data;
  },

  createSemester: async (payload: CreateSemesterPayload) => {
    const { data } = await axiosClient.post<ApiResponse<Semester>>("/admin/academic/semesters", payload);
    return data.data;
  },

  getSubjects: async (page = 0, size = 10, search?: string) => {
    const queryParams = new URLSearchParams();
    queryParams.append("page", page.toString());
    queryParams.append("size", size.toString());
    if (search && search.trim()) {
      queryParams.append("search", search.trim());
    }

    const { data } = await axiosClient.get<ApiResponse<PageResponse<Subject>>>(
      `/admin/academic/subjects?${queryParams.toString()}`
    );
    return data.data;
  },

  createSubject: async (payload: CreateSubjectPayload) => {
    const { data } = await axiosClient.post<ApiResponse<Subject>>("/admin/academic/subjects", payload);
    return data.data;
  },

  updateSubject: async (id: string, payload: CreateSubjectPayload) => {
    const { data } = await axiosClient.put<ApiResponse<Subject>>(`/admin/academic/subjects/${id}`, payload);
    return data.data;
  },

  deleteSubject: async (id: string) => {
    const { data } = await axiosClient.delete<ApiResponse<void>>(`/admin/academic/subjects/${id}`);
    return data.data;
  },

  getClasses: async (page = 0, size = 10, search?: string) => {
    const queryParams = new URLSearchParams();
    queryParams.append("page", page.toString());
    queryParams.append("size", size.toString());
    if (search && search.trim()) {
      queryParams.append("search", search.trim());
    }

    const { data } = await axiosClient.get<ApiResponse<PageResponse<AcademicClass>>>(
      `/admin/academic/classes?${queryParams.toString()}`
    );
    return data.data;
  },

  createClass: async (payload: CreateClassPayload) => {
    const { data } = await axiosClient.post<ApiResponse<AcademicClass>>("/admin/academic/classes", payload);
    return data.data;
  },

  updateClass: async (id: string, payload: CreateClassPayload) => {
    const { data } = await axiosClient.put<ApiResponse<AcademicClass>>(`/admin/academic/classes/${id}`, payload);
    return data.data;
  },

  deleteClass: async (id: string) => {
    const { data } = await axiosClient.delete<ApiResponse<void>>(`/admin/academic/classes/${id}`);
    return data.data;
  },
};
