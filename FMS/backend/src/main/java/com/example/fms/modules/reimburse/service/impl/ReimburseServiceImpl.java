package com.example.fms.modules.reimburse.service.impl;

import com.example.fms.common.api.PageResult;
import com.example.fms.common.exception.BizException;
import com.example.fms.modules.archive.mapper.ArchiveMapper;
import com.example.fms.modules.budget.dto.ProjectBudgetVO;
import com.example.fms.modules.budget.mapper.BudgetSubjectMapper;
import com.example.fms.modules.budget.mapper.ProjectBudgetMapper;
import com.example.fms.modules.budget.service.BudgetService;
import com.example.fms.modules.pay.mapper.PaymentMapper;
import com.example.fms.modules.project.dto.ProjectEntity;
import com.example.fms.modules.project.dto.ProjectStatus;
import com.example.fms.modules.project.mapper.ProjectMapper;
import com.example.fms.modules.project.mapper.ProjectMemberMapper;
import com.example.fms.modules.reimburse.dto.*;
import com.example.fms.modules.reimburse.mapper.ReimburseAuditLogMapper;
import com.example.fms.modules.reimburse.mapper.ReimburseFileMapper;
import com.example.fms.modules.reimburse.mapper.ReimburseItemMapper;
import com.example.fms.modules.reimburse.mapper.ReimburseMapper;
import com.example.fms.modules.reimburse.service.ReimburseService;
import com.example.fms.modules.shared.support.UserSupport;
import com.example.fms.modules.workflow.dto.WfBizTypes;
import com.example.fms.modules.workflow.dto.WfNodeCodes;
import com.example.fms.modules.workflow.mapper.MsgInboxMapper;
import com.example.fms.modules.workflow.mapper.WfTaskMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Transactional(rollbackFor = Exception.class)
public class ReimburseServiceImpl implements ReimburseService {

    private final ReimburseMapper reimburseMapper;
    private final ReimburseItemMapper reimburseItemMapper;
    private final ReimburseAuditLogMapper reimburseAuditLogMapper;
    private final ReimburseFileMapper reimburseFileMapper;
    private final ProjectMapper projectMapper;
    private final ProjectMemberMapper projectMemberMapper;
    private final BudgetSubjectMapper budgetSubjectMapper;
    private final ProjectBudgetMapper projectBudgetMapper;
    private final BudgetService budgetService;
    private final UserSupport userSupport;
    private final WfTaskMapper wfTaskMapper;
    private final MsgInboxMapper msgInboxMapper;
    private final PaymentMapper paymentMapper;
    private final ArchiveMapper archiveMapper;

    public ReimburseServiceImpl(ReimburseMapper reimburseMapper,
                                ReimburseItemMapper reimburseItemMapper,
                                ReimburseAuditLogMapper reimburseAuditLogMapper,
                                ReimburseFileMapper reimburseFileMapper,
                                ProjectMapper projectMapper,
                                ProjectMemberMapper projectMemberMapper,
                                BudgetSubjectMapper budgetSubjectMapper,
                                ProjectBudgetMapper projectBudgetMapper,
                                BudgetService budgetService,
                                UserSupport userSupport,
                                WfTaskMapper wfTaskMapper,
                                MsgInboxMapper msgInboxMapper,
                                PaymentMapper paymentMapper,
                                ArchiveMapper archiveMapper) {
        this.reimburseMapper = reimburseMapper;
        this.reimburseItemMapper = reimburseItemMapper;
        this.reimburseAuditLogMapper = reimburseAuditLogMapper;
        this.reimburseFileMapper = reimburseFileMapper;
        this.projectMapper = projectMapper;
        this.projectMemberMapper = projectMemberMapper;
        this.budgetSubjectMapper = budgetSubjectMapper;
        this.projectBudgetMapper = projectBudgetMapper;
        this.budgetService = budgetService;
        this.userSupport = userSupport;
        this.wfTaskMapper = wfTaskMapper;
        this.msgInboxMapper = msgInboxMapper;
        this.paymentMapper = paymentMapper;
        this.archiveMapper = archiveMapper;
    }

