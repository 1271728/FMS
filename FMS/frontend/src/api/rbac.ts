import { http } from "./http";

export interface RoleOption {
  id: number;
  roleCode: string;
  roleName: string;
}

export function apiRoles() {
  return http.get<any, RoleOption[]>("/rbac/roles");
}

export function apiAccessCodes() {
  return http.get<any, string[]>("/rbac/access-codes");
}
