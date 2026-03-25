package com.example.fms.modules.workflow.service.impl;

import com.example.fms.common.exception.BizException;
import com.example.fms.modules.shared.support.UserSupport;
import com.example.fms.modules.workflow.dto.WfActionReq;
import com.example.fms.modules.workflow.handler.WfBizHandler;
import com.example.fms.modules.workflow.mapper.WfLogMapper;
import com.example.fms.modules.workflow.mapper.WfTaskMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

class WfServiceImplTest {

    @Test
    void approve_shouldDispatchToMatchedHandler() {
        WfTaskMapper wfTaskMapper = Mockito.mock(WfTaskMapper.class);
        WfLogMapper wfLogMapper = Mockito.mock(WfLogMapper.class);
        UserSupport userSupport = Mockito.mock(UserSupport.class);
        AtomicBoolean approved = new AtomicBoolean(false);

        WfBizHandler reimburseHandler = new WfBizHandler() {
            @Override
            public String bizType() { return "REIMB"; }

            @Override
            public void approve(Long bizId, String nodeCode, String comment) {
                if (Long.valueOf(12L).equals(bizId) && "PI_AUDIT".equals(nodeCode)) approved.set(true);
            }

            @Override
            public void reject(Long bizId, String nodeCode, String comment) {}
        };

        WfServiceImpl service = new WfServiceImpl(wfTaskMapper, wfLogMapper, userSupport, Arrays.asList(reimburseHandler));

        WfActionReq req = new WfActionReq();
        req.setBizType("reimb");
        req.setBizId(12L);
        req.setNodeCode("pi_audit");
        req.setComment("ok");

        service.approve(req);
        Assertions.assertTrue(approved.get());
    }

    @Test
    void reject_shouldThrowWhenBizTypeUnsupported() {
        WfTaskMapper wfTaskMapper = Mockito.mock(WfTaskMapper.class);
        WfLogMapper wfLogMapper = Mockito.mock(WfLogMapper.class);
        UserSupport userSupport = Mockito.mock(UserSupport.class);
        WfServiceImpl service = new WfServiceImpl(wfTaskMapper, wfLogMapper, userSupport, Arrays.<WfBizHandler>asList());

        WfActionReq req = new WfActionReq();
        req.setBizType("UNKNOWN");
        req.setBizId(1L);
        req.setNodeCode("ANY");

        BizException ex = Assertions.assertThrows(BizException.class, () -> service.reject(req));
        Assertions.assertEquals(400, ex.getCode());
    }
}
