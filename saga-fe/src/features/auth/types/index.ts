export interface User {
  id: string;
  email: string;
  name: string;
  picture: string;
  role: string;
  status: string;
}

export interface AuthResponse {
  accessToken: string;
  user: User;
}
