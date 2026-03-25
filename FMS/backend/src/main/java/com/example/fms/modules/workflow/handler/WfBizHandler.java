package com.example.fms.modules.workflow.handler;

public interface WfBizHandler {

    String bizType();

    void approve(Long bizId, String nodeCode, String comment);

    void reject(Long bizId, String nodeCode, String comment);
}
