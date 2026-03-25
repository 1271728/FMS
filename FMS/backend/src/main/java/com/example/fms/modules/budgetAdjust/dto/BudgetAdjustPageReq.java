package com.example.fms.modules.budgetAdjust.dto;

public class BudgetAdjustPageReq {
    private Integer pageNo;
    private Integer pageSize;
    private String keyword;
    private Long projectId;
    private Integer status;
    private Boolean todoOnly;

    public Integer getPageNo() { return pageNo; }
    public void setPageNo(Integer pageNo) { this.pageNo = pageNo; }
    public Integer getPageSize() { return pageSize; }
    public void setPageSize(Integer pageSize) { this.pageSize = pageSize; }
    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }
    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public Boolean getTodoOnly() { return todoOnly; }
    public void setTodoOnly(Boolean todoOnly) { this.todoOnly = todoOnly; }
}