    @Override
    public PageResult<ReimburseVO> page(ReimbursePageReq req) {
        UserSupport.CurrentUser cu = userSupport.currentUser();
        Scope scope = resolveScope(cu.getRoles());
        int pageNo = req == null || req.getPageNo() == null || req.getPageNo() < 1 ? 1 : req.getPageNo();
        int pageSize = req == null || req.getPageSize() == null || req.getPageSize() < 1 ? 10 : req.getPageSize();
        String keyword = req == null ? null : safeTrim(req.getKeyword());
        Integer status = req == null ? null : req.getStatus();
        Long projectId = req == null ? null : req.getProjectId();
        Boolean todoOnly = req == null ? null : req.getTodoOnly();
        int offset = (pageNo - 1) * pageSize;
        long total = reimburseMapper.count(keyword, status, projectId, todoOnly, cu.getUser().getId(), cu.getUser().getUnitId(), scope.adminAll, scope.teacherScope, scope.unitAuditor, scope.financeCenter);
        List<ReimburseVO> records = reimburseMapper.selectPage(offset, pageSize, keyword, status, projectId, todoOnly, cu.getUser().getId(), cu.getUser().getUnitId(), scope.adminAll, scope.teacherScope, scope.unitAuditor, scope.financeCenter);
        for (ReimburseVO item : records) {
            ProjectEntity project = projectMapper.selectEntityById(item.getProjectId());
            item.setCanEdit(canEdit(item.getApplicantUserId(), item.getStatus(), cu) ? 1 : 0);
            item.setCanSubmit(canSubmit(item.getApplicantUserId(), item.getStatus(), cu) ? 1 : 0);
            item.setCanLeaderAudit(canLeaderAudit(item.getApplicantUserId(), project == null ? null : project.getPrincipalUserId(), item.getStatus(), cu) ? 1 : 0);
            item.setCanUnitAudit(canUnitAudit(item.getUnitId(), item.getStatus(), cu) ? 1 : 0);
            item.setCanFinanceAudit(canFinanceAudit(item.getStatus(), cu) ? 1 : 0);
            item.setCanWithdraw(canWithdraw(item.getApplicantUserId(), item.getStatus(), cu) ? 1 : 0);
        }
        return new PageResult<ReimburseVO>(records, total, pageNo, pageSize);
    }

    @Override
    public ReimburseDetailVO detail(Long id) {
        if (id == null) throw BizException.badRequest("id不能为空");
        UserSupport.CurrentUser cu = userSupport.currentUser();
        ReimburseEntity entity = requireReimburse(id);
        ProjectEntity project = requireProject(entity.getProjectId());
        ensureReadable(entity, project, cu);
        ReimburseDetailVO detail = reimburseMapper.selectDetailById(id);
        if (detail == null) throw BizException.notFound("报销单不存在");
        detail.setCanEdit(canEdit(entity.getApplicantUserId(), entity.getStatus(), cu) ? 1 : 0);
        detail.setCanSubmit(canSubmit(entity.getApplicantUserId(), entity.getStatus(), cu) ? 1 : 0);
        detail.setCanLeaderAudit(canLeaderAudit(entity.getApplicantUserId(), project.getPrincipalUserId(), entity.getStatus(), cu) ? 1 : 0);
        detail.setCanUnitAudit(canUnitAudit(entity.getUnitId(), entity.getStatus(), cu) ? 1 : 0);
        detail.setCanFinanceAudit(canFinanceAudit(entity.getStatus(), cu) ? 1 : 0);
        detail.setCanWithdraw(canWithdraw(entity.getApplicantUserId(), entity.getStatus(), cu) ? 1 : 0);
        detail.setItems(reimburseItemMapper.selectByReimburseId(id));
        detail.setAuditLogs(reimburseAuditLogMapper.selectByReimburseId(id));
        detail.setAttachments(reimburseFileMapper.selectByReimburseId(id));
        detail.setPayment(paymentMapper.selectByReimbId(id));
        detail.setArchive(archiveMapper.selectByReimbId(id));
        return detail;
    }

    @Override
    public Long create(ReimburseCreateReq req) {
        if (req == null) throw BizException.badRequest("请求不能为空");
        UserSupport.CurrentUser cu = userSupport.currentUser();
        ensureCanCreate(cu);
        ProjectEntity project = validateProjectForDraft(req.getProjectId(), cu);
        List<ReimburseItemReq> items = normalizeItems(req.getItems());
        BigDecimal total = sumItems(items);
        ReimburseEntity entity = new ReimburseEntity();
        entity.setReimburseNo(generateNo());
        entity.setProjectId(project.getId());
        entity.setApplicantUserId(cu.getUser().getId());
        entity.setUnitId(project.getUnitId());
        entity.setTitle(nonBlank(safeTrim(req.getTitle()), "请填写报销标题"));
        entity.setDescription(blankToNull(req.getDescription()));
        entity.setTotalAmount(total);
        entity.setStatus(ReimburseStatus.DRAFT);
        reimburseMapper.insert(entity);
        if (entity.getId() == null) throw BizException.serverError("报销单创建失败");
        saveItems(entity.getId(), items);
        saveAttachments(entity.getId(), req.getAttachments());
        reimburseAuditLogMapper.insert(entity.getId(), "保存内容", null, ReimburseStatus.DRAFT, cu.getUser().getId(), entity.getDescription());
        reimburseAuditLogMapper.insert(entity.getId(), "创建报销单", null, ReimburseStatus.DRAFT, cu.getUser().getId(), null);
        return entity.getId();
    }

