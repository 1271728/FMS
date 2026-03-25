package com.example.fms.modules.project.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.example.fms.common.api.PageResult;
import com.example.fms.common.exception.BizException;
import com.example.fms.modules.auth.entity.SysUser;
import com.example.fms.modules.auth.mapper.RbacMapper;
import com.example.fms.modules.auth.mapper.SysUserMapper;
import com.example.fms.modules.budget.dto.ProjectBudgetVO;
import com.example.fms.modules.budget.mapper.BudgetSubjectMapper;
import com.example.fms.modules.budget.mapper.ProjectBudgetMapper;
import com.example.fms.modules.budget.service.BudgetService;
import com.example.fms.modules.project.dto.*;
import com.example.fms.modules.project.mapper.ProjectAuditLogMapper;
import com.example.fms.modules.project.mapper.ProjectMapper;
import com.example.fms.modules.project.mapper.ProjectMemberMapper;
import com.example.fms.modules.project.service.ProjectService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
@Transactional(rollbackFor = Exception.class)
public class ProjectServiceImpl implements ProjectService {

    private final ProjectMapper projectMapper;
    private final ProjectMemberMapper projectMemberMapper;
    private final ProjectAuditLogMapper projectAuditLogMapper;
    private final SysUserMapper sysUserMapper;
    private final RbacMapper rbacMapper;
    private final BudgetService budgetService;
    private final ProjectBudgetMapper projectBudgetMapper;
    private final BudgetSubjectMapper budgetSubjectMapper;

    public ProjectServiceImpl(ProjectMapper projectMapper,
                              ProjectMemberMapper projectMemberMapper,
                              ProjectAuditLogMapper projectAuditLogMapper,
                              SysUserMapper sysUserMapper,
                              RbacMapper rbacMapper,
                              BudgetService budgetService,
                              ProjectBudgetMapper projectBudgetMapper,
                              BudgetSubjectMapper budgetSubjectMapper) {
        this.projectMapper = projectMapper;
        this.projectMemberMapper = projectMemberMapper;
        this.projectAuditLogMapper = projectAuditLogMapper;
        this.sysUserMapper = sysUserMapper;
        this.rbacMapper = rbacMapper;
        this.budgetService = budgetService;
        this.projectBudgetMapper = projectBudgetMapper;
        this.budgetSubjectMapper = budgetSubjectMapper;
    }

    @Override
    public PageResult<ProjectVO> page(ProjectPageReq req) {
        CurrentUser cu = currentUser();
        Scope scope = resolveScope(cu.roles);
        int pageNo = req == null || req.getPageNo() == null || req.getPageNo() < 1 ? 1 : req.getPageNo();
        int pageSize = req == null || req.getPageSize() == null || req.getPageSize() < 1 ? 10 : req.getPageSize();
        String keyword = req == null ? null : safeTrim(req.getKeyword());
        Integer status = req == null ? null : req.getStatus();
        Boolean todoOnly = req == null ? null : req.getTodoOnly();
        String viewType = req == null ? null : safeTrim(req.getViewType());
        if (!("lead".equals(viewType) || "joined".equals(viewType))) viewType = null;
        int offset = (pageNo - 1) * pageSize;

        long total = projectMapper.count(keyword, status, todoOnly, viewType, cu.user.getId(), cu.user.getUnitId(), scope.adminAll, scope.teacherScope, scope.unitAuditor, scope.financeCenter);
        List<ProjectVO> records = projectMapper.selectPage(offset, pageSize, keyword, status, todoOnly, viewType, cu.user.getId(), cu.user.getUnitId(), scope.adminAll, scope.teacherScope, scope.unitAuditor, scope.financeCenter);
        for (ProjectVO item : records) {
            item.setProjectType(parseProjectType(item.getProjectType()));
            item.setCanEdit(canEdit(item.getPrincipalUserId(), item.getStatus(), cu) ? 1 : 0);
            item.setCanSubmit(canSubmit(item.getPrincipalUserId(), item.getStatus(), cu) ? 1 : 0);
            item.setCanManageMembers(canManageMembers(item.getPrincipalUserId(), item.getStatus(), cu) ? 1 : 0);
            item.setCanUnitAudit(canUnitAudit(item.getUnitId(), item.getStatus(), cu) ? 1 : 0);
            item.setCanFinanceAudit(canFinanceAudit(item.getStatus(), cu) ? 1 : 0);
            item.setCanWithdraw(canWithdraw(item.getPrincipalUserId(), item.getStatus(), cu) ? 1 : 0);
        }
        return new PageResult<ProjectVO>(records, total, pageNo, pageSize);
    }

