import { http } from "./http";

export interface ProjectPageReq {
  pageNo: number;
  pageSize: number;
  keyword?: string;
  status?: number | null;
  todoOnly?: boolean;
  viewType?: "lead" | "joined" | "all";
}

export interface ProjectVO {
  id: number;
  projectCode: string;
  projectName: string;
  projectType?: string;
  principalUserId?: number;
  principalName?: string;
  unitId?: number | null;
  unitName?: string;
  startDate?: string;
  endDate?: string;
  totalBudget?: number;
  status: number;
  memberCount?: number;
  canEdit?: number;
  canSubmit?: number;
  canManageMembers?: number;
  canUnitAudit?: number;
  canFinanceAudit?: number;
  canWithdraw?: number;
  submittedAt?: string;
}

export interface ProjectAuditLogVO {
  id: number;
  action: string;
  fromStatus?: number | null;
  toStatus?: number | null;
  operatorUserId?: number | null;
  operatorName?: string;
  comment?: string;
  createdAt?: string;
}

export interface ProjectDetailVO extends ProjectVO {
  description?: string;
  createdAt?: string;
  updatedAt?: string;
  unitAuditorName?: string;
  unitAuditAt?: string;
  unitAuditComment?: string;
  financeAuditorName?: string;
  financeAuditAt?: string;
  financeAuditComment?: string;
  auditLogs?: ProjectAuditLogVO[];
}

export interface ProjectMemberVO {
  userId: number;
  username: string;
  realName: string;
  unitId?: number | null;
  unitName?: string;
  memberRole?: string;
  joinedAt?: string;
}

export interface PageResult<T> {
  records: T[];
  total: number;
  pageNo: number;
  pageSize: number;
}

export interface ProjectBudgetLineReq {
  subjectId: number | null;
  approvedAmount: number | null;
}

export interface ProjectCreateReq {
  projectCode: string;
  projectName: string;
  projectType?: string;
  startDate?: string;
  endDate?: string;
  totalBudget?: number | null;
  description?: string;
  budgetLines?: ProjectBudgetLineReq[];
}

export interface ProjectUpdateReq extends ProjectCreateReq {
  id: number;
}

export function apiProjectPage(data: ProjectPageReq) {
  return http.post<any, PageResult<ProjectVO>>("/project/page", data);
}

export function apiProjectDetail(id: number) {
  return http.get<any, ProjectDetailVO>(`/project/detail?id=${id}`);
}

export function apiProjectCreate(data: ProjectCreateReq) {
  return http.post("/project/create", data);
}

export function apiProjectUpdate(data: ProjectUpdateReq) {
  return http.post("/project/update", data);
}

export function apiProjectDelete(id: number) {
  return http.post("/project/delete", { id });
}

export function apiProjectSubmit(id: number) {
  return http.post("/project/submit", { id });
}

export function apiProjectWithdraw(id: number) {
  return http.post("/project/withdraw", { id });
}

export function apiProjectAudit(projectId: number, action: "pass" | "reject", comment?: string) {
  return http.post("/project/audit", { projectId, action, comment });
}

export function apiProjectMembers(projectId: number) {
  return http.get<any, ProjectMemberVO[]>(`/project/members?projectId=${projectId}`);
}

export function apiProjectAddMember(projectId: number, username: string, memberRole?: string) {
  return http.post("/project/addMember", { projectId, username, memberRole });
}

export function apiProjectRemoveMember(projectId: number, userId: number) {
  return http.post("/project/removeMember", { projectId, userId });
}
