import { http } from "./http";

export interface LoginReq {
  username: string;
  password: string;
}

export interface LoginResp {
  tokenValue: string;
  user: MeResp;
}

export interface ChangePasswordReq {
  oldPassword: string;
  newPassword: string;
}

export interface MeResp {
  id: number;
  username: string;
  realName: string;
  unitId: number;
  unitName: string;
  roles: string[];
  perms?: string[];
  phone?: string;
  email?: string;
  status?: number;
  lastLoginAt?: string;
}

export function apiLogin(data: LoginReq) {
  return http.post<any, LoginResp>("/auth/login", data);
}

export function apiMe() {
  return http.get<any, MeResp>("/auth/me");
}

export function apiLogout() {
  return http.post<any, void>("/auth/logout");
}

export function apiChangePassword(data: ChangePasswordReq) {
  return http.post<any, void>("/auth/changePassword", data);
}