    @Override
    public void update(ReimburseUpdateReq req) {
        if (req == null || req.getId() == null) throw BizException.badRequest("id不能为空");
        UserSupport.CurrentUser cu = userSupport.currentUser();
        ReimburseEntity old = requireReimburse(req.getId());
        if (!canEdit(old.getApplicantUserId(), old.getStatus(), cu)) throw BizException.forbidden("当前状态不可编辑");
        ProjectEntity project = validateProjectForDraft(req.getProjectId(), cu);
        List<ReimburseItemReq> items = normalizeItems(req.getItems());
        BigDecimal total = sumItems(items);
        old.setProjectId(project.getId());
        old.setUnitId(project.getUnitId());
        old.setTitle(nonBlank(safeTrim(req.getTitle()), "请填写报销标题"));
        old.setDescription(blankToNull(req.getDescription()));
        old.setTotalAmount(total);
        reimburseMapper.updateDraft(old);
        reimburseItemMapper.deleteByReimburseId(old.getId());
        saveItems(old.getId(), items);
        reimburseFileMapper.deleteByReimburseId(old.getId());
        saveAttachments(old.getId(), req.getAttachments());
        reimburseAuditLogMapper.insert(old.getId(), "保存内容", old.getStatus(), old.getStatus(), cu.getUser().getId(), old.getDescription());
        reimburseAuditLogMapper.insert(old.getId(), "编辑报销单", old.getStatus(), old.getStatus(), cu.getUser().getId(), null);
    }

    @Override
    public void submit(ReimburseSubmitReq req) {
        if (req == null || req.getId() == null) throw BizException.badRequest("id不能为空");
        UserSupport.CurrentUser cu = userSupport.currentUser();
        ReimburseEntity entity = requireReimburse(req.getId());
        if (!canSubmit(entity.getApplicantUserId(), entity.getStatus(), cu)) throw BizException.forbidden("当前状态不可提交");
        ProjectEntity project = requireProject(entity.getProjectId());
        if (project.getStatus() == null || project.getStatus() != ProjectStatus.APPROVED) throw BizException.badRequest("仅立项通过项目可提交报销");
        List<ReimburseItemVO> items = reimburseItemMapper.selectByReimburseId(entity.getId());
        if (items == null || items.isEmpty()) throw BizException.badRequest("请至少填写一条报销明细");
        List<ReimburseAttachmentVO> files = reimburseFileMapper.selectByReimburseId(entity.getId());
        if (files == null || files.isEmpty()) throw BizException.badRequest("请至少上传发票或凭证附件");
        budgetService.ensureDefaultBudgetLedger(project.getId(), project.getTotalBudget());
        Map<Long, BigDecimal> amounts = sumBySubject(items);
        List<ProjectBudgetVO> budgets = projectBudgetMapper.selectByProjectId(project.getId());
        Map<Long, ProjectBudgetVO> budgetMap = new HashMap<Long, ProjectBudgetVO>();
        for (ProjectBudgetVO it : budgets) budgetMap.put(it.getSubjectId(), it);
        for (Map.Entry<Long, BigDecimal> e : amounts.entrySet()) {
            ProjectBudgetVO pb = budgetMap.get(e.getKey());
            if (pb == null) throw BizException.badRequest("存在未配置项目预算的科目，subjectId=" + e.getKey());
            if (pb.getAvailableAmount() == null || pb.getAvailableAmount().compareTo(e.getValue()) < 0) {
                throw BizException.badRequest("预算余额不足：" + pb.getSubjectName());
            }
        }
        for (Map.Entry<Long, BigDecimal> e : amounts.entrySet()) {
            if (projectBudgetMapper.freeze(project.getId(), e.getKey(), e.getValue()) <= 0) {
                throw BizException.badRequest("冻结预算失败，可能余额不足");
            }
        }
        wfTaskMapper.cancelTodoByBiz(WfBizTypes.REIMB, entity.getId(), "RE-SUBMIT", cu.getUser().getId());
        if (project.getPrincipalUserId() != null && !project.getPrincipalUserId().equals(entity.getApplicantUserId())) {
            reimburseMapper.markSubmitted(entity.getId(), ReimburseStatus.PENDING_PI, WfNodeCodes.PI_AUDIT);
            wfTaskMapper.insert(WfBizTypes.REIMB, entity.getId(), WfNodeCodes.PI_AUDIT, "报销单待项目负责人审批", "PI", project.getPrincipalUserId(), entity.getApplicantUserId(), entity.getUnitId());
            msgInboxMapper.insert(project.getPrincipalUserId(), "PI_TODO", "成员报销待审批", "成员报销单 " + entity.getReimburseNo() + " 已提交，请组长先行审批。", WfBizTypes.REIMB, entity.getId());
            reimburseAuditLogMapper.insert(entity.getId(), "提交报销审批", entity.getStatus(), ReimburseStatus.PENDING_PI, cu.getUser().getId(), null);
        } else {
            reimburseMapper.markSubmitted(entity.getId(), ReimburseStatus.PENDING_UNIT, WfNodeCodes.UNIT_AUDIT);
            wfTaskMapper.insert(WfBizTypes.REIMB, entity.getId(), WfNodeCodes.UNIT_AUDIT, "报销单待单位审核", "UNIT_ADMIN", null, entity.getApplicantUserId(), entity.getUnitId());
            reimburseAuditLogMapper.insert(entity.getId(), "提交报销审批", entity.getStatus(), ReimburseStatus.PENDING_UNIT, cu.getUser().getId(), null);
        }
    }

