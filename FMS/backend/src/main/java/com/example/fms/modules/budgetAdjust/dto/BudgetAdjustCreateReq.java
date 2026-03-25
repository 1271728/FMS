package com.example.fms.modules.budgetAdjust.dto;

import java.util.List;

public class BudgetAdjustCreateReq {
    private Long projectId;
    private String reason;
    private List<BudgetAdjustLineReq> lines;

    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public List<BudgetAdjustLineReq> getLines() { return lines; }
    public void setLines(List<BudgetAdjustLineReq> lines) { this.lines = lines; }
}
