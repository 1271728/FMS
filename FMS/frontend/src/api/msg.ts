import { http } from './http';
import type { PageResult } from './project';

export interface MsgVO {
  id: number;
  msgType?: string;
  title: string;
  content: string;
  relatedBizType?: string;
  relatedBizId?: number;
  isRead: number;
  readAt?: string;
  createdAt?: string;
}

export interface MsgPageReq {
  pageNo: number;
  pageSize: number;
  isRead?: number | null;
  msgType?: string;
}

export function apiMsgPage(data: MsgPageReq) {
  return http.post<any, PageResult<MsgVO>>('/msg/page', data);
}

export function apiMsgUnreadCount() {
  return http.get<any, number>('/msg/unreadCount');
}

export function apiMsgMarkRead(id: number) {
  return http.post('/msg/markRead', { id });
}

export function apiMsgMarkReadBatch(ids: number[]) {
  return http.post('/msg/markReadBatch', { ids });
}