    @Override
    public void withdraw(ReimburseSubmitReq req) {
        if (req == null || req.getId() == null) throw BizException.badRequest("id不能为空");
        UserSupport.CurrentUser cu = userSupport.currentUser();
        ReimburseEntity entity = requireReimburse(req.getId());
        if (!canWithdraw(entity.getApplicantUserId(), entity.getStatus(), cu)) throw BizException.forbidden("当前状态不可撤销");
        releaseFrozen(requireProject(entity.getProjectId()).getId(), sumBySubject(reimburseItemMapper.selectByReimburseId(entity.getId())));
        wfTaskMapper.cancelTodoByBiz(WfBizTypes.REIMB, entity.getId(), "WITHDRAW", cu.getUser().getId());
        reimburseMapper.updateStatus(entity.getId(), ReimburseStatus.WITHDRAWN, WfNodeCodes.WITHDRAWN, null);
        reimburseAuditLogMapper.insert(entity.getId(), "撤销报销单", entity.getStatus(), ReimburseStatus.WITHDRAWN, cu.getUser().getId(), null);
    }

    @Override
    public void audit(ReimburseAuditReq req) {
        if (req == null || req.getReimburseId() == null) throw BizException.badRequest("reimburseId不能为空");
        String action = safeTrim(req.getAction());
        if (!"pass".equalsIgnoreCase(action) && !"reject".equalsIgnoreCase(action)) throw BizException.badRequest("action仅支持pass/reject");
        ReimburseEntity entity = requireReimburse(req.getReimburseId());
        String nodeCode;
        if (entity.getStatus() != null && entity.getStatus() == ReimburseStatus.PENDING_PI) nodeCode = WfNodeCodes.PI_AUDIT;
        else if (entity.getStatus() != null && entity.getStatus() == ReimburseStatus.PENDING_UNIT) nodeCode = WfNodeCodes.UNIT_AUDIT;
        else nodeCode = WfNodeCodes.FIN_REVIEW;
        if ("pass".equalsIgnoreCase(action)) workflowApprove(req.getReimburseId(), nodeCode, req.getComment());
        else workflowReject(req.getReimburseId(), nodeCode, req.getComment());
    }

