import { http } from './http';

export function apiBudgetExecution(projectId: number) {
  return http.get<any, Array<Record<string, any>>>(`/report/budgetExecution?projectId=${projectId}`);
}

export function apiReimbStat(data: Record<string, any>) {
  return http.post<any, Record<string, any>>('/report/reimbStat', data);
}
