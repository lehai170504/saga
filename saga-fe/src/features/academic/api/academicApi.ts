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
  CreateCoursePayload,
  AddStudentToCoursePayload,
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

  getCourses: async (page = 0, size = 10, search?: string) => {
    const queryParams = new URLSearchParams();
    queryParams.append("page", page.toString());
    queryParams.append("size", size.toString());
    if (search && search.trim()) {
      queryParams.append("search", search.trim());
    }

    const { data } = await axiosClient.get<ApiResponse<PageResponse<Course>>>(
      `/admin/academic/courses?${queryParams.toString()}`
    );
    return data.data;
  },

  createCourse: async (payload: CreateCoursePayload) => {
    const { data } = await axiosClient.post<ApiResponse<Course>>("/admin/academic/courses", payload);
    return data.data;
  },

  downloadRosterTemplate: async (courseId: string) => {
    const response = await axiosClient.get(
      `/admin/academic/courses/${courseId}/roster-template`,
      {
        responseType: "blob",
      }
    );

    const contentType =
      typeof response.headers["content-type"] === "string"
        ? response.headers["content-type"]
        : "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    const blob = new Blob([response.data], { type: contentType });
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement("a");
    link.href = url;
    link.setAttribute("download", `Student_Roster_Template_${courseId}.xlsx`);
    document.body.appendChild(link);
    link.click();
    link.remove();
    window.URL.revokeObjectURL(url);
  },

  addStudentToCourse: async (courseId: string, payload: AddStudentToCoursePayload) => {
    const { data } = await axiosClient.post<ApiResponse<void>>(
      `/admin/academic/courses/${courseId}/students`,
      payload
    );
    return data.data;
  },

  importRoster: async (courseId: string, file: File) => {
    const formData = new FormData();
    formData.append("file", file);

    const { data } = await axiosClient.post<ApiResponse<void>>(
      `/admin/academic/courses/${courseId}/import-roster`,
      formData,
      {
        headers: {
          "Content-Type": "multipart/form-data",
        },
      }
    );
    return data.data;
  },
};
