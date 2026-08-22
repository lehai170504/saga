import { axiosClient } from '@/lib/axios';

export interface AuthResponse {
  accessToken: string;
  refreshToken?: string;
  role: string;
  user?: UserProfileDTO;
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

export interface LoginLocalDTO {
  email: string;
  password: string;
}

export const authApi = {
  loginWithGoogle: (idToken: string) => {
    return axiosClient.post<{ data: AuthResponse }>('/auth/login', { idToken });
  },

  loginLocal: (data: LoginLocalDTO) => {
    return axiosClient.post<{ data: AuthResponse }>('/auth/login-local', data);
  },

  refreshToken: (token: string) => {
    return axiosClient.post<{ data: AuthResponse }>('/auth/refresh', null, {
      params: { token }
    });
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