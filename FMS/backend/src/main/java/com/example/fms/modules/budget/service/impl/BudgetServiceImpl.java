package com.example.fms.modules.budget.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.example.fms.common.exception.BizException;
import com.example.fms.modules.auth.entity.SysUser;
import com.example.fms.modules.auth.mapper.RbacMapper;
import com.example.fms.modules.auth.mapper.SysUserMapper;
import com.example.fms.modules.budget.dto.BudgetSubjectNode;
import com.example.fms.modules.budget.dto.ProjectBudgetVO;
import com.example.fms.modules.budget.mapper.BudgetSubjectMapper;
import com.example.fms.modules.budget.mapper.ProjectBudgetMapper;
import com.example.fms.modules.budget.service.BudgetService;
import com.example.fms.modules.project.dto.ProjectEntity;
import com.example.fms.modules.project.dto.ProjectStatus;
import com.example.fms.modules.project.mapper.ProjectMapper;
import com.example.fms.modules.project.mapper.ProjectMemberMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
@Transactional(rollbackFor = Exception.class)
public class BudgetServiceImpl implements BudgetService {

    private final BudgetSubjectMapper budgetSubjectMapper;
    private final ProjectBudgetMapper projectBudgetMapper;
    private final ProjectMapper projectMapper;
    private final ProjectMemberMapper projectMemberMapper;
    private final SysUserMapper sysUserMapper;
    private final RbacMapper rbacMapper;

    public BudgetServiceImpl(BudgetSubjectMapper budgetSubjectMapper,
                             ProjectBudgetMapper projectBudgetMapper,
                             ProjectMapper projectMapper,
                             ProjectMemberMapper projectMemberMapper,
                             SysUserMapper sysUserMapper,
                             RbacMapper rbacMapper) {
        this.budgetSubjectMapper = budgetSubjectMapper;
        this.projectBudgetMapper = projectBudgetMapper;
        this.projectMapper = projectMapper;
        this.projectMemberMapper = projectMemberMapper;
        this.sysUserMapper = sysUserMapper;
        this.rbacMapper = rbacMapper;
    }

    @Override
    public List<BudgetSubjectNode> subjectTree() {
        List<BudgetSubjectNode> all = budgetSubjectMapper.selectAllEnabled();
        Map<Long, BudgetSubjectNode> map = new LinkedHashMap<>();
        List<BudgetSubjectNode> roots = new ArrayList<>();
        for (BudgetSubjectNode node : all) map.put(node.getId(), node);
        for (BudgetSubjectNode node : all) {
            if (node.getParentId() == null || node.getParentId() == 0 || !map.containsKey(node.getParentId())) roots.add(node);
            else map.get(node.getParentId()).getChildren().add(node);
        }
        return roots;
    }

    @Override
    public List<ProjectBudgetVO> projectBudgetList(Long projectId) {
        if (projectId == null) throw BizException.badRequest("projectId不能为空");
        CurrentUser cu = currentUser();
        ProjectEntity project = requireProject(projectId);
        ensureReadable(project, cu);
        ensureProjectBudgetSubjects(projectId, project.getStatus() != null && project.getStatus() == ProjectStatus.APPROVED ? project.getTotalBudget() : null);
        return projectBudgetMapper.selectByProjectId(projectId);
    }

    @Override
    public void ensureProjectBudgetSubjects(Long projectId, BigDecimal totalBudget) {
        fillMissingProjectBudgetSubjects(projectId, totalBudget);
    }

    @Override
    public void ensureDefaultBudgetLedger(Long projectId, BigDecimal totalBudget) {
        ensureProjectBudgetSubjects(projectId, totalBudget);
    }

    @Override
    public Map<String, Object> backfillLegacyProjectBudgets() {
        CurrentUser cu = currentUser();
        if (!(hasAdminRole(cu.roles) || hasFinCenterRole(cu.roles))) {
            throw BizException.forbidden("仅管理员或财务处可执行旧项目预算科目补齐");
        }
        List<ProjectEntity> projects = projectMapper.selectAllForBudgetInit();
        int projectCount = 0;
        int insertedRows = 0;
        for (ProjectEntity item : projects) {
            BigDecimal seedTotal = item.getStatus() != null && item.getStatus() == ProjectStatus.APPROVED ? item.getTotalBudget() : null;
            BudgetInitStats stats = fillMissingProjectBudgetSubjects(item.getId(), seedTotal);
            if (stats.changed) {
                projectCount++;
            }
            insertedRows += stats.insertedRows;
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("projectCount", projectCount);
        result.put("insertedRows", insertedRows);
        result.put("message", projectCount > 0 ? "旧项目预算科目已补齐" : "没有需要补齐的项目");
        return result;
    }


    private BudgetInitStats fillMissingProjectBudgetSubjects(Long projectId, BigDecimal totalBudget) {
        if (projectId == null) return BudgetInitStats.empty();
        List<Long> leafIds = budgetSubjectMapper.selectAllEnabledLeafIds();
        if (leafIds == null || leafIds.isEmpty()) {
            throw BizException.serverError("预算科目未初始化，请先补齐 rf_budget_subject");
        }

        boolean changed = false;
        int existedCount = projectBudgetMapper.countByProjectId(projectId);
        Long otherId = budgetSubjectMapper.selectIdByCode("OTHER");
        Long fallbackSubjectId = otherId != null ? otherId : leafIds.get(leafIds.size() - 1);
        int inserted = 0;
        for (Long subjectId : leafIds) {
            if (projectBudgetMapper.countByProjectIdAndSubjectId(projectId, subjectId) > 0) continue;
            projectBudgetMapper.insert(projectId, subjectId, BigDecimal.ZERO);
            inserted++;
            changed = true;
        }

        if (totalBudget != null) {
            BigDecimal approvedSum = projectBudgetMapper.sumApprovedAmountByProjectId(projectId);
            if (approvedSum == null || approvedSum.compareTo(BigDecimal.ZERO) == 0) {
                int updated = projectBudgetMapper.updateApprovedAmount(projectId, fallbackSubjectId, totalBudget);
                changed = changed || updated > 0;
            }
        }
        return new BudgetInitStats(inserted, changed);
    }

    private ProjectEntity requireProject(Long id) {
        ProjectEntity entity = projectMapper.selectEntityById(id);
        if (entity == null) throw BizException.notFound("项目不存在");
        return entity;
    }

    private void ensureReadable(ProjectEntity entity, CurrentUser cu) {
        boolean admin = hasAdminRole(cu.roles);
        boolean finCenter = hasFinCenterRole(cu.roles);
        boolean finUnit = hasFinUnitRole(cu.roles);
        boolean teacher = hasTeacherRole(cu.roles);
        if (admin || finCenter) return;
        if (finUnit) {
            if (cu.user.getUnitId() != null && cu.user.getUnitId().equals(entity.getUnitId())) return;
            throw BizException.forbidden("无权查看该项目预算");
        }
        if (teacher) {
            if ((entity.getPrincipalUserId() != null && entity.getPrincipalUserId().equals(cu.user.getId())) || projectMemberMapper.countByProjectIdAndUserId(entity.getId(), cu.user.getId()) > 0) return;
            throw BizException.forbidden("无权查看该项目预算");
        }
        throw BizException.forbidden("无权查看该项目预算");
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
    private static class BudgetInitStats {
        private final int insertedRows;
        private final boolean changed;

        private BudgetInitStats(int insertedRows, boolean changed) {
            this.insertedRows = insertedRows;
            this.changed = changed;
        }

        private static BudgetInitStats empty() {
            return new BudgetInitStats(0, false);
        }
    }

    private static class CurrentUser {
        private SysUser user;
        private List<String> roles;
    }
}
