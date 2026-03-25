package com.example.fms.modules.dashboard.service.impl;

import com.example.fms.modules.dashboard.mapper.DashboardMapper;
import com.example.fms.modules.dashboard.service.DashboardService;
import com.example.fms.modules.msg.mapper.MsgCenterMapper;
import com.example.fms.modules.shared.support.UserSupport;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final DashboardMapper dashboardMapper;
    private final MsgCenterMapper msgCenterMapper;
    private final UserSupport userSupport;

    public DashboardServiceImpl(DashboardMapper dashboardMapper,
                                MsgCenterMapper msgCenterMapper,
                                UserSupport userSupport) {
        this.dashboardMapper = dashboardMapper;
        this.msgCenterMapper = msgCenterMapper;
        this.userSupport = userSupport;
    }

    @Override
    public Map<String, Object> stats() {
        UserSupport.CurrentUser cu = userSupport.currentUser();
        Map<String, Object> data = new LinkedHashMap<String, Object>();
        List<Map<String, Object>> cards = new ArrayList<Map<String, Object>>();
        data.put("unreadCount", msgCenterMapper.countUnread(cu.getUser().getId()));

        if (userSupport.hasAdminRole(cu.getRoles())) {
            data.put("role", "ADMIN");
            cards.add(card("activeUsers", "启用用户数", dashboardMapper.countActiveUsers()));
            cards.add(card("approvedProjects", "立项通过项目", dashboardMapper.countApprovedProjects()));
            cards.add(card("reimburseCount", "报销单总数", dashboardMapper.countAllReimb()));
            cards.add(card("todoCount", "系统待办数", dashboardMapper.countAllTodo()));
        } else if (userSupport.hasFinanceRole(cu.getRoles())) {
            data.put("role", "FINANCE");
            cards.add(card("finReviewTodo", "待财务复核", dashboardMapper.countFinanceReviewTodo()));
            cards.add(card("payArchiveTodo", "待支付归档", dashboardMapper.countFinancePayTodo()));
            cards.add(card("monthPaidAmount", "本月支付金额", dashboardMapper.sumFinanceMonthPaidAmount()));
            cards.add(card("monthArchived", "本月归档数", dashboardMapper.countFinanceMonthArchived()));
        } else if (userSupport.hasUnitAdminRole(cu.getRoles())) {
            data.put("role", "UNIT_ADMIN");
            cards.add(card("unitTodo", "本单位待审核", dashboardMapper.countUnitTodo(cu.getUser().getUnitId())));
            cards.add(card("unitMonthApprovedAmount", "本月通过金额", dashboardMapper.sumUnitMonthApprovedAmount(cu.getUser().getUnitId())));
            cards.add(card("unitRejected", "本单位驳回数", dashboardMapper.countUnitRejected(cu.getUser().getUnitId())));
            cards.add(card("unitProjectCount", "本单位立项项目", dashboardMapper.countUnitApprovedProject(cu.getUser().getUnitId())));
        } else {
            data.put("role", "PI");
            cards.add(card("draftCount", "草稿数", dashboardMapper.countPiDraft(cu.getUser().getId())));
            cards.add(card("processingCount", "审批中报销", dashboardMapper.countPiProcessing(cu.getUser().getId())));
            cards.add(card("rejectedCount", "已驳回待修改", dashboardMapper.countPiRejected(cu.getUser().getId())));
            cards.add(card("monthSubmittedAmount", "本月提交金额", dashboardMapper.sumPiMonthSubmittedAmount(cu.getUser().getId())));
        }
        data.put("cards", cards);
        return data;
    }

    private Map<String, Object> card(String key, String label, Object value) {
        Map<String, Object> m = new LinkedHashMap<String, Object>();
        m.put("key", key);
        m.put("label", label);
        m.put("value", value == null ? BigDecimal.ZERO : value);
        return m;
    }
}
