package com.example.fms.modules.msg.service.impl;

import com.example.fms.common.api.PageResult;
import com.example.fms.common.exception.BizException;
import com.example.fms.modules.msg.dto.MsgMarkReadBatchReq;
import com.example.fms.modules.msg.dto.MsgMarkReadReq;
import com.example.fms.modules.msg.dto.MsgPageReq;
import com.example.fms.modules.msg.dto.MsgVO;
import com.example.fms.modules.msg.mapper.MsgCenterMapper;
import com.example.fms.modules.msg.service.MsgService;
import com.example.fms.modules.shared.support.UserSupport;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MsgServiceImpl implements MsgService {

    private final MsgCenterMapper msgCenterMapper;
    private final UserSupport userSupport;

    public MsgServiceImpl(MsgCenterMapper msgCenterMapper, UserSupport userSupport) {
        this.msgCenterMapper = msgCenterMapper;
        this.userSupport = userSupport;
    }

    @Override
    public PageResult<MsgVO> page(MsgPageReq req) {
        UserSupport.CurrentUser cu = userSupport.currentUser();
        int pageNo = req == null || req.getPageNo() == null || req.getPageNo() < 1 ? 1 : req.getPageNo();
        int pageSize = req == null || req.getPageSize() == null || req.getPageSize() < 1 ? 10 : req.getPageSize();
        Integer isRead = req == null ? null : req.getIsRead();
        String msgType = req == null || req.getMsgType() == null ? null : req.getMsgType().trim();
        int offset = (pageNo - 1) * pageSize;
        long total = msgCenterMapper.countPage(cu.getUser().getId(), isRead, msgType);
        List<MsgVO> records = msgCenterMapper.selectPage(offset, pageSize, cu.getUser().getId(), isRead, msgType);
        return new PageResult<MsgVO>(records, total, pageNo, pageSize);
    }

    @Override
    public int unreadCount() {
        UserSupport.CurrentUser cu = userSupport.currentUser();
        return msgCenterMapper.countUnread(cu.getUser().getId());
    }

    @Override
    public void markRead(MsgMarkReadReq req) {
        if (req == null || req.getId() == null) throw BizException.badRequest("id不能为空");
        UserSupport.CurrentUser cu = userSupport.currentUser();
        msgCenterMapper.markRead(cu.getUser().getId(), req.getId());
    }

    @Override
    public void markReadBatch(MsgMarkReadBatchReq req) {
        if (req == null || req.getIds() == null || req.getIds().isEmpty()) throw BizException.badRequest("ids不能为空");
        UserSupport.CurrentUser cu = userSupport.currentUser();
        msgCenterMapper.markReadBatch(cu.getUser().getId(), req.getIds());
    }
}
