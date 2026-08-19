import { axiosClient } from '@/lib/axios';

export interface AuthResponse {
  accessToken: string;
  role: string;
}

export interface UserProfileDTO {
  email: string;
  name: string;
  picture: string;
  role?: string;
}

export interface CsrfTokenResponse {
  token: string;
  headerName: string;
}

export const authApi = {
  loginWithGoogle: (idToken: string) => {
    return axiosClient.post<{ data: AuthResponse }>('/auth/login', { idToken });
  },

  getMe: () => {
    return axiosClient.get<{ data: UserProfileDTO }>('/auth/me').then(res => res.data.data);
  },

  getCsrfToken: () => {
    return axiosClient.get<{ data: CsrfTokenResponse }>('/auth/csrf').then(res => res.data.data);
  },

  logout: () => {
    return axiosClient.post('/auth/logout');
  }
};