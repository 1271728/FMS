package com.example.fms.modules.report.service.impl;

import com.example.fms.common.exception.BizException;
import com.example.fms.modules.project.dto.ProjectEntity;
import com.example.fms.modules.project.mapper.ProjectMapper;
import com.example.fms.modules.report.dto.ReimbStatReq;
import com.example.fms.modules.report.mapper.ReportMapper;
import com.example.fms.modules.report.service.ReportService;
import com.example.fms.modules.shared.support.UserSupport;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportServiceImpl implements ReportService {

    private final ReportMapper reportMapper;
    private final ProjectMapper projectMapper;
    private final UserSupport userSupport;

    public ReportServiceImpl(ReportMapper reportMapper,
                             ProjectMapper projectMapper,
                             UserSupport userSupport) {
        this.reportMapper = reportMapper;
        this.projectMapper = projectMapper;
        this.userSupport = userSupport;
    }

    @Override
    public List<Map<String, Object>> budgetExecution(Long projectId) {
        if (projectId == null) throw BizException.badRequest("projectId不能为空");
        UserSupport.CurrentUser cu = userSupport.currentUser();
        ProjectEntity project = projectMapper.selectEntityById(projectId);
        if (project == null) throw BizException.notFound("项目不存在");
        if (!userSupport.canReadProject(project, cu)) throw BizException.forbidden("无权查看该项目预算执行");
        return reportMapper.budgetExecution(projectId);
    }

    @Override
    public Map<String, Object> reimbStat(ReimbStatReq req) {
        UserSupport.CurrentUser cu = userSupport.currentUser();
        if (!(userSupport.hasUnitAdminRole(cu.getRoles()) || userSupport.hasFinanceRole(cu.getRoles()) || userSupport.hasAdminRole(cu.getRoles()))) {
            throw BizException.forbidden("当前角色无统计权限");
        }
        Long unitId = req == null ? null : req.getUnitId();
        if (userSupport.hasUnitAdminRole(cu.getRoles()) && !userSupport.hasAdminRole(cu.getRoles()) && !userSupport.hasFinanceRole(cu.getRoles())) {
            unitId = cu.getUser().getUnitId();
        }
        Long projectId = req == null ? null : req.getProjectId();
        String dateFrom = req == null ? null : blankToNull(req.getDateFrom());
        String dateTo = req == null ? null : blankToNull(req.getDateTo());
        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("summary", reportMapper.reimbSummary(unitId, projectId, dateFrom, dateTo));
        data.put("byStatus", reportMapper.reimbStatByStatus(unitId, projectId, dateFrom, dateTo));
        return data;
    }

    private String blankToNull(String v) {
        return v == null || v.trim().isEmpty() ? null : v.trim();
    }
}
