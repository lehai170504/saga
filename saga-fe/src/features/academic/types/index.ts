
export interface Semester {
  id: string;
  name: string;
  startDate: string;
  endDate: string;
}

export interface Subject {
  id: string;
  code: string;
  name: string;
  credits: number;
}

export interface Course {
  id: string;
  courseCode: string;
  subjectId: string;
  semesterId: string;
  instructorId: string;
  capacity: number;
}

export interface PageResponse<T> {
  content: T[];
  pageable: {
    pageNumber: number;
    pageSize: number;
  };
  totalElements: number;
  totalPages: number;
}