    @Override
    public void workflowApprove(Long bizId, String nodeCode, String comment) {
        UserSupport.CurrentUser cu = userSupport.currentUser();
        ReimburseEntity entity = requireReimburse(bizId);
        ProjectEntity project = requireProject(entity.getProjectId());
        List<ReimburseItemVO> items = reimburseItemMapper.selectByReimburseId(entity.getId());
        Map<Long, BigDecimal> amounts = sumBySubject(items);
        if (WfNodeCodes.PI_AUDIT.equalsIgnoreCase(nodeCode)) {
            if (!canLeaderAudit(entity.getApplicantUserId(), project.getPrincipalUserId(), entity.getStatus(), cu)) throw BizException.forbidden("无权执行项目负责人审批");
            if (wfTaskMapper.countTodo(WfBizTypes.REIMB, bizId, WfNodeCodes.PI_AUDIT) <= 0) throw BizException.conflict("当前待办已不存在");
            wfTaskMapper.markDone(WfBizTypes.REIMB, bizId, WfNodeCodes.PI_AUDIT, "APPROVE", cu.getUser().getId());
            reimburseMapper.updateStatus(entity.getId(), ReimburseStatus.PENDING_UNIT, WfNodeCodes.UNIT_AUDIT, blankToNull(comment));
            wfTaskMapper.insert(WfBizTypes.REIMB, entity.getId(), WfNodeCodes.UNIT_AUDIT, "报销单待单位审核", "UNIT_ADMIN", null, entity.getApplicantUserId(), entity.getUnitId());
            reimburseAuditLogMapper.insert(entity.getId(), "组长审批通过", entity.getStatus(), ReimburseStatus.PENDING_UNIT, cu.getUser().getId(), blankToNull(comment));
            msgInboxMapper.insert(entity.getApplicantUserId(), "LEADER_PASS", "组长审批通过", "报销单 " + entity.getReimburseNo() + " 已通过组长审批，进入单位审核。", WfBizTypes.REIMB, bizId);
            return;
        }
        if (WfNodeCodes.UNIT_AUDIT.equalsIgnoreCase(nodeCode)) {
            if (!canUnitAudit(entity.getUnitId(), entity.getStatus(), cu)) throw BizException.forbidden("无权执行二级单位审批");
            if (wfTaskMapper.countTodo(WfBizTypes.REIMB, bizId, WfNodeCodes.UNIT_AUDIT) <= 0) throw BizException.conflict("当前待办已不存在");
            wfTaskMapper.markDone(WfBizTypes.REIMB, bizId, WfNodeCodes.UNIT_AUDIT, "APPROVE", cu.getUser().getId());
            reimburseMapper.updateStatus(entity.getId(), ReimburseStatus.PENDING_FINANCE, WfNodeCodes.FIN_REVIEW, blankToNull(comment));
            wfTaskMapper.insert(WfBizTypes.REIMB, entity.getId(), WfNodeCodes.FIN_REVIEW, "报销单待财务复核", "FINANCE", null, entity.getApplicantUserId(), entity.getUnitId());
            reimburseAuditLogMapper.insert(entity.getId(), "二级单位审批通过", entity.getStatus(), ReimburseStatus.PENDING_FINANCE, cu.getUser().getId(), blankToNull(comment));
            return;
        }
        if (WfNodeCodes.FIN_REVIEW.equalsIgnoreCase(nodeCode)) {
            if (!canFinanceAudit(entity.getStatus(), cu)) throw BizException.forbidden("无权执行财务处审批");
            if (wfTaskMapper.countTodo(WfBizTypes.REIMB, bizId, WfNodeCodes.FIN_REVIEW) <= 0) throw BizException.conflict("当前待办已不存在");
            Long projectId = project.getId();
            consumeFrozen(projectId, amounts);
            wfTaskMapper.markDone(WfBizTypes.REIMB, bizId, WfNodeCodes.FIN_REVIEW, "APPROVE", cu.getUser().getId());
            reimburseMapper.updateStatus(entity.getId(), ReimburseStatus.WAIT_PAY, WfNodeCodes.PAY_ARCHIVE, blankToNull(comment));
            wfTaskMapper.insert(WfBizTypes.REIMB, entity.getId(), WfNodeCodes.PAY_ARCHIVE, "报销单待支付归档", "FINANCE", null, entity.getApplicantUserId(), entity.getUnitId());
            reimburseAuditLogMapper.insert(entity.getId(), "财务处审批通过", entity.getStatus(), ReimburseStatus.WAIT_PAY, cu.getUser().getId(), blankToNull(comment));
            msgInboxMapper.insert(entity.getApplicantUserId(), "FIN_PASS", "报销单已通过财务复核", "报销单 " + entity.getReimburseNo() + " 已进入支付归档环节。", WfBizTypes.REIMB, bizId);
            return;
        }
        throw BizException.badRequest("当前节点不支持通过操作");
    }

    @Override
    public void workflowReject(Long bizId, String nodeCode, String comment) {
        UserSupport.CurrentUser cu = userSupport.currentUser();
        ReimburseEntity entity = requireReimburse(bizId);
        ProjectEntity project = requireProject(entity.getProjectId());
        if (isBlank(comment)) throw BizException.badRequest("驳回时请填写意见");
        if (WfNodeCodes.PI_AUDIT.equalsIgnoreCase(nodeCode)) {
            if (!canLeaderAudit(entity.getApplicantUserId(), project.getPrincipalUserId(), entity.getStatus(), cu)) throw BizException.forbidden("无权执行组长审批");
        } else if (WfNodeCodes.UNIT_AUDIT.equalsIgnoreCase(nodeCode)) {
            if (!canUnitAudit(entity.getUnitId(), entity.getStatus(), cu)) throw BizException.forbidden("无权执行二级单位审批");
        } else if (WfNodeCodes.FIN_REVIEW.equalsIgnoreCase(nodeCode)) {
            if (!canFinanceAudit(entity.getStatus(), cu)) throw BizException.forbidden("无权执行财务处审批");
        } else {
            throw BizException.badRequest("当前节点不支持驳回操作");
        }
        if (wfTaskMapper.countTodo(WfBizTypes.REIMB, bizId, upper(nodeCode)) <= 0) throw BizException.conflict("当前待办已不存在");
        Map<Long, BigDecimal> amounts = sumBySubject(reimburseItemMapper.selectByReimburseId(entity.getId()));
        releaseFrozen(project.getId(), amounts);
        wfTaskMapper.cancelTodoByBiz(WfBizTypes.REIMB, bizId, "REJECT", cu.getUser().getId());
        reimburseMapper.updateStatus(entity.getId(), ReimburseStatus.REJECTED, WfNodeCodes.REJECTED, comment.trim());
        reimburseAuditLogMapper.insert(entity.getId(), "驳回报销单", entity.getStatus(), ReimburseStatus.REJECTED, cu.getUser().getId(), comment.trim());
        msgInboxMapper.insert(entity.getApplicantUserId(), "REJECT", "报销单被驳回", "报销单 " + entity.getReimburseNo() + " 已被驳回：" + comment.trim(), WfBizTypes.REIMB, bizId);
    }

    private void releaseFrozen(Long projectId, Map<Long, BigDecimal> amounts) {
        for (Map.Entry<Long, BigDecimal> e : amounts.entrySet()) {
            if (projectBudgetMapper.releaseFrozen(projectId, e.getKey(), e.getValue()) <= 0) throw BizException.serverError("释放预算冻结失败");
        }
    }

    private void consumeFrozen(Long projectId, Map<Long, BigDecimal> amounts) {
        for (Map.Entry<Long, BigDecimal> e : amounts.entrySet()) {
            if (projectBudgetMapper.consumeFrozen(projectId, e.getKey(), e.getValue()) <= 0) throw BizException.serverError("核销预算失败");
        }
    }

    private void saveItems(Long reimburseId, List<ReimburseItemReq> items) {
        for (ReimburseItemReq item : items) {
            if (budgetSubjectMapper.existsEnabled(item.getSubjectId()) <= 0) throw BizException.badRequest("预算科目不存在");
            reimburseItemMapper.insert(reimburseId, item.getSubjectId(), item.getItemName(), item.getExpenseDate(), item.getAmount(), item.getBaseAmount(), item.getTravelDays(), item.getSubsidyPerDay(), item.getSubsidyAmount(), blankToNull(item.getRemark()));
        }
    }

    private void saveAttachments(Long reimburseId, List<ReimburseAttachmentReq> attachments) {
        if (attachments == null) return;
        for (ReimburseAttachmentReq item : attachments) {
            if (item == null || isBlank(item.getFileUrl())) continue;
            reimburseFileMapper.insert(reimburseId, blankToNull(item.getFileCategory()) == null ? "OTHER" : item.getFileCategory().trim().toUpperCase(), blankToNull(item.getOriginalName()), item.getFileUrl().trim(), item.getFileSize(), userSupport.currentUser().getUser().getId());
        }
    }

    private ProjectEntity validateProjectForDraft(Long projectId, UserSupport.CurrentUser cu) {
        if (projectId == null) throw BizException.badRequest("请选择项目");
        ProjectEntity project = requireProject(projectId);
        ensureProjectVisibleForTeacher(project, cu);
        if (project.getStatus() == null || project.getStatus() != ProjectStatus.APPROVED) throw BizException.badRequest("仅立项通过项目可发起报销");
        return project;
    }

