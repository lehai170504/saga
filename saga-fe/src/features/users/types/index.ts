export interface UserItem {
  id: string;
  email: string;
  name: string;
  picture: string;
  role: string;
  status: string;
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

export interface PageData<T> {
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

export interface UserQueryParams {
  page?: number;
  size?: number;
  search?: string;
  status?: string;
  role?: string;
}