    @Override
    public ProjectDetailVO detail(Long id) {
        if (id == null) throw BizException.badRequest("id不能为空");
        CurrentUser cu = currentUser();
        ProjectEntity entity = requireProject(id);
        ensureReadable(entity, cu);
        ProjectDetailVO detail = projectMapper.selectDetailById(id);
        if (detail == null) throw BizException.notFound("项目不存在");
        detail.setProjectType(parseProjectType(detail.getProjectType()));
        detail.setDescription(parseProjectDescription(detail.getDescription()));
        detail.setCanEdit(canEdit(entity.getPrincipalUserId(), entity.getStatus(), cu) ? 1 : 0);
        detail.setCanSubmit(canSubmit(entity.getPrincipalUserId(), entity.getStatus(), cu) ? 1 : 0);
        detail.setCanManageMembers(canManageMembers(entity.getPrincipalUserId(), entity.getStatus(), cu) ? 1 : 0);
        detail.setCanUnitAudit(canUnitAudit(entity.getUnitId(), entity.getStatus(), cu) ? 1 : 0);
        detail.setCanFinanceAudit(canFinanceAudit(entity.getStatus(), cu) ? 1 : 0);
        detail.setCanWithdraw(canWithdraw(entity.getPrincipalUserId(), entity.getStatus(), cu) ? 1 : 0);
        detail.setMemberCount(projectMemberMapper.selectByProjectId(id).size());
        detail.setAuditLogs(projectAuditLogMapper.selectByProjectId(id));
        return detail;
    }

    @Override
    public Long create(ProjectCreateReq req) {
        if (req == null) throw BizException.badRequest("请求不能为空");
        CurrentUser cu = currentUser();
        ensureCanCreate(cu);
        validateBaseFields(req.getProjectCode(), req.getProjectName());
        if (projectMapper.countByProjectCode(req.getProjectCode().trim(), null) > 0) {
            throw BizException.conflict("项目编号已存在");
        }

        ProjectEntity entity = new ProjectEntity();
        entity.setProjectCode(req.getProjectCode().trim());
        entity.setProjectName(req.getProjectName().trim());
        entity.setProjectType(safeTrim(req.getProjectType()));
        entity.setPrincipalUserId(cu.user.getId());
        entity.setUnitId(cu.user.getUnitId());
        entity.setStartDate(blankToNull(req.getStartDate()));
        entity.setEndDate(blankToNull(req.getEndDate()));
        entity.setTotalBudget(req.getTotalBudget());
        entity.setStatus(ProjectStatus.DRAFT);
        entity.setDescription(composeProjectRemark(req.getProjectType(), req.getDescription()));
        projectMapper.insert(entity);
        if (entity.getId() == null) throw BizException.serverError("项目创建失败");
        budgetService.ensureProjectBudgetSubjects(entity.getId(), null);
        saveBudgetAllocations(entity.getId(), req.getBudgetLines());
        projectAuditLogMapper.insert(entity.getId(), "创建项目", null, ProjectStatus.DRAFT, cu.user.getId(), null);
        return entity.getId();
    }

    @Override
    public void update(ProjectUpdateReq req) {
        if (req == null || req.getId() == null) throw BizException.badRequest("id不能为空");
        CurrentUser cu = currentUser();
        ProjectEntity old = requireProject(req.getId());
        ensureEditable(old, cu);
        validateBaseFields(req.getProjectCode(), req.getProjectName());
        if (projectMapper.countByProjectCode(req.getProjectCode().trim(), req.getId()) > 0) {
            throw BizException.conflict("项目编号已存在");
        }
        old.setProjectCode(req.getProjectCode().trim());
        old.setProjectName(req.getProjectName().trim());
        old.setProjectType(safeTrim(req.getProjectType()));
        old.setStartDate(blankToNull(req.getStartDate()));
        old.setEndDate(blankToNull(req.getEndDate()));
        old.setTotalBudget(req.getTotalBudget());
        old.setDescription(composeProjectRemark(req.getProjectType(), req.getDescription()));
        projectMapper.updateDraft(old);
        budgetService.ensureProjectBudgetSubjects(old.getId(), null);
        saveBudgetAllocations(old.getId(), req.getBudgetLines());
        projectAuditLogMapper.insert(old.getId(), "编辑项目", old.getStatus(), old.getStatus(), cu.user.getId(), null);
    }

    @Override
    public void delete(ProjectDeleteReq req) {
        if (req == null || req.getId() == null) throw BizException.badRequest("id不能为空");
        CurrentUser cu = currentUser();
        ProjectEntity entity = requireProject(req.getId());
        if (!canDelete(entity.getPrincipalUserId(), entity.getStatus(), cu)) throw BizException.forbidden("仅草稿项目可删除");
        if (projectMapper.countReimburseByProjectId(entity.getId()) > 0 || projectMapper.countBudgetAdjustByProjectId(entity.getId()) > 0) {
            throw BizException.conflict("项目已关联业务单据，不能删除");
        }
        projectMapper.deleteMembersByProjectId(entity.getId());
        projectMapper.deleteBudgetsByProjectId(entity.getId());
        projectMapper.deleteWfTasksByProjectId(entity.getId());
        projectMapper.deleteWfLogsByProjectId(entity.getId());
        projectMapper.deleteById(entity.getId());
    }

    @Override
    public void submit(ProjectSubmitReq req) {
        if (req == null || req.getId() == null) throw BizException.badRequest("id不能为空");
        CurrentUser cu = currentUser();
        ProjectEntity entity = requireProject(req.getId());
        if (!canSubmit(entity.getPrincipalUserId(), entity.getStatus(), cu)) throw BizException.forbidden("当前状态不可提交立项审批");
        validateSubmitFields(entity);
        validateBudgetAllocationForSubmit(entity.getId(), entity.getTotalBudget());
        Integer fromStatus = entity.getStatus();
        projectMapper.markSubmitted(entity.getId(), ProjectStatus.PENDING_UNIT);
        projectAuditLogMapper.insert(entity.getId(), "提交立项审批", fromStatus, ProjectStatus.PENDING_UNIT, cu.user.getId(), null);
    }

    @Override
    public void withdraw(ProjectWithdrawReq req) {
        if (req == null || req.getId() == null) throw BizException.badRequest("id不能为空");
        CurrentUser cu = currentUser();
        ProjectEntity entity = requireProject(req.getId());
        if (!canWithdraw(entity.getPrincipalUserId(), entity.getStatus(), cu)) throw BizException.forbidden("当前状态不可撤回");
        Integer fromStatus = entity.getStatus();
        projectMapper.markWithdrawnToDraft(entity.getId(), ProjectStatus.DRAFT);
        projectAuditLogMapper.insert(entity.getId(), "撤回审批", fromStatus, ProjectStatus.DRAFT, cu.user.getId(), null);
    }

    @Override
    public void audit(ProjectAuditReq req) {
        if (req == null || req.getProjectId() == null) throw BizException.badRequest("projectId不能为空");
        CurrentUser cu = currentUser();
        ProjectEntity entity = requireProject(req.getProjectId());
        String action = safeTrim(req.getAction());
        String comment = safeTrim(req.getComment());
        if (!"pass".equalsIgnoreCase(action) && !"reject".equalsIgnoreCase(action)) {
            throw BizException.badRequest("action仅支持pass/reject");
        }
        boolean pass = "pass".equalsIgnoreCase(action);
        if (!pass && isBlank(comment)) throw BizException.badRequest("驳回时请填写意见");

        if (entity.getStatus() == ProjectStatus.PENDING_UNIT) {
            if (!canUnitAudit(entity.getUnitId(), entity.getStatus(), cu)) throw BizException.forbidden("无权执行二级单位审批");
            int toStatus = pass ? ProjectStatus.PENDING_FINANCE : ProjectStatus.REJECTED;
            projectMapper.auditUnit(entity.getId(), toStatus, cu.user.getId(), blankToNull(comment));
            projectAuditLogMapper.insert(entity.getId(), pass ? "二级单位审批通过" : "二级单位驳回", entity.getStatus(), toStatus, cu.user.getId(), blankToNull(comment));
            return;
        }

        if (entity.getStatus() == ProjectStatus.PENDING_FINANCE) {
            if (!canFinanceAudit(entity.getStatus(), cu)) throw BizException.forbidden("无权执行财务处审批");
            int toStatus = pass ? ProjectStatus.APPROVED : ProjectStatus.REJECTED;
            projectMapper.auditFinance(entity.getId(), toStatus, cu.user.getId(), blankToNull(comment));
            if (pass) budgetService.ensureProjectBudgetSubjects(entity.getId(), entity.getTotalBudget());
            projectAuditLogMapper.insert(entity.getId(), pass ? "财务处审批通过" : "财务处驳回", entity.getStatus(), toStatus, cu.user.getId(), blankToNull(comment));
            return;
        }

        throw BizException.badRequest("当前项目不在可审批状态");
    }

    @Override
    public List<ProjectMemberVO> members(Long projectId) {
        if (projectId == null) throw BizException.badRequest("projectId不能为空");
        CurrentUser cu = currentUser();
        ProjectEntity entity = requireProject(projectId);
        ensureReadable(entity, cu);
        return projectMemberMapper.selectByProjectId(projectId);
    }

    @Override
    public void addMember(ProjectAddMemberReq req) {
        if (req == null || req.getProjectId() == null) throw BizException.badRequest("projectId不能为空");
        CurrentUser cu = currentUser();
        ProjectEntity entity = requireProject(req.getProjectId());
        if (!canManageMembers(entity.getPrincipalUserId(), entity.getStatus(), cu)) {
            throw BizException.forbidden("当前状态不可维护项目成员");
        }
        String username = safeTrim(req.getUsername());
        if (isBlank(username)) throw BizException.badRequest("请输入成员账号");
        SysUser target = sysUserMapper.selectByUsername(username);
        if (target == null) throw BizException.notFound("成员账号不存在");
        if (target.getStatus() == null || target.getStatus() != 1) throw BizException.badRequest("成员账号已禁用");
        if (entity.getPrincipalUserId() != null && entity.getPrincipalUserId().equals(target.getId())) {
            throw BizException.badRequest("项目负责人无需重复添加为成员");
        }
        if (projectMemberMapper.countByProjectIdAndUserId(req.getProjectId(), target.getId()) > 0) {
            throw BizException.conflict("该成员已在项目中");
        }
        projectMemberMapper.insert(req.getProjectId(), target.getId(), isBlank(req.getMemberRole()) ? "项目成员" : req.getMemberRole().trim());
        projectAuditLogMapper.insert(req.getProjectId(), "添加项目成员", entity.getStatus(), entity.getStatus(), cu.user.getId(), target.getUsername());
    }

    @Override
    public void removeMember(ProjectRemoveMemberReq req) {
        if (req == null || req.getProjectId() == null || req.getUserId() == null) throw BizException.badRequest("projectId/userId不能为空");
        CurrentUser cu = currentUser();
        ProjectEntity entity = requireProject(req.getProjectId());
        if (!canManageMembers(entity.getPrincipalUserId(), entity.getStatus(), cu)) {
            throw BizException.forbidden("当前状态不可维护项目成员");
        }
        if (entity.getPrincipalUserId() != null && entity.getPrincipalUserId().equals(req.getUserId())) {
            throw BizException.badRequest("项目负责人不能从成员中移除");
        }
        projectMemberMapper.delete(req.getProjectId(), req.getUserId());
        projectAuditLogMapper.insert(req.getProjectId(), "移除项目成员", entity.getStatus(), entity.getStatus(), cu.user.getId(), String.valueOf(req.getUserId()));
    }

    private void validateBaseFields(String projectCode, String projectName) {
        if (isBlank(projectCode) || isBlank(projectName)) throw BizException.badRequest("projectCode/projectName不能为空");
    }

    private void validateSubmitFields(ProjectEntity entity) {
        if (isBlank(entity.getProjectCode()) || isBlank(entity.getProjectName()) || isBlank(entity.getProjectType())) {
            throw BizException.badRequest("提交审批前请补全项目编号、项目名称、项目类型");
        }
        if (isBlank(entity.getStartDate()) || isBlank(entity.getEndDate())) {
            throw BizException.badRequest("提交审批前请补全项目起止日期");
        }
        if (entity.getTotalBudget() == null) throw BizException.badRequest("提交审批前请填写项目预算总额");
    }

    private void validateBudgetAllocationForSubmit(Long projectId, BigDecimal totalBudget) {
        if (projectId == null) throw BizException.badRequest("projectId不能为空");
        budgetService.ensureProjectBudgetSubjects(projectId, null);
        List<ProjectBudgetVO> rows = projectBudgetMapper.selectByProjectId(projectId);
        BigDecimal total = BigDecimal.ZERO;
        boolean hasPositive = false;
        if (rows != null) {
            for (ProjectBudgetVO row : rows) {
                BigDecimal amt = row.getApprovedAmount() == null ? BigDecimal.ZERO : row.getApprovedAmount();
                total = total.add(amt);
                if (amt.compareTo(BigDecimal.ZERO) > 0) hasPositive = true;
            }
        }
        if (!hasPositive) throw BizException.badRequest("提交审批前请先完成预算分配");
        BigDecimal expect = totalBudget == null ? BigDecimal.ZERO : totalBudget;
        if (total.compareTo(expect) != 0) {
            throw BizException.badRequest("预算分配合计必须与预算总额一致，当前分配合计为：" + total.toPlainString());
        }
    }

    private void saveBudgetAllocations(Long projectId, List<ProjectBudgetLineReq> budgetLines) {
        budgetService.ensureProjectBudgetSubjects(projectId, null);
        Map<Long, BigDecimal> input = new LinkedHashMap<Long, BigDecimal>();
        if (budgetLines != null) {
            for (ProjectBudgetLineReq line : budgetLines) {
                if (line == null || line.getSubjectId() == null) continue;
                if (budgetSubjectMapper.existsEnabled(line.getSubjectId()) <= 0) throw BizException.badRequest("预算科目不存在");
                BigDecimal amt = line.getApprovedAmount() == null ? BigDecimal.ZERO : line.getApprovedAmount();
                if (amt.compareTo(BigDecimal.ZERO) < 0) throw BizException.badRequest("预算分配金额不能为负数");
                input.put(line.getSubjectId(), amt);
            }
        }
        List<ProjectBudgetVO> exists = projectBudgetMapper.selectByProjectId(projectId);
        for (ProjectBudgetVO row : exists) {
            BigDecimal amt = input.containsKey(row.getSubjectId()) ? input.get(row.getSubjectId()) : BigDecimal.ZERO;
            projectBudgetMapper.updateApprovedAmount(projectId, row.getSubjectId(), amt);
        }
    }

    private void ensureCanCreate(CurrentUser cu) {
        if (!(hasTeacherRole(cu.roles) || hasAdminRole(cu.roles))) throw BizException.forbidden("当前角色不能创建项目");
    }

    private boolean canEdit(Long principalUserId, Integer status, CurrentUser cu) {
        if (hasAdminRole(cu.roles)) return true;
        return principalUserId != null && principalUserId.equals(cu.user.getId()) && (status == ProjectStatus.DRAFT || status == ProjectStatus.REJECTED);
    }

    private boolean canDelete(Long principalUserId, Integer status, CurrentUser cu) {
        if (status == null || status != ProjectStatus.DRAFT) return false;
        if (hasAdminRole(cu.roles)) return true;
        return principalUserId != null && principalUserId.equals(cu.user.getId());
    }

    private boolean canSubmit(Long principalUserId, Integer status, CurrentUser cu) {
        if (hasAdminRole(cu.roles)) return status == ProjectStatus.DRAFT || status == ProjectStatus.REJECTED;
        return principalUserId != null && principalUserId.equals(cu.user.getId()) && (status == ProjectStatus.DRAFT || status == ProjectStatus.REJECTED);
    }

    private boolean canManageMembers(Long principalUserId, Integer status, CurrentUser cu) {
        if (hasAdminRole(cu.roles)) return status != ProjectStatus.CLOSED && status != ProjectStatus.DISABLED;
        return principalUserId != null && principalUserId.equals(cu.user.getId()) && (status == ProjectStatus.DRAFT || status == ProjectStatus.REJECTED || status == ProjectStatus.APPROVED);
    }

    private boolean canWithdraw(Long principalUserId, Integer status, CurrentUser cu) {
        if (status == null || !(status == ProjectStatus.PENDING_UNIT || status == ProjectStatus.PENDING_FINANCE)) return false;
        if (hasAdminRole(cu.roles)) return true;
        return principalUserId != null && principalUserId.equals(cu.user.getId());
    }

    private boolean canUnitAudit(Long unitId, Integer status, CurrentUser cu) {
        if (status == null || status != ProjectStatus.PENDING_UNIT) return false;
        if (hasAdminRole(cu.roles)) return true;
        return hasFinUnitRole(cu.roles) && unitId != null && unitId.equals(cu.user.getUnitId());
    }

    private boolean canFinanceAudit(Integer status, CurrentUser cu) {
        if (status == null || status != ProjectStatus.PENDING_FINANCE) return false;
        return hasAdminRole(cu.roles) || hasFinCenterRole(cu.roles);
    }

    private void ensureEditable(ProjectEntity entity, CurrentUser cu) {
        if (!canEdit(entity.getPrincipalUserId(), entity.getStatus(), cu)) {
            throw BizException.forbidden("仅项目负责人可编辑草稿/被驳回项目，管理员可特殊维护");
        }
    }

    private void ensureReadable(ProjectEntity entity, CurrentUser cu) {
        Scope scope = resolveScope(cu.roles);
        if (scope.adminAll || scope.financeCenter) return;
        if (scope.unitAuditor) {
            if (cu.user.getUnitId() != null && cu.user.getUnitId().equals(entity.getUnitId())) return;
            throw BizException.forbidden("无权查看该项目");
        }
        if (scope.teacherScope) {
            if ((entity.getPrincipalUserId() != null && entity.getPrincipalUserId().equals(cu.user.getId())) || projectMemberMapper.countByProjectIdAndUserId(entity.getId(), cu.user.getId()) > 0) return;
            throw BizException.forbidden("无权查看该项目");
        }
        if (cu.user.getUnitId() != null && cu.user.getUnitId().equals(entity.getUnitId())) return;
        throw BizException.forbidden("无权查看该项目");
    }

