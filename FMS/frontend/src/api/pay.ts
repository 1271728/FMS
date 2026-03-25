import { http } from './http';

export interface PayCreateOrUpdateReq {
  reimbId: number;
  payMethod: string;
  voucherNo?: string;
  payAmount: number;
  paidAt: string;
}

export interface PaymentVO {
  id: number;
  reimbId: number;
  payMethod: string;
  voucherNo?: string;
  payAmount: number;
  paidAt?: string;
  operatorName?: string;
  createdAt?: string;
  updatedAt?: string;
}

export function apiPayCreateOrUpdate(data: PayCreateOrUpdateReq) {
  return http.post('/pay/createOrUpdate', data);
}

export function apiPayDetail(reimbId: number) {
  return http.get<any, PaymentVO | null>(`/pay/detail?reimbId=${reimbId}`);
}
