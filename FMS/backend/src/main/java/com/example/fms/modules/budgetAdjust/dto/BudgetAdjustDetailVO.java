package com.example.fms.modules.budgetAdjust.dto;

import com.example.fms.modules.workflow.dto.WfLogVO;

import java.util.List;

public class BudgetAdjustDetailVO extends BudgetAdjustVO {
    private String createdAt;
    private String updatedAt;
    private String effectiveAt;
    private String currentNode;
    private String lastComment;
    private List<BudgetAdjustLineVO> lines;
    private List<WfLogVO> wfLogs;

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
    public String getEffectiveAt() { return effectiveAt; }
    public void setEffectiveAt(String effectiveAt) { this.effectiveAt = effectiveAt; }
    public String getCurrentNode() { return currentNode; }
    public void setCurrentNode(String currentNode) { this.currentNode = currentNode; }
    public String getLastComment() { return lastComment; }
    public void setLastComment(String lastComment) { this.lastComment = lastComment; }
    public List<BudgetAdjustLineVO> getLines() { return lines; }
    public void setLines(List<BudgetAdjustLineVO> lines) { this.lines = lines; }
    public List<WfLogVO> getWfLogs() { return wfLogs; }
    public void setWfLogs(List<WfLogVO> wfLogs) { this.wfLogs = wfLogs; }
}