    private void ensureProjectVisibleForTeacher(ProjectEntity project, UserSupport.CurrentUser cu) {
        if (userSupport.hasAdminRole(cu.getRoles()) || userSupport.hasFinanceRole(cu.getRoles())) return;
        if (userSupport.hasUnitAdminRole(cu.getRoles())) {
            if (cu.getUser().getUnitId() != null && cu.getUser().getUnitId().equals(project.getUnitId())) return;
            throw BizException.forbidden("无权使用该项目");
        }
        if (userSupport.hasTeacherRole(cu.getRoles())) {
            if ((project.getPrincipalUserId() != null && project.getPrincipalUserId().equals(cu.getUser().getId())) || projectMemberMapper.countByProjectIdAndUserId(project.getId(), cu.getUser().getId()) > 0) return;
            throw BizException.forbidden("无权使用该项目");
        }
        throw BizException.forbidden("当前角色不能发起报销");
    }

    private void ensureReadable(ReimburseEntity entity, ProjectEntity project, UserSupport.CurrentUser cu) {
        Scope scope = resolveScope(cu.getRoles());
        if (scope.adminAll || scope.financeCenter) return;
        if (scope.unitAuditor) {
            if (cu.getUser().getUnitId() != null && cu.getUser().getUnitId().equals(entity.getUnitId())) return;
            throw BizException.forbidden("无权查看该报销单");
        }
        if (scope.teacherScope) {
            if ((entity.getApplicantUserId() != null && entity.getApplicantUserId().equals(cu.getUser().getId())) || (project.getPrincipalUserId() != null && project.getPrincipalUserId().equals(cu.getUser().getId())) || projectMemberMapper.countByProjectIdAndUserId(project.getId(), cu.getUser().getId()) > 0) return;
            throw BizException.forbidden("无权查看该报销单");
        }
        throw BizException.forbidden("无权查看该报销单");
    }

    private void ensureCanCreate(UserSupport.CurrentUser cu) {
        if (!(userSupport.hasTeacherRole(cu.getRoles()) || userSupport.hasAdminRole(cu.getRoles()))) throw BizException.forbidden("当前角色不能创建报销单");
    }

    private boolean canEdit(Long applicantUserId, Integer status, UserSupport.CurrentUser cu) {
        return applicantUserId != null && applicantUserId.equals(cu.getUser().getId()) && (status == ReimburseStatus.DRAFT || status == ReimburseStatus.REJECTED);
    }

    private boolean canSubmit(Long applicantUserId, Integer status, UserSupport.CurrentUser cu) {
        if (status == null || !(status == ReimburseStatus.DRAFT || status == ReimburseStatus.REJECTED)) return false;
        return applicantUserId != null && applicantUserId.equals(cu.getUser().getId());
    }

    private boolean canLeaderAudit(Long applicantUserId, Long principalUserId, Integer status, UserSupport.CurrentUser cu) {
        if (status == null || status != ReimburseStatus.PENDING_PI) return false;
        if (userSupport.hasAdminRole(cu.getRoles())) return true;
        return principalUserId != null && principalUserId.equals(cu.getUser().getId()) && applicantUserId != null && !applicantUserId.equals(cu.getUser().getId());
    }

    private boolean canWithdraw(Long applicantUserId, Integer status, UserSupport.CurrentUser cu) {
        if (status == null || !(status == ReimburseStatus.PENDING_PI || status == ReimburseStatus.PENDING_UNIT)) return false;
        if (userSupport.hasAdminRole(cu.getRoles())) return true;
        return applicantUserId != null && applicantUserId.equals(cu.getUser().getId());
    }

    private boolean canUnitAudit(Long unitId, Integer status, UserSupport.CurrentUser cu) {
        if (status == null || status != ReimburseStatus.PENDING_UNIT) return false;
        if (userSupport.hasAdminRole(cu.getRoles())) return true;
        return userSupport.hasUnitAdminRole(cu.getRoles()) && unitId != null && unitId.equals(cu.getUser().getUnitId());
    }

    private boolean canFinanceAudit(Integer status, UserSupport.CurrentUser cu) {
        if (status == null || status != ReimburseStatus.PENDING_FINANCE) return false;
        return userSupport.hasAdminRole(cu.getRoles()) || userSupport.hasFinanceRole(cu.getRoles());
    }

    private Map<Long, BigDecimal> sumBySubject(List<ReimburseItemVO> items) {
        Map<Long, BigDecimal> map = new LinkedHashMap<Long, BigDecimal>();
        if (items == null) return map;
        for (ReimburseItemVO item : items) {
            BigDecimal old = map.containsKey(item.getSubjectId()) ? map.get(item.getSubjectId()) : BigDecimal.ZERO;
            map.put(item.getSubjectId(), old.add(item.getAmount() == null ? BigDecimal.ZERO : item.getAmount()));
        }
        return map;
    }

