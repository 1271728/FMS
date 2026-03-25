import { http } from "./http";

export interface UserPageReq {
  pageNo: number;
  pageSize: number;
  unitId?: number | null;
  keyword?: string;
  status?: number | null;
}

export interface UserVO {
  id: number;
  username: string;
  realName: string;
  phone?: string;
  email?: string;
  unitId?: number | null;
  unitName?: string;
  status: number;
  lastLoginAt?: string;
  roles: string[];
}

export interface PageResult<T> {
  records: T[];
  total: number;
  pageNo: number;
  pageSize: number;
}

export interface UserCreateReq {
  username: string;
  password: string;
  realName: string;
  phone?: string;
  email?: string;
  unitId?: number | null;
  status?: number;
  roleCodes: string[];
}

export interface UserUpdateReq {
  id: number;
  realName: string;
  phone?: string;
  email?: string;
  unitId?: number | null;
  status?: number;
}

export function apiUserPage(data: UserPageReq) {
  return http.post<any, PageResult<UserVO>>("/user/page", data);
}

export function apiUserCreate(data: UserCreateReq) {
  return http.post("/user/create", data);
}

export function apiUserUpdate(data: UserUpdateReq) {
  return http.post("/user/update", data);
}

export function apiUserDisable(userId: number) {
  return http.post("/user/disable", { userId });
}

export function apiUserEnable(userId: number) {
  return http.post("/user/enable", { userId });
}

export function apiUserResetPassword(userId: number, newPassword = "123456") {
  return http.post("/user/resetPassword", { userId, newPassword });
}

export function apiUserSetRoles(userId: number, roleCodes: string[]) {
  return http.post("/user/setRoles", { userId, roleCodes });
}
