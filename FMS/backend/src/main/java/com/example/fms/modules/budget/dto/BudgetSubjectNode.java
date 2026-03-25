package com.example.fms.modules.budget.dto;

import java.util.ArrayList;
import java.util.List;

public class BudgetSubjectNode {
    private Long id;
    private Long parentId;
    private String code;
    private String name;
    private Integer levelNo;
    private Integer sortNo;
    private Integer enabled;
    private List<BudgetSubjectNode> children = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getLevelNo() { return levelNo; }
    public void setLevelNo(Integer levelNo) { this.levelNo = levelNo; }
    public Integer getSortNo() { return sortNo; }
    public void setSortNo(Integer sortNo) { this.sortNo = sortNo; }
    public Integer getEnabled() { return enabled; }
    public void setEnabled(Integer enabled) { this.enabled = enabled; }
    public List<BudgetSubjectNode> getChildren() { return children; }
    public void setChildren(List<BudgetSubjectNode> children) { this.children = children; }
}
