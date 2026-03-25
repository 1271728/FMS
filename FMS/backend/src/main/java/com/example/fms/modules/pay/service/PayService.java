package com.example.fms.modules.pay.service;

import com.example.fms.modules.pay.dto.PayCreateOrUpdateReq;
import com.example.fms.modules.pay.dto.PaymentVO;

public interface PayService {
    void createOrUpdate(PayCreateOrUpdateReq req);
    PaymentVO detailByReimbId(Long reimbId);
}
