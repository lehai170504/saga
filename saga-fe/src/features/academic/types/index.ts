export interface Semester {
  id: string;
  code: string;
  name: string;
  startDate: string;
  endDate: string;
  active: boolean;
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
  message: string;
  data: T;
}
