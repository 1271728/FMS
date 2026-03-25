package com.example.fms.modules.reimburse.service;

import com.example.fms.common.api.PageResult;
import com.example.fms.modules.reimburse.dto.*;

public interface ReimburseService {
    PageResult<ReimburseVO> page(ReimbursePageReq req);
    ReimburseDetailVO detail(Long id);
    Long create(ReimburseCreateReq req);
    void update(ReimburseUpdateReq req);
    void submit(ReimburseSubmitReq req);
    void withdraw(ReimburseSubmitReq req);
    void audit(ReimburseAuditReq req);
    void workflowApprove(Long bizId, String nodeCode, String comment);
    void workflowReject(Long bizId, String nodeCode, String comment);
}
