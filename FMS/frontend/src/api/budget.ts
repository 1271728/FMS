import { http } from './http';

export interface BudgetSubjectNode {
  id: number;
  parentId?: number | null;
  code: string;
  name: string;
  levelNo?: number;
  sortNo?: number;
  enabled?: number;
  children?: BudgetSubjectNode[];
}

export interface ProjectBudgetVO {
  id: number;
  projectId: number;
  subjectId: number;
  subjectCode: string;
  subjectName: string;
  approvedAmount: number;
  usedAmount: number;
  frozenAmount: number;
  availableAmount: number;
}

export function apiBudgetSubjectTree() {
  return http.get<any, BudgetSubjectNode[]>('/budget/subject/tree');
}

export function apiProjectBudgetList(projectId: number) {
  return http.get<any, ProjectBudgetVO[]>(`/budget/project/list?projectId=${projectId}`);
}

export function apiBackfillAllProjectBudgets() {
  return http.post<any, { projectCount: number; insertedRows: number; message: string }>('/budget/project/backfill-all', {});
}
