package com.example.fms.modules.workflow.handler;

import com.example.fms.modules.reimburse.service.ReimburseService;
import com.example.fms.modules.workflow.dto.WfBizTypes;
import org.springframework.stereotype.Component;

@Component
public class ReimburseWfBizHandler implements WfBizHandler {

    private final ReimburseService reimburseService;

    public ReimburseWfBizHandler(ReimburseService reimburseService) {
        this.reimburseService = reimburseService;
    }

    @Override
    public String bizType() {
        return WfBizTypes.REIMB;
    }

    @Override
    public void approve(Long bizId, String nodeCode, String comment) {
        reimburseService.workflowApprove(bizId, nodeCode, comment);
    }

    @Override
    public void reject(Long bizId, String nodeCode, String comment) {
        reimburseService.workflowReject(bizId, nodeCode, comment);
    }
}
