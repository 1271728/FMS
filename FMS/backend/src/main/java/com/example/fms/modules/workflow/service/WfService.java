package com.example.fms.modules.workflow.service;

import com.example.fms.common.api.PageResult;
import com.example.fms.modules.workflow.dto.*;

import java.util.List;

public interface WfService {
    PageResult<WfTaskVO> todoPage(WfTodoPageReq req);
    PageResult<WfTaskVO> donePage(WfDonePageReq req);
    void approve(WfActionReq req);
    void reject(WfActionReq req);
    List<WfLogVO> logs(String bizType, Long bizId);
}
