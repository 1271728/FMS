// src/api/rbac.ts
import { http } from "./http";   // 关键：不用 "@/api/http"，避免别名链式问题

export interface RoleOption {
  id: number;
  roleCode: string;
  roleName: string;
}

export function apiRoles() {
  return http.get<any, RoleOption[]>("/rbac/roles");
}