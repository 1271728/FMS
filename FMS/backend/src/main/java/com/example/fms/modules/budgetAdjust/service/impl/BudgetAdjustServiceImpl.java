package com.example.fms.modules.budgetAdjust.service.impl;

import com.example.fms.common.api.PageResult;
import com.example.fms.common.exception.BizException;
import com.example.fms.modules.budget.dto.ProjectBudgetVO;
import com.example.fms.modules.budget.mapper.BudgetSubjectMapper;
import com.example.fms.modules.budget.mapper.ProjectBudgetMapper;
import com.example.fms.modules.budgetAdjust.dto.*;
import com.example.fms.modules.budgetAdjust.mapper.BudgetAdjustLineMapper;
import com.example.fms.modules.budgetAdjust.mapper.BudgetAdjustMapper;
import com.example.fms.modules.budgetAdjust.service.BudgetAdjustService;
import com.example.fms.modules.project.dto.ProjectEntity;
import com.example.fms.modules.project.dto.ProjectStatus;
import com.example.fms.modules.project.mapper.ProjectMapper;
import com.example.fms.modules.shared.support.UserSupport;
import com.example.fms.modules.workflow.dto.WfBizTypes;
import com.example.fms.modules.workflow.dto.WfNodeCodes;
import com.example.fms.modules.workflow.mapper.MsgInboxMapper;
import com.example.fms.modules.workflow.mapper.WfLogMapper;
import com.example.fms.modules.workflow.mapper.WfTaskMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class BudgetAdjustServiceImpl implements BudgetAdjustService {

    private final BudgetAdjustMapper budgetAdjustMapper;
    private final BudgetAdjustLineMapper budgetAdjustLineMapper;
    private final ProjectMapper projectMapper;
    private final ProjectBudgetMapper projectBudgetMapper;
    private final BudgetSubjectMapper budgetSubjectMapper;
    private final UserSupport userSupport;
    private final WfTaskMapper wfTaskMapper;
    private final WfLogMapper wfLogMapper;
    private final MsgInboxMapper msgInboxMapper;

    public BudgetAdjustServiceImpl(BudgetAdjustMapper budgetAdjustMapper,
                                   BudgetAdjustLineMapper budgetAdjustLineMapper,
                                   ProjectMapper projectMapper,
                                   ProjectBudgetMapper projectBudgetMapper,
                                   BudgetSubjectMapper budgetSubjectMapper,
                                   UserSupport userSupport,
                                   WfTaskMapper wfTaskMapper,
                                   WfLogMapper wfLogMapper,
                                   MsgInboxMapper msgInboxMapper) {
        this.budgetAdjustMapper = budgetAdjustMapper;
        this.budgetAdjustLineMapper = budgetAdjustLineMapper;
        this.projectMapper = projectMapper;
        this.projectBudgetMapper = projectBudgetMapper;
        this.budgetSubjectMapper = budgetSubjectMapper;
        this.userSupport = userSupport;
        this.wfTaskMapper = wfTaskMapper;
        this.wfLogMapper = wfLogMapper;
        this.msgInboxMapper = msgInboxMapper;
    }

    @Override
    public PageResult<BudgetAdjustVO> page(BudgetAdjustPageReq req) {
        UserSupport.CurrentUser cu = userSupport.currentUser();
        Scope scope = resolveScope(cu);
        int pageNo = req == null || req.getPageNo() == null || req.getPageNo() < 1 ? 1 : req.getPageNo();
        int pageSize = req == null || req.getPageSize() == null || req.getPageSize() < 1 ? 10 : req.getPageSize();
        String keyword = safeTrim(req == null ? null : req.getKeyword());
        Long projectId = req == null ? null : req.getProjectId();
        Integer status = req == null ? null : req.getStatus();
        Boolean todoOnly = req == null ? null : req.getTodoOnly();
        int offset = (pageNo - 1) * pageSize;
        long total = budgetAdjustMapper.count(keyword, projectId, status, todoOnly, cu.getUser().getId(), cu.getUser().getUnitId(), scope.adminAll, scope.teacherScope, scope.unitAuditor, scope.financeCenter);
        List<BudgetAdjustVO> records = budgetAdjustMapper.selectPage(offset, pageSize, keyword, projectId, status, todoOnly, cu.getUser().getId(), cu.getUser().getUnitId(), scope.adminAll, scope.teacherScope, scope.unitAuditor, scope.financeCenter);
        for (BudgetAdjustVO item : records) {
            item.setCanEdit(canEdit(item.getApplicantId(), item.getStatus(), cu) ? 1 : 0);
            item.setCanSubmit(canSubmit(item.getApplicantId(), item.getStatus(), cu) ? 1 : 0);
            item.setCanWithdraw(canWithdraw(item.getApplicantId(), item.getStatus(), cu) ? 1 : 0);
            item.setCanUnitAudit(canUnitAudit(item.getUnitId(), item.getStatus(), cu) ? 1 : 0);
            item.setCanFinanceAudit(canFinanceAudit(item.getStatus(), cu) ? 1 : 0);
        }
        return new PageResult<BudgetAdjustVO>(records, total, pageNo, pageSize);
    }

    @Override
    public BudgetAdjustDetailVO detail(Long id) {
        if (id == null) throw BizException.badRequest("id不能为空");
        UserSupport.CurrentUser cu = userSupport.currentUser();
        BudgetAdjustEntity entity = requireAdjust(id);
        ProjectEntity project = requireProject(entity.getProjectId());
        ensureReadable(entity, project, cu);
        BudgetAdjustDetailVO detail = budgetAdjustMapper.selectDetailById(id);
        if (detail == null) throw BizException.notFound("预算调整单不存在");
        detail.setCanEdit(canEdit(entity.getApplicantId(), entity.getStatus(), cu) ? 1 : 0);
        detail.setCanSubmit(canSubmit(entity.getApplicantId(), entity.getStatus(), cu) ? 1 : 0);
        detail.setCanWithdraw(canWithdraw(entity.getApplicantId(), entity.getStatus(), cu) ? 1 : 0);
        detail.setCanUnitAudit(canUnitAudit(entity.getUnitId(), entity.getStatus(), cu) ? 1 : 0);
        detail.setCanFinanceAudit(canFinanceAudit(entity.getStatus(), cu) ? 1 : 0);
        detail.setLines(budgetAdjustLineMapper.selectByAdjustId(id));
        detail.setWfLogs(wfLogMapper.selectByBiz(WfBizTypes.BUDGET_ADJUST, id));
        return detail;
    }

    @Override
    public Long create(BudgetAdjustCreateReq req) {
        if (req == null) throw BizException.badRequest("请求不能为空");
        UserSupport.CurrentUser cu = userSupport.currentUser();
        ensureCreator(cu);
        ProjectEntity project = validateProject(req.getProjectId(), cu);
        List<BudgetAdjustLineReq> lines = normalizeLines(req.getLines());
        BigDecimal totalDelta = sumDelta(lines);
        BudgetAdjustEntity entity = new BudgetAdjustEntity();
        entity.setAdjustNo(generateNo());
        entity.setProjectId(project.getId());
        entity.setApplicantId(cu.getUser().getId());
        entity.setUnitId(project.getUnitId());
        entity.setReason(nonBlank(safeTrim(req.getReason()), "请填写调整原因"));
        entity.setTotalDelta(totalDelta);
        entity.setStatus(BudgetAdjustStatus.DRAFT);
        budgetAdjustMapper.insert(entity);
        if (entity.getId() == null) throw BizException.serverError("预算调整单创建失败");
        saveLines(entity.getId(), lines);
        wfLogMapper.insert(WfBizTypes.BUDGET_ADJUST, entity.getId(), WfNodeCodes.CREATE, "创建预算调整单", cu.getUser().getId(), null);
        wfLogMapper.insert(WfBizTypes.BUDGET_ADJUST, entity.getId(), WfNodeCodes.CONTENT, "保存内容", cu.getUser().getId(), entity.getReason());
        return entity.getId();
    }

    @Override
    public void update(BudgetAdjustUpdateReq req) {
        if (req == null || req.getId() == null) throw BizException.badRequest("id不能为空");
        UserSupport.CurrentUser cu = userSupport.currentUser();
        BudgetAdjustEntity entity = requireAdjust(req.getId());
        if (!canEdit(entity.getApplicantId(), entity.getStatus(), cu)) throw BizException.forbidden("当前状态不可编辑");
        ProjectEntity project = validateProject(req.getProjectId(), cu);
        List<BudgetAdjustLineReq> lines = normalizeLines(req.getLines());
        entity.setProjectId(project.getId());
        entity.setUnitId(project.getUnitId());
        entity.setReason(nonBlank(safeTrim(req.getReason()), "请填写调整原因"));
        entity.setTotalDelta(sumDelta(lines));
        budgetAdjustMapper.updateDraft(entity);
        budgetAdjustLineMapper.deleteByAdjustId(entity.getId());
        saveLines(entity.getId(), lines);
        wfLogMapper.insert(WfBizTypes.BUDGET_ADJUST, entity.getId(), WfNodeCodes.CONTENT, "编辑预算调整单", cu.getUser().getId(), entity.getReason());
    }

    @Override
    public void delete(BudgetAdjustIdReq req) {
        if (req == null || req.getId() == null) throw BizException.badRequest("id不能为空");
        UserSupport.CurrentUser cu = userSupport.currentUser();
        BudgetAdjustEntity entity = requireAdjust(req.getId());
        if (!canEdit(entity.getApplicantId(), entity.getStatus(), cu)) throw BizException.forbidden("当前状态不可删除");
        budgetAdjustLineMapper.deleteByAdjustId(entity.getId());
        budgetAdjustMapper.deleteById(entity.getId());
        wfLogMapper.insert(WfBizTypes.BUDGET_ADJUST, entity.getId(), WfNodeCodes.WITHDRAWN, "删除预算调整单", cu.getUser().getId(), null);
    }

    @Override
    public void submit(BudgetAdjustIdReq req) {
        if (req == null || req.getId() == null) throw BizException.badRequest("id不能为空");
        UserSupport.CurrentUser cu = userSupport.currentUser();
        BudgetAdjustEntity entity = requireAdjust(req.getId());
        if (!canSubmit(entity.getApplicantId(), entity.getStatus(), cu)) throw BizException.forbidden("当前状态不可提交");
        List<BudgetAdjustLineVO> lines = budgetAdjustLineMapper.selectByAdjustId(entity.getId());
        if (lines == null || lines.isEmpty()) throw BizException.badRequest("请至少填写一条调整明细");
        budgetAdjustMapper.markSubmitted(entity.getId(), BudgetAdjustStatus.PENDING_UNIT, WfNodeCodes.UNIT_AUDIT);
        wfTaskMapper.cancelTodoByBiz(WfBizTypes.BUDGET_ADJUST, entity.getId(), "RE-SUBMIT", cu.getUser().getId());
        wfTaskMapper.insert(WfBizTypes.BUDGET_ADJUST, entity.getId(), WfNodeCodes.UNIT_AUDIT, "预算调整单待单位审核", "UNIT_ADMIN", null, entity.getApplicantId(), entity.getUnitId());
        wfLogMapper.insert(WfBizTypes.BUDGET_ADJUST, entity.getId(), WfNodeCodes.UNIT_AUDIT, "提交预算调整单", cu.getUser().getId(), null);
    }

    @Override
    public void withdraw(BudgetAdjustIdReq req) {
        if (req == null || req.getId() == null) throw BizException.badRequest("id不能为空");
        UserSupport.CurrentUser cu = userSupport.currentUser();
        BudgetAdjustEntity entity = requireAdjust(req.getId());
        if (!canWithdraw(entity.getApplicantId(), entity.getStatus(), cu)) throw BizException.forbidden("当前状态不可撤销");
        wfTaskMapper.cancelTodoByBiz(WfBizTypes.BUDGET_ADJUST, entity.getId(), "WITHDRAW", cu.getUser().getId());
        budgetAdjustMapper.updateStatus(entity.getId(), BudgetAdjustStatus.WITHDRAWN, WfNodeCodes.WITHDRAWN, null);
        wfLogMapper.insert(WfBizTypes.BUDGET_ADJUST, entity.getId(), WfNodeCodes.WITHDRAWN, "撤销预算调整单", cu.getUser().getId(), null);
    }

    @Override
    public void workflowApprove(Long bizId, String nodeCode, String comment) {
        UserSupport.CurrentUser cu = userSupport.currentUser();
        BudgetAdjustEntity entity = requireAdjust(bizId);
        if (WfNodeCodes.UNIT_AUDIT.equalsIgnoreCase(nodeCode)) {
            if (!canUnitAudit(entity.getUnitId(), entity.getStatus(), cu)) throw BizException.forbidden("无权执行二级单位审核");
            if (wfTaskMapper.countTodo(WfBizTypes.BUDGET_ADJUST, bizId, WfNodeCodes.UNIT_AUDIT) <= 0) throw BizException.conflict("当前待办已不存在");
            wfTaskMapper.markDone(WfBizTypes.BUDGET_ADJUST, bizId, WfNodeCodes.UNIT_AUDIT, "APPROVE", cu.getUser().getId());
            budgetAdjustMapper.updateStatus(bizId, BudgetAdjustStatus.PENDING_FINANCE, WfNodeCodes.FIN_REVIEW, blankToNull(comment));
            wfTaskMapper.insert(WfBizTypes.BUDGET_ADJUST, bizId, WfNodeCodes.FIN_REVIEW, "预算调整单待财务复核", "FINANCE", null, entity.getApplicantId(), entity.getUnitId());
            wfLogMapper.insert(WfBizTypes.BUDGET_ADJUST, bizId, WfNodeCodes.UNIT_AUDIT, "二级单位审核通过", cu.getUser().getId(), blankToNull(comment));
            return;
        }
        if (WfNodeCodes.FIN_REVIEW.equalsIgnoreCase(nodeCode)) {
            if (!canFinanceAudit(entity.getStatus(), cu)) throw BizException.forbidden("无权执行财务复核");
            if (wfTaskMapper.countTodo(WfBizTypes.BUDGET_ADJUST, bizId, WfNodeCodes.FIN_REVIEW) <= 0) throw BizException.conflict("当前待办已不存在");
            applyBudgetChanges(entity.getProjectId(), budgetAdjustLineMapper.selectByAdjustId(bizId));
            wfTaskMapper.markDone(WfBizTypes.BUDGET_ADJUST, bizId, WfNodeCodes.FIN_REVIEW, "APPROVE", cu.getUser().getId());
            budgetAdjustMapper.markEffective(bizId, BudgetAdjustStatus.EFFECTIVE, WfNodeCodes.DONE, blankToNull(comment));
            wfLogMapper.insert(WfBizTypes.BUDGET_ADJUST, bizId, WfNodeCodes.FIN_REVIEW, "财务复核通过并生效", cu.getUser().getId(), blankToNull(comment));
            return;
        }
        throw BizException.badRequest("当前节点不支持通过操作");
    }

    @Override
    public void workflowReject(Long bizId, String nodeCode, String comment) {
        UserSupport.CurrentUser cu = userSupport.currentUser();
        BudgetAdjustEntity entity = requireAdjust(bizId);
        if (isBlank(comment)) throw BizException.badRequest("驳回时请填写意见");
        if (WfNodeCodes.UNIT_AUDIT.equalsIgnoreCase(nodeCode)) {
            if (!canUnitAudit(entity.getUnitId(), entity.getStatus(), cu)) throw BizException.forbidden("无权执行二级单位审核");
        } else if (WfNodeCodes.FIN_REVIEW.equalsIgnoreCase(nodeCode)) {
            if (!canFinanceAudit(entity.getStatus(), cu)) throw BizException.forbidden("无权执行财务复核");
        } else {
            throw BizException.badRequest("当前节点不支持驳回操作");
        }
        if (wfTaskMapper.countTodo(WfBizTypes.BUDGET_ADJUST, bizId, upper(nodeCode)) <= 0) throw BizException.conflict("当前待办已不存在");
        wfTaskMapper.cancelTodoByBiz(WfBizTypes.BUDGET_ADJUST, bizId, "REJECT", cu.getUser().getId());
        budgetAdjustMapper.updateStatus(bizId, BudgetAdjustStatus.REJECTED, WfNodeCodes.REJECTED, comment.trim());
        wfLogMapper.insert(WfBizTypes.BUDGET_ADJUST, bizId, upper(nodeCode), "驳回预算调整单", cu.getUser().getId(), comment.trim());
        msgInboxMapper.insert(entity.getApplicantId(), "REJECT", "预算调整单被驳回", "预算调整单" + entity.getAdjustNo() + " 已被驳回：" + comment.trim(), WfBizTypes.BUDGET_ADJUST, bizId);
    }

    private void applyBudgetChanges(Long projectId, List<BudgetAdjustLineVO> lines) {
        if (lines == null || lines.isEmpty()) return;
        for (BudgetAdjustLineVO line : lines) {
            BigDecimal delta = line.getDeltaAmount() == null ? BigDecimal.ZERO : line.getDeltaAmount();
            if (delta.compareTo(BigDecimal.ZERO) == 0) continue;
            if (delta.compareTo(BigDecimal.ZERO) > 0) {
                if (projectBudgetMapper.countByProjectIdAndSubjectId(projectId, line.getSubjectId()) > 0) {
                    if (projectBudgetMapper.increaseAdjusted(projectId, line.getSubjectId(), delta) <= 0) {
                        throw BizException.serverError("预算调整生效失败");
                    }
                } else {
                    if (projectBudgetMapper.insertWithAdjusted(projectId, line.getSubjectId(), delta) <= 0) {
                        throw BizException.serverError("预算调整生效失败");
                    }
                }
            } else {
                BigDecimal abs = delta.abs();
                if (projectBudgetMapper.decreaseAdjustedSafely(projectId, line.getSubjectId(), abs) <= 0) {
                    throw BizException.badRequest("预算科目可调整余额不足：" + (line.getSubjectName() == null ? line.getSubjectId() : line.getSubjectName()));
                }
            }
        }
    }

    private void saveLines(Long adjustId, List<BudgetAdjustLineReq> lines) {
        for (BudgetAdjustLineReq line : lines) {
            if (budgetSubjectMapper.existsEnabled(line.getSubjectId()) <= 0) throw BizException.badRequest("预算科目不存在");
            budgetAdjustLineMapper.insert(adjustId, line.getSubjectId(), line.getDeltaAmount(), blankToNull(line.getRemark()));
        }
    }

    private List<BudgetAdjustLineReq> normalizeLines(List<BudgetAdjustLineReq> lines) {
        if (lines == null || lines.isEmpty()) throw BizException.badRequest("请至少填写一条调整明细");
        List<BudgetAdjustLineReq> out = new ArrayList<BudgetAdjustLineReq>();
        for (BudgetAdjustLineReq line : lines) {
            if (line == null) continue;
            if (line.getSubjectId() == null) throw BizException.badRequest("请选择预算科目");
            if (line.getDeltaAmount() == null || line.getDeltaAmount().compareTo(BigDecimal.ZERO) == 0) throw BizException.badRequest("调整金额不能为0");
            BudgetAdjustLineReq x = new BudgetAdjustLineReq();
            x.setSubjectId(line.getSubjectId());
            x.setDeltaAmount(line.getDeltaAmount());
            x.setRemark(blankToNull(line.getRemark()));
            out.add(x);
        }
        if (out.isEmpty()) throw BizException.badRequest("请至少填写一条调整明细");
        return out;
    }

    private BigDecimal sumDelta(List<BudgetAdjustLineReq> lines) {
        BigDecimal total = BigDecimal.ZERO;
        for (BudgetAdjustLineReq line : lines) {
            total = total.add(line.getDeltaAmount() == null ? BigDecimal.ZERO : line.getDeltaAmount());
        }
        return total;
    }

    private ProjectEntity validateProject(Long projectId, UserSupport.CurrentUser cu) {
        if (projectId == null) throw BizException.badRequest("请选择项目");
        ProjectEntity project = requireProject(projectId);
        if (!userSupport.canReadProject(project, cu)) throw BizException.forbidden("无权使用该项目");
        if (project.getStatus() == null || project.getStatus() != ProjectStatus.APPROVED) {
            throw BizException.badRequest("仅立项通过项目可发起预算调整");
        }
        if (!(userSupport.hasAdminRole(cu.getRoles()) || userSupport.hasTeacherRole(cu.getRoles()))) {
            throw BizException.forbidden("当前角色不能发起预算调整");
        }
        return project;
    }

    private void ensureReadable(BudgetAdjustEntity entity, ProjectEntity project, UserSupport.CurrentUser cu) {
        if (userSupport.hasAdminRole(cu.getRoles()) || userSupport.hasFinanceRole(cu.getRoles())) return;
        if (userSupport.hasUnitAdminRole(cu.getRoles())) {
            if (cu.getUser().getUnitId() != null && cu.getUser().getUnitId().equals(entity.getUnitId())) return;
            throw BizException.forbidden("无权查看该预算调整单");
        }
        if (userSupport.hasTeacherRole(cu.getRoles())) {
            if ((entity.getApplicantId() != null && entity.getApplicantId().equals(cu.getUser().getId())) || userSupport.canReadProject(project, cu)) return;
            throw BizException.forbidden("无权查看该预算调整单");
        }
        throw BizException.forbidden("无权查看该预算调整单");
    }

    private boolean canEdit(Long applicantId, Integer status, UserSupport.CurrentUser cu) {
        if (userSupport.hasAdminRole(cu.getRoles())) return true;
        return applicantId != null && applicantId.equals(cu.getUser().getId()) && (status == BudgetAdjustStatus.DRAFT || status == BudgetAdjustStatus.REJECTED);
    }

    private boolean canSubmit(Long applicantId, Integer status, UserSupport.CurrentUser cu) {
        return canEdit(applicantId, status, cu);
    }

    private boolean canWithdraw(Long applicantId, Integer status, UserSupport.CurrentUser cu) {
        if (userSupport.hasAdminRole(cu.getRoles())) return status != null && status == BudgetAdjustStatus.PENDING_UNIT;
        return applicantId != null && applicantId.equals(cu.getUser().getId()) && status != null && status == BudgetAdjustStatus.PENDING_UNIT;
    }

    private boolean canUnitAudit(Long unitId, Integer status, UserSupport.CurrentUser cu) {
        if (status == null || status != BudgetAdjustStatus.PENDING_UNIT) return false;
        if (userSupport.hasAdminRole(cu.getRoles())) return true;
        return userSupport.hasUnitAdminRole(cu.getRoles()) && unitId != null && unitId.equals(cu.getUser().getUnitId());
    }

    private boolean canFinanceAudit(Integer status, UserSupport.CurrentUser cu) {
        if (status == null || status != BudgetAdjustStatus.PENDING_FINANCE) return false;
        return userSupport.hasAdminRole(cu.getRoles()) || userSupport.hasFinanceRole(cu.getRoles());
    }

    private void ensureCreator(UserSupport.CurrentUser cu) {
        if (!(userSupport.hasTeacherRole(cu.getRoles()) || userSupport.hasAdminRole(cu.getRoles()))) {
            throw BizException.forbidden("当前角色不能创建预算调整单");
        }
    }

    private BudgetAdjustEntity requireAdjust(Long id) {
        BudgetAdjustEntity entity = budgetAdjustMapper.selectEntityById(id);
        if (entity == null) throw BizException.notFound("预算调整单不存在");
        return entity;
    }

    private ProjectEntity requireProject(Long id) {
        ProjectEntity project = projectMapper.selectEntityById(id);
        if (project == null) throw BizException.notFound("项目不存在");
        return project;
    }

    private Scope resolveScope(UserSupport.CurrentUser cu) {
        Scope scope = new Scope();
        scope.adminAll = userSupport.hasAdminRole(cu.getRoles());
        scope.financeCenter = userSupport.hasFinanceRole(cu.getRoles());
        scope.unitAuditor = userSupport.hasUnitAdminRole(cu.getRoles());
        scope.teacherScope = userSupport.hasTeacherRole(cu.getRoles()) && !scope.adminAll && !scope.financeCenter && !scope.unitAuditor;
        return scope;
    }

    private String generateNo() { return "TJ" + System.currentTimeMillis(); }
    private String safeTrim(String s) { return s == null ? null : s.trim(); }
    private String blankToNull(String s) { String v = safeTrim(s); return v == null || v.isEmpty() ? null : v; }
    private String nonBlank(String s, String msg) { if (isBlank(s)) throw BizException.badRequest(msg); return s; }
    private boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }
    private String upper(String s) { return safeTrim(s) == null ? null : safeTrim(s).toUpperCase(); }

    private static class Scope {
        private boolean adminAll;
        private boolean teacherScope;
        private boolean unitAuditor;
        private boolean financeCenter;
    }
}
