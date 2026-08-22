export interface Semester {
  id: string;
  code: string;
  name: string;
  startDate: string;
  endDate: string;
  active?: boolean;
  isActive?: boolean;
}

export interface CreateSemesterPayload {
  code: string;
  name: string;
  startDate: string;
  endDate: string;
}

export interface Subject {
  id: string;
  subjectCode: string;
  subjectName: string;
  code?: string;
  name?: string;
}

export interface CreateSubjectPayload {
  subjectCode: string;
  subjectName: string;
}

export interface AcademicClass {
  id: string;
  classCode: string;
}

export interface CreateClassPayload {
  classCode: string;
}

export interface Course {
  id: string;
  semesterId?: string;
  semesterName?: string;
  subjectId?: string;
  subjectName?: string;
  classId?: string;
  classCode?: string;
  instructorId?: string;
  instructorName?: string;
  courseCode?: string;
  capacity?: number;
}

export interface CreateCoursePayload {
  semesterId: string;
  subjectId: string;
  classId: string;
  instructorId: string;
}

export interface AddStudentToCoursePayload {
  email: string;
}

export interface SortItem {
  direction?: string;
  nullHandling?: string;
  ascending?: boolean;
  property?: string;
  ignoreCase?: boolean;
}

export interface PageableInfo {
  pageNumber: number;
  pageSize: number;
  paged?: boolean;
  unpaged?: boolean;
  offset?: number;
  sort?: SortItem[];
}

export interface PageResponse<T> {
  totalPages: number;
  totalElements: number;
  pageable: PageableInfo;
  numberOfElements: number;
  first: boolean;
  last: boolean;
  size: number;
  content: T[];
  number: number;
  sort?: SortItem[];
  empty: boolean;
}

export interface ApiResponse<T> {
  status: number;
  message?: string;
  data: T;
}
