package com.example.fms.modules.project.dto;

import java.math.BigDecimal;
import java.util.List;

public class ProjectUpdateReq {
    private Long id;
    private String projectCode;
    private String projectName;
    private String projectType;
    private String startDate;
    private String endDate;
    private BigDecimal totalBudget;
    private Integer status;
    private String description;
    private List<ProjectBudgetLineReq> budgetLines;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getProjectCode() { return projectCode; }
    public void setProjectCode(String projectCode) { this.projectCode = projectCode; }
    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }
    public String getProjectType() { return projectType; }
    public void setProjectType(String projectType) { this.projectType = projectType; }
    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
    public BigDecimal getTotalBudget() { return totalBudget; }
    public void setTotalBudget(BigDecimal totalBudget) { this.totalBudget = totalBudget; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<ProjectBudgetLineReq> getBudgetLines() { return budgetLines; }
    public void setBudgetLines(List<ProjectBudgetLineReq> budgetLines) { this.budgetLines = budgetLines; }
}