    private BigDecimal sumItems(List<ReimburseItemReq> items) {
        BigDecimal total = BigDecimal.ZERO;
        for (ReimburseItemReq item : items) total = total.add(item.getAmount());
        return total;
    }

    private List<ReimburseItemReq> normalizeItems(List<ReimburseItemReq> items) {
        if (items == null || items.isEmpty()) throw BizException.badRequest("请至少填写一条报销明细");
        List<ReimburseItemReq> out = new ArrayList<ReimburseItemReq>();
        for (ReimburseItemReq item : items) {
            if (item == null) continue;
            if (item.getSubjectId() == null) throw BizException.badRequest("请选择预算科目");
            if (isBlank(item.getItemName())) throw BizException.badRequest("请填写明细名称");
            BigDecimal base = item.getBaseAmount();
            if (base == null) base = item.getAmount();
            if (base == null || base.compareTo(BigDecimal.ZERO) < 0) throw BizException.badRequest("基础金额不能小于0");
            Integer travelDays = item.getTravelDays() == null ? 0 : item.getTravelDays();
            if (travelDays < 0) throw BizException.badRequest("差旅天数不能小于0");
            BigDecimal subsidyPerDay = item.getSubsidyPerDay() == null ? BigDecimal.ZERO : item.getSubsidyPerDay();
            if (subsidyPerDay.compareTo(BigDecimal.ZERO) < 0) throw BizException.badRequest("补助标准不能小于0");
            BigDecimal subsidyAmount = travelDays > 0 && subsidyPerDay.compareTo(BigDecimal.ZERO) > 0 ? subsidyPerDay.multiply(new BigDecimal(travelDays)) : BigDecimal.ZERO;
            BigDecimal amount = base.add(subsidyAmount);
            if (amount.compareTo(BigDecimal.ZERO) <= 0) throw BizException.badRequest("报销金额必须大于0");
            ReimburseItemReq x = new ReimburseItemReq();
            x.setSubjectId(item.getSubjectId());
            x.setItemName(item.getItemName().trim());
            x.setExpenseDate(blankToNull(item.getExpenseDate()));
            x.setBaseAmount(base);
            x.setTravelDays(travelDays == 0 ? null : travelDays);
            x.setSubsidyPerDay(subsidyPerDay.compareTo(BigDecimal.ZERO) <= 0 ? null : subsidyPerDay);
            x.setSubsidyAmount(subsidyAmount.compareTo(BigDecimal.ZERO) <= 0 ? null : subsidyAmount);
            x.setAmount(amount);
            x.setRemark(blankToNull(item.getRemark()));
            out.add(x);
        }
        if (out.isEmpty()) throw BizException.badRequest("请至少填写一条报销明细");
        return out;
    }

    private ProjectEntity requireProject(Long id) {
        ProjectEntity entity = projectMapper.selectEntityById(id);
        if (entity == null) throw BizException.notFound("项目不存在");
        return entity;
    }

    private ReimburseEntity requireReimburse(Long id) {
        ReimburseEntity entity = reimburseMapper.selectEntityById(id);
        if (entity == null) throw BizException.notFound("报销单不存在");
        return entity;
    }

    private Scope resolveScope(List<String> roles) {
        Scope scope = new Scope();
        scope.adminAll = userSupport.hasAdminRole(roles);
        scope.financeCenter = userSupport.hasFinanceRole(roles);
        scope.unitAuditor = userSupport.hasUnitAdminRole(roles);
        scope.teacherScope = userSupport.hasTeacherRole(roles) && !scope.adminAll && !scope.financeCenter && !scope.unitAuditor;
        return scope;
    }

    private String generateNo() {
        String prefix = "BX-" + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE) + "-";
        int seq = reimburseMapper.countLikePrefix(prefix) + 1;
        return prefix + String.format("%04d", seq);
    }

    private boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }
    private String safeTrim(String s) { return s == null ? null : s.trim(); }
    private String blankToNull(String s) { String v = safeTrim(s); return v == null || v.isEmpty() ? null : v; }
    private String nonBlank(String s, String msg) { if (isBlank(s)) throw BizException.badRequest(msg); return s; }
    private String upper(String s) { return safeTrim(s) == null ? null : safeTrim(s).toUpperCase(); }

    private static class Scope {
        private boolean adminAll;
        private boolean teacherScope;
        private boolean unitAuditor;
        private boolean financeCenter;
    }
}
