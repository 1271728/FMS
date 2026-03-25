package com.example.fms.modules.msg.service;

import com.example.fms.common.api.PageResult;
import com.example.fms.modules.msg.dto.MsgMarkReadBatchReq;
import com.example.fms.modules.msg.dto.MsgMarkReadReq;
import com.example.fms.modules.msg.dto.MsgPageReq;
import com.example.fms.modules.msg.dto.MsgVO;

public interface MsgService {
    PageResult<MsgVO> page(MsgPageReq req);
    int unreadCount();
    void markRead(MsgMarkReadReq req);
    void markReadBatch(MsgMarkReadBatchReq req);
}
