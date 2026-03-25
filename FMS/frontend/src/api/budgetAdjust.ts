import { http } from './http';
import type { PageResult } from './project';
import type { WfLogVO } from './workflow';

export interface BudgetAdjustPageReq {
  pageNo: number;
  pageSize: number;
  keyword?: string;
  projectId?: number | null;
  status?: number | null;
  todoOnly?: boolean;
}

export interface BudgetAdjustLineReq {
  subjectId: number | null;
  deltaAmount: number | null;
  remark?: string;
}

export interface BudgetAdjustCreateReq {
  projectId: number | null;
  reason: string;
  lines: BudgetAdjustLineReq[];
}

export interface BudgetAdjustUpdateReq extends BudgetAdjustCreateReq {
  id: number;
}

export interface BudgetAdjustVO {
  id: number;
  adjustNo: string;
  projectId: number;
  projectCode?: string;
  projectName?: string;
  applicantId?: number;
  applicantName?: string;
  unitId?: number | null;
  unitName?: string;
  reason: string;
  totalDelta: number;
  status: number;
  submittedAt?: string;
  canEdit?: number;
  canSubmit?: number;
  canWithdraw?: number;
  canUnitAudit?: number;
  canFinanceAudit?: number;
}

export interface BudgetAdjustLineVO {
  id: number;
  adjustId: number;
  subjectId: number;
  subjectCode?: string;
  subjectName?: string;
  deltaAmount: number;
  remark?: string;
}

export interface BudgetAdjustDetailVO extends BudgetAdjustVO {
  createdAt?: string;
  updatedAt?: string;
  effectiveAt?: string;
  currentNode?: string;
  lastComment?: string;
  lines?: BudgetAdjustLineVO[];
  wfLogs?: WfLogVO[];
}

export function apiBudgetAdjustPage(data: BudgetAdjustPageReq) {
  return http.post<any, PageResult<BudgetAdjustVO>>('/budgetAdjust/page', data);
}

export function apiBudgetAdjustDetail(id: number) {
  return http.get<any, BudgetAdjustDetailVO>(`/budgetAdjust/detail?id=${id}`);
}

export function apiBudgetAdjustCreate(data: BudgetAdjustCreateReq) {
  return http.post<any, number>('/budgetAdjust/create', data);
}

export function apiBudgetAdjustUpdate(data: BudgetAdjustUpdateReq) {
  return http.post('/budgetAdjust/update', data);
}

export function apiBudgetAdjustDelete(id: number) {
  return http.post('/budgetAdjust/delete', { id });
}

export function apiBudgetAdjustSubmit(id: number) {
  return http.post('/budgetAdjust/submit', { id });
}

export function apiBudgetAdjustWithdraw(id: number) {
  return http.post('/budgetAdjust/withdraw', { id });
}
