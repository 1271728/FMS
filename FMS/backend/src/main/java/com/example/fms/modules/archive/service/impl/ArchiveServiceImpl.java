package com.example.fms.modules.archive.service.impl;

import com.example.fms.common.exception.BizException;
import com.example.fms.modules.archive.dto.ArchiveCreateReq;
import com.example.fms.modules.archive.dto.ArchiveVO;
import com.example.fms.modules.archive.mapper.ArchiveMapper;
import com.example.fms.modules.archive.service.ArchiveService;
import com.example.fms.modules.pay.mapper.PaymentMapper;
import com.example.fms.modules.reimburse.dto.ReimburseEntity;
import com.example.fms.modules.reimburse.dto.ReimburseStatus;
import com.example.fms.modules.reimburse.mapper.ReimburseMapper;
import com.example.fms.modules.shared.support.UserSupport;
import com.example.fms.modules.workflow.dto.WfBizTypes;
import com.example.fms.modules.workflow.dto.WfNodeCodes;
import com.example.fms.modules.workflow.mapper.MsgInboxMapper;
import com.example.fms.modules.workflow.mapper.WfLogMapper;
import com.example.fms.modules.workflow.mapper.WfTaskMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class ArchiveServiceImpl implements ArchiveService {

    private final ArchiveMapper archiveMapper;
    private final PaymentMapper paymentMapper;
    private final ReimburseMapper reimburseMapper;
    private final UserSupport userSupport;
    private final WfTaskMapper wfTaskMapper;
    private final WfLogMapper wfLogMapper;
    private final MsgInboxMapper msgInboxMapper;

    public ArchiveServiceImpl(ArchiveMapper archiveMapper,
                              PaymentMapper paymentMapper,
                              ReimburseMapper reimburseMapper,
                              UserSupport userSupport,
                              WfTaskMapper wfTaskMapper,
                              WfLogMapper wfLogMapper,
                              MsgInboxMapper msgInboxMapper) {
        this.archiveMapper = archiveMapper;
        this.paymentMapper = paymentMapper;
        this.reimburseMapper = reimburseMapper;
        this.userSupport = userSupport;
        this.wfTaskMapper = wfTaskMapper;
        this.wfLogMapper = wfLogMapper;
        this.msgInboxMapper = msgInboxMapper;
    }

    @Override
    public void create(ArchiveCreateReq req) {
        if (req == null || req.getReimbId() == null) throw BizException.badRequest("reimbId不能为空");
        UserSupport.CurrentUser cu = userSupport.currentUser();
        if (!(userSupport.hasFinanceRole(cu.getRoles()) || userSupport.hasAdminRole(cu.getRoles()))) {
            throw BizException.forbidden("仅财务或管理员可执行归档");
        }
        ReimburseEntity reimburse = reimburseMapper.selectEntityById(req.getReimbId());
        if (reimburse == null) throw BizException.notFound("报销单不存在");
        if (reimburse.getStatus() == null || reimburse.getStatus() != ReimburseStatus.WAIT_PAY) {
            throw BizException.conflict("当前报销单状态不支持归档");
        }
        if (paymentMapper.countByReimbId(req.getReimbId()) <= 0) {
            throw BizException.conflict("请先完成支付登记再归档");
        }
        if (archiveMapper.countByReimbId(req.getReimbId()) > 0) {
            throw BizException.conflict("该报销单已归档");
        }
        String archiveNo = nonBlank(req.getArchiveNo(), "archiveNo不能为空");
        archiveMapper.insert(req.getReimbId(), archiveNo, blankToNull(req.getArchivePath()), cu.getUser().getId());
        wfTaskMapper.markDone(WfBizTypes.REIMB, req.getReimbId(), WfNodeCodes.PAY_ARCHIVE, "ARCHIVE", cu.getUser().getId());
        reimburseMapper.updateStatus(req.getReimbId(), ReimburseStatus.DONE, WfNodeCodes.DONE, archiveNo);
        wfLogMapper.insert(WfBizTypes.REIMB, req.getReimbId(), WfNodeCodes.DONE, "归档完成", cu.getUser().getId(), composeComment(req));
        msgInboxMapper.insert(reimburse.getApplicantUserId(), "DONE", "报销单已完成", "报销单" + reimburse.getReimburseNo() + " 已完成支付归档。", WfBizTypes.REIMB, req.getReimbId());
    }

    @Override
    public ArchiveVO detailByReimbId(Long reimbId) {
        userSupport.currentUser();
        if (reimbId == null) throw BizException.badRequest("reimbId不能为空");
        return archiveMapper.selectByReimbId(reimbId);
    }

    private String composeComment(ArchiveCreateReq req) {
        StringBuilder sb = new StringBuilder("档案编号：").append(req.getArchiveNo().trim());
        if (req.getArchivePath() != null && !req.getArchivePath().trim().isEmpty()) sb.append("；档案路径：").append(req.getArchivePath().trim());
        return sb.toString();
    }

    private String nonBlank(String v, String msg) {
        if (v == null || v.trim().isEmpty()) throw BizException.badRequest(msg);
        return v.trim();
    }

    private String blankToNull(String v) {
        return v == null || v.trim().isEmpty() ? null : v.trim();
    }
}
