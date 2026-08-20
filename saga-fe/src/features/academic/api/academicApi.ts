
import { axiosClient } from '@/lib/axios';
import { Semester, Subject, Course, PageResponse } from '../types';

export const academicApi = {
  getSemesters: async (page = 0, size = 10) => {
    const { data } = await axiosClient.get<PageResponse<Semester>>(`/academic/semesters?page=${page}&size=${size}`);
    return data;
  },
  createSemester: async (payload: Omit<Semester, 'id'>) => {
    const { data } = await axiosClient.post<Semester>('/academic/semesters', payload);
    return data;
  },
  getSubjects: async (page = 0, size = 10) => {
    const { data } = await axiosClient.get<PageResponse<Subject>>(`/academic/subjects?page=${page}&size=${size}`);
    return data;
  }
};
