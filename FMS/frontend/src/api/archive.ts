import { http } from './http';

export interface ArchiveCreateReq {
  reimbId: number;
  archiveNo: string;
  archivePath?: string;
}

export interface ArchiveVO {
  id: number;
  reimbId: number;
  archiveNo: string;
  archivePath?: string;
  operatorName?: string;
  archivedAt?: string;
  createdAt?: string;
  updatedAt?: string;
}

export function apiArchiveCreate(data: ArchiveCreateReq) {
  return http.post('/archive/create', data);
}

export function apiArchiveDetail(reimbId: number) {
  return http.get<any, ArchiveVO | null>(`/archive/detail?reimbId=${reimbId}`);
}
