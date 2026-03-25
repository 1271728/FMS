import { http } from './http';
import type { PageResult } from './project';

export interface WfTodoPageReq {
  pageNo: number;
  pageSize: number;
  bizType?: string;
  nodeCode?: string;
  keyword?: string;
}

export interface WfDonePageReq extends WfTodoPageReq {}

export interface WfTaskVO {
  id: number;
  bizType: string;
  bizId: number;
  bizNo?: string;
  title?: string;
  projectName?: string;
  projectId?: number | null;
  applicantUserId?: number | null;
  applicantName?: string;
  unitId?: number | null;
  unitName?: string;
  nodeCode?: string;
  assigneeRoleCode?: string;
  taskStatus?: string;
  createdAt?: string;
  completedAt?: string;
}

export interface WfLogVO {
  id: number;
  bizType?: string;
  bizId?: number;
  nodeCode?: string;
  action?: string;
  actorUserId?: number | null;
  actorName?: string;
  comment?: string;
  createdAt?: string;
}

export interface WfActionReq {
  bizType: string;
  bizId: number;
  nodeCode: string;
  comment?: string;
}

export function apiWfTodoPage(data: WfTodoPageReq) {
  return http.post<any, PageResult<WfTaskVO>>('/wf/todoPage', data);
}

export function apiWfDonePage(data: WfDonePageReq) {
  return http.post<any, PageResult<WfTaskVO>>('/wf/donePage', data);
}

export function apiWfApprove(data: WfActionReq) {
  return http.post('/wf/approve', data);
}

export function apiWfReject(data: WfActionReq) {
  return http.post('/wf/reject', data);
}

export function apiWfLogs(bizType: string, bizId: number) {
  return http.get<any, WfLogVO[]>(`/wf/logs?bizType=${encodeURIComponent(bizType)}&bizId=${bizId}`);
}
