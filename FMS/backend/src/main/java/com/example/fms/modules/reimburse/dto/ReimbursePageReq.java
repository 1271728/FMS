package com.example.fms.modules.reimburse.dto;

public class ReimbursePageReq {
    private Integer pageNo;
    private Integer pageSize;
    private String keyword;
    private Integer status;
    private Long projectId;
    private Boolean todoOnly;

    public Integer getPageNo() { return pageNo; }
    public void setPageNo(Integer pageNo) { this.pageNo = pageNo; }
    public Integer getPageSize() { return pageSize; }
    public void setPageSize(Integer pageSize) { this.pageSize = pageSize; }
    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }
    public Boolean getTodoOnly() { return todoOnly; }
    public void setTodoOnly(Boolean todoOnly) { this.todoOnly = todoOnly; }
}
