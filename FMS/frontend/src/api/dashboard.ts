import { http } from './http';

export interface DashboardCardItem {
  key: string;
  label: string;
  value: number | string;
}

export interface DashboardStatsResp {
  role: string;
  unreadCount: number;
  cards: DashboardCardItem[];
}

export function apiDashboardStats() {
  return http.get<any, DashboardStatsResp>('/dashboard/stats');
}
