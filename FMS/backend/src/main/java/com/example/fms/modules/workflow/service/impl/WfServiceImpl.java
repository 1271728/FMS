package com.example.fms.modules.workflow.service.impl;

import com.example.fms.common.api.PageResult;
import com.example.fms.common.exception.BizException;
import com.example.fms.modules.shared.support.UserSupport;
import com.example.fms.modules.workflow.dto.*;
import com.example.fms.modules.workflow.handler.WfBizHandler;
import com.example.fms.modules.workflow.mapper.WfLogMapper;
import com.example.fms.modules.workflow.mapper.WfTaskMapper;
import com.example.fms.modules.workflow.service.WfService;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class WfServiceImpl implements WfService {

    private final WfTaskMapper wfTaskMapper;
    private final WfLogMapper wfLogMapper;
    private final UserSupport userSupport;
    private final Map<String, WfBizHandler> bizHandlerMap;

    public WfServiceImpl(WfTaskMapper wfTaskMapper,
                         WfLogMapper wfLogMapper,
                         UserSupport userSupport,
                         List<WfBizHandler> bizHandlers) {
        this.wfTaskMapper = wfTaskMapper;
        this.wfLogMapper = wfLogMapper;
        this.userSupport = userSupport;
        this.bizHandlerMap = new LinkedHashMap<String, WfBizHandler>();
        if (bizHandlers != null) {
            for (WfBizHandler handler : bizHandlers) {
                String key = upper(handler.bizType());
                if (key != null && !key.isEmpty()) this.bizHandlerMap.put(key, handler);
            }
        }
    }

    @Override
    public PageResult<WfTaskVO> todoPage(WfTodoPageReq req) {
        UserSupport.CurrentUser cu = userSupport.currentUser();
        int pageNo = req == null || req.getPageNo() == null || req.getPageNo() < 1 ? 1 : req.getPageNo();
        int pageSize = req == null || req.getPageSize() == null || req.getPageSize() < 1 ? 10 : req.getPageSize();
        String bizType = safeTrim(req == null ? null : req.getBizType());
        String nodeCode = safeTrim(req == null ? null : req.getNodeCode());
        String keyword = safeTrim(req == null ? null : req.getKeyword());
        String assigneeRoleCode = null;
        boolean unitScoped = false;
        if (userSupport.hasUnitAdminRole(cu.getRoles()) && !userSupport.hasAdminRole(cu.getRoles())) {
            assigneeRoleCode = "UNIT_ADMIN";
            unitScoped = true;
            if (isBlank(nodeCode)) nodeCode = WfNodeCodes.UNIT_AUDIT;
        } else if (userSupport.hasFinanceRole(cu.getRoles()) && !userSupport.hasAdminRole(cu.getRoles())) {
            assigneeRoleCode = "FINANCE";
        } else if (!userSupport.hasAdminRole(cu.getRoles())) {
            throw BizException.forbidden("当前角色无审批中心权限");
        }
        int offset = (pageNo - 1) * pageSize;
        long total = wfTaskMapper.countTodoPage(bizType, nodeCode, assigneeRoleCode, unitScoped, cu.getUser().getUnitId(), keyword);
        List<WfTaskVO> records = wfTaskMapper.selectTodoPage(offset, pageSize, bizType, nodeCode, assigneeRoleCode, unitScoped, cu.getUser().getUnitId(), keyword);
        return new PageResult<WfTaskVO>(records, total, pageNo, pageSize);
    }

    @Override
    public PageResult<WfTaskVO> donePage(WfDonePageReq req) {
        UserSupport.CurrentUser cu = userSupport.currentUser();
        if (!(userSupport.hasUnitAdminRole(cu.getRoles()) || userSupport.hasFinanceRole(cu.getRoles()) || userSupport.hasAdminRole(cu.getRoles()))) {
            throw BizException.forbidden("当前角色无审批中心权限");
        }
        int pageNo = req == null || req.getPageNo() == null || req.getPageNo() < 1 ? 1 : req.getPageNo();
        int pageSize = req == null || req.getPageSize() == null || req.getPageSize() < 1 ? 10 : req.getPageSize();
        String bizType = safeTrim(req == null ? null : req.getBizType());
        String nodeCode = safeTrim(req == null ? null : req.getNodeCode());
        String keyword = safeTrim(req == null ? null : req.getKeyword());
        int offset = (pageNo - 1) * pageSize;
        long total = wfTaskMapper.countDonePage(bizType, nodeCode, cu.getUser().getId(), keyword);
        List<WfTaskVO> records = wfTaskMapper.selectDonePage(offset, pageSize, bizType, nodeCode, cu.getUser().getId(), keyword);
        return new PageResult<WfTaskVO>(records, total, pageNo, pageSize);
    }

    @Override
    public void approve(WfActionReq req) {
        if (req == null || req.getBizId() == null) throw BizException.badRequest("bizId不能为空");
        WfBizHandler handler = requireHandler(req.getBizType());
        handler.approve(req.getBizId(), upper(req.getNodeCode()), req.getComment());
    }

    @Override
    public void reject(WfActionReq req) {
        if (req == null || req.getBizId() == null) throw BizException.badRequest("bizId不能为空");
        WfBizHandler handler = requireHandler(req.getBizType());
        handler.reject(req.getBizId(), upper(req.getNodeCode()), req.getComment());
    }

    @Override
    public List<WfLogVO> logs(String bizType, Long bizId) {
        userSupport.currentUser();
        if (bizId == null) throw BizException.badRequest("bizId不能为空");
        return wfLogMapper.selectByBiz(upper(bizType), bizId);
    }

    private WfBizHandler requireHandler(String bizType) {
        String key = upper(bizType);
        WfBizHandler handler = key == null ? null : bizHandlerMap.get(key);
        if (handler == null) throw BizException.badRequest("暂不支持的bizType: " + bizType);
        return handler;
    }

    private String safeTrim(String s) { return s == null ? null : s.trim(); }
    private boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }
    private String upper(String s) { return safeTrim(s) == null ? null : safeTrim(s).toUpperCase(); }
}