    private String composeProjectRemark(String projectType, String description) {
        String type = blankToNull(projectType);
        String desc = blankToNull(description);
        if (type == null && desc == null) return null;
        StringBuilder sb = new StringBuilder();
        if (type != null) sb.append("[PT]").append(type);
        if (desc != null) {
            if (sb.length() > 0) sb.append("\n");
            sb.append("[DESC]").append(desc);
        }
        return sb.toString();
    }

    private String parseProjectType(String remark) {
        if (isBlank(remark)) return null;
        String x = remark.trim();
        int pt = x.indexOf("[PT]");
        if (pt >= 0) {
            int end = x.indexOf("\n[DESC]");
            if (end < 0) end = x.length();
            String v = x.substring(pt + 4, end).trim();
            return v.isEmpty() ? null : v;
        }
        for (String part : x.split("；")) {
            if (part.startsWith("项目类型=")) {
                String v = part.substring("项目类型=".length()).trim();
                return v.isEmpty() ? null : v;
            }
        }
        return null;
    }

    private String parseProjectDescription(String remark) {
        if (isBlank(remark)) return null;
        String x = remark.trim();
        int desc = x.indexOf("[DESC]");
        if (desc >= 0) {
            String v = x.substring(desc + 6).trim();
            return v.isEmpty() ? null : v;
        }
        if (x.startsWith("项目说明=")) {
            String v = x.substring("项目说明=".length()).trim();
            return v.isEmpty() ? null : v;
        }
        for (String part : x.split("；")) {
            if (part.startsWith("项目说明=")) {
                String v = part.substring("项目说明=".length()).trim();
                return v.isEmpty() ? null : v;
            }
        }
        return parseProjectType(x) == null ? x : null;
    }

    private ProjectEntity requireProject(Long id) {
        ProjectEntity entity = projectMapper.selectEntityById(id);
        if (entity == null) throw BizException.notFound("项目不存在");
        entity.setProjectType(parseProjectType(entity.getDescription()));
        entity.setDescription(parseProjectDescription(entity.getDescription()));
        return entity;
    }

    private Scope resolveScope(List<String> roles) {
        Scope scope = new Scope();
        scope.adminAll = hasAdminRole(roles);
        scope.financeCenter = hasFinCenterRole(roles);
        scope.unitAuditor = hasFinUnitRole(roles);
        scope.teacherScope = hasTeacherRole(roles) && !scope.unitAuditor && !scope.financeCenter && !scope.adminAll;
        return scope;
    }

    private CurrentUser currentUser() {
        StpUtil.checkLogin();
        Long userId = StpUtil.getLoginIdAsLong();
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) throw BizException.unauthorized("用户不存在");
        CurrentUser cu = new CurrentUser();
        cu.user = user;
        cu.roles = rbacMapper.selectRoleCodesByUserId(userId);
        return cu;
    }

    private boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }
    private String safeTrim(String s) { return s == null ? null : s.trim(); }
    private String blankToNull(String s) { String val = safeTrim(s); return val == null || val.isEmpty() ? null : val; }

    private static class Scope {
        private boolean adminAll;
        private boolean teacherScope;
        private boolean unitAuditor;
        private boolean financeCenter;
    }

    private boolean hasAdminRole(List<String> roles) { return hasAnyRole(roles, "ADMIN"); }
    private boolean hasTeacherRole(List<String> roles) { return hasAnyRole(roles, "PI"); }
    private boolean hasFinUnitRole(List<String> roles) { return hasAnyRole(roles, "UNIT_ADMIN"); }
    private boolean hasFinCenterRole(List<String> roles) { return hasAnyRole(roles, "FINANCE"); }
    private boolean hasAnyRole(List<String> roles, String... candidates) {
        if (roles == null || roles.isEmpty()) return false;
        for (String role : roles) {
            if (role == null) continue;
            String x = role.trim().toUpperCase();
            for (String c : candidates) if (x.equals(c)) return true;
        }
        return false;
    }

    private static class CurrentUser {
        private SysUser user;
        private List<String> roles;
    }
}
