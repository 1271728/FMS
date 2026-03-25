package com.example.fms.modules.pay.service.impl;

import com.example.fms.common.exception.BizException;
import com.example.fms.modules.pay.dto.PayCreateOrUpdateReq;
import com.example.fms.modules.pay.dto.PaymentVO;
import com.example.fms.modules.pay.mapper.PaymentMapper;
import com.example.fms.modules.pay.service.PayService;
import com.example.fms.modules.reimburse.dto.ReimburseEntity;
import com.example.fms.modules.reimburse.dto.ReimburseStatus;
import com.example.fms.modules.reimburse.mapper.ReimburseMapper;
import com.example.fms.modules.shared.support.UserSupport;
import com.example.fms.modules.workflow.dto.WfBizTypes;
import com.example.fms.modules.workflow.dto.WfNodeCodes;
import com.example.fms.modules.workflow.mapper.WfLogMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional(rollbackFor = Exception.class)
public class PayServiceImpl implements PayService {

    private final PaymentMapper paymentMapper;
    private final ReimburseMapper reimburseMapper;
    private final UserSupport userSupport;
    private final WfLogMapper wfLogMapper;

    public PayServiceImpl(PaymentMapper paymentMapper,
                          ReimburseMapper reimburseMapper,
                          UserSupport userSupport,
                          WfLogMapper wfLogMapper) {
        this.paymentMapper = paymentMapper;
        this.reimburseMapper = reimburseMapper;
        this.userSupport = userSupport;
        this.wfLogMapper = wfLogMapper;
    }

    @Override
    public void createOrUpdate(PayCreateOrUpdateReq req) {
        if (req == null || req.getReimbId() == null) throw BizException.badRequest("reimbId不能为空");
        UserSupport.CurrentUser cu = userSupport.currentUser();
        if (!(userSupport.hasFinanceRole(cu.getRoles()) || userSupport.hasAdminRole(cu.getRoles()))) {
            throw BizException.forbidden("仅财务或管理员可登记支付");
        }
        ReimburseEntity reimburse = reimburseMapper.selectEntityById(req.getReimbId());
        if (reimburse == null) throw BizException.notFound("报销单不存在");
        Integer status = reimburse.getStatus();
        if (status == null || (status != ReimburseStatus.WAIT_PAY && status != ReimburseStatus.DONE)) {
            throw BizException.conflict("当前报销单状态不支持支付登记");
        }
        String payMethod = nonBlank(req.getPayMethod(), "payMethod不能为空");
        BigDecimal payAmount = req.getPayAmount();
        if (payAmount == null || payAmount.compareTo(BigDecimal.ZERO) <= 0) throw BizException.badRequest("payAmount必须大于0");
        String paidAt = nonBlank(req.getPaidAt(), "paidAt不能为空");
        if (paymentMapper.countByReimbId(req.getReimbId()) > 0) {
            paymentMapper.updateByReimbId(req.getReimbId(), payMethod, blankToNull(req.getVoucherNo()), payAmount, paidAt, cu.getUser().getId());
            wfLogMapper.insert(WfBizTypes.REIMB, req.getReimbId(), WfNodeCodes.PAY_ARCHIVE, "更新支付登记", cu.getUser().getId(), composeComment(req));
        } else {
            paymentMapper.insert(req.getReimbId(), payMethod, blankToNull(req.getVoucherNo()), payAmount, paidAt, cu.getUser().getId());
            wfLogMapper.insert(WfBizTypes.REIMB, req.getReimbId(), WfNodeCodes.PAY_ARCHIVE, "支付登记", cu.getUser().getId(), composeComment(req));
        }
    }

    @Override
    public PaymentVO detailByReimbId(Long reimbId) {
        userSupport.currentUser();
        if (reimbId == null) throw BizException.badRequest("reimbId不能为空");
        return paymentMapper.selectByReimbId(reimbId);
    }

    private String composeComment(PayCreateOrUpdateReq req) {
        StringBuilder sb = new StringBuilder();
        sb.append("支付方式：").append(req.getPayMethod());
        if (req.getVoucherNo() != null && !req.getVoucherNo().trim().isEmpty()) sb.append("；凭证号：").append(req.getVoucherNo().trim());
        if (req.getPayAmount() != null) sb.append("；金额：").append(req.getPayAmount().toPlainString());
        if (req.getPaidAt() != null && !req.getPaidAt().trim().isEmpty()) sb.append("；支付时间：").append(req.getPaidAt().trim());
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
