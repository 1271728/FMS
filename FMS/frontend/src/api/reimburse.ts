import { http } from './http';
import type { PageResult } from './project';
import type { ArchiveVO } from './archive';
import type { PaymentVO } from './pay';

export interface ReimburseAttachmentReq {
  fileCategory?: string;
  originalName?: string;
  storageName?: string;
  fileUrl: string;
  fileSize?: number | null;
}

export interface ReimburseItemReq {
  subjectId: number | null;
  itemName: string;
  expenseDate?: string;
  amount?: number | null;
  baseAmount?: number | null;
  travelDays?: number | null;
  subsidyPerDay?: number | null;
  subsidyAmount?: number | null;
  remark?: string;
}

export interface ReimburseCreateReq {
  projectId: number | null;
  title: string;
  description?: string;
  items: ReimburseItemReq[];
  attachments?: ReimburseAttachmentReq[];
}

export interface ReimburseUpdateReq extends ReimburseCreateReq {
  id: number;
}

export interface ReimbursePageReq {
  pageNo: number;
  pageSize: number;
  keyword?: string;
  status?: number | null;
  projectId?: number | null;
  todoOnly?: boolean;
}

export interface ReimburseVO {
  id: number;
  reimburseNo: string;
  projectId: number;
  projectCode?: string;
  projectName?: string;
  applicantUserId?: number;
  applicantName?: string;
  unitId?: number | null;
  unitName?: string;
  title: string;
  totalAmount: number;
  status: number;
  itemCount?: number;
  submittedAt?: string;
  canEdit?: number;
  canSubmit?: number;
  canLeaderAudit?: number;
  canUnitAudit?: number;
  canFinanceAudit?: number;
  canWithdraw?: number;
}

export interface ReimburseItemVO {
  id: number;
  reimburseId: number;
  subjectId: number;
  subjectCode?: string;
  subjectName?: string;
  itemName: string;
  expenseDate?: string;
  amount: number;
  baseAmount?: number | null;
  travelDays?: number | null;
  subsidyPerDay?: number | null;
  subsidyAmount?: number | null;
  remark?: string;
}

export interface ReimburseAuditLogVO {
  id: number;
  action: string;
  fromStatus?: number | null;
  toStatus?: number | null;
  operatorName?: string;
  comment?: string;
  createdAt?: string;
}

export interface ReimburseDetailVO extends ReimburseVO {
  description?: string;
  createdAt?: string;
  updatedAt?: string;
  unitAuditorName?: string;
  unitAuditAt?: string;
  unitAuditComment?: string;
  financeAuditorName?: string;
  financeAuditAt?: string;
  financeAuditComment?: string;
  items?: ReimburseItemVO[];
  auditLogs?: ReimburseAuditLogVO[];
  attachments?: ReimburseAttachmentReq[];
  payment?: PaymentVO | null;
  archive?: ArchiveVO | null;
}

export function apiReimbursePage(data: ReimbursePageReq) {
  return http.post<any, PageResult<ReimburseVO>>('/reimburse/page', data);
}

export function apiReimburseDetail(id: number) {
  return http.get<any, ReimburseDetailVO>(`/reimburse/detail?id=${id}`);
}

export function apiReimburseCreate(data: ReimburseCreateReq) {
  return http.post('/reimburse/create', data);
}

export function apiReimburseUpdate(data: ReimburseUpdateReq) {
  return http.post('/reimburse/update', data);
}

export function apiReimburseSubmit(id: number) {
  return http.post('/reimburse/submit', { id });
}

export function apiReimburseAudit(reimburseId: number, action: 'pass' | 'reject', comment?: string) {
  return http.post('/reimburse/audit', { reimburseId, action, comment });
}

export function apiReimburseWithdraw(id: number) {
  return http.post('/reimburse/withdraw', { id });
}

export async function apiReimburseUpload(file: File, fileCategory: string) {
  const form = new FormData();
  form.append('file', file);
  form.append('fileCategory', fileCategory);
  return http.post<any, ReimburseAttachmentReq>('/reimburse/upload', form, {
    headers: { 'Content-Type': 'multipart/form-data' },
  });
}
