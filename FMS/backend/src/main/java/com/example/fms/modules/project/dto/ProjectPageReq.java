package com.example.fms.modules.project.dto;

public class ProjectPageReq {
    private Integer pageNo;
    private Integer pageSize;
    private String keyword;
    private Integer status;
    private Boolean todoOnly;
    private String viewType;

    public Integer getPageNo() { return pageNo; }
    public void setPageNo(Integer pageNo) { this.pageNo = pageNo; }

    public Integer getPageSize() { return pageSize; }
    public void setPageSize(Integer pageSize) { this.pageSize = pageSize; }

    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public Boolean getTodoOnly() { return todoOnly; }
    public void setTodoOnly(Boolean todoOnly) { this.todoOnly = todoOnly; }

    public String getViewType() { return viewType; }
    public void setViewType(String viewType) { this.viewType = viewType; }
}
