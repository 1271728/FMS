package com.example.fms.modules.admin.org.dto;

import java.util.ArrayList;
import java.util.List;

public class OrgTreeNode {
    private Long id;
    private Long parentId;
    private String code;
    private String name;
    private Integer enabled;
    private Integer sortNo;
    private List<OrgTreeNode> children = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getEnabled() { return enabled; }
    public void setEnabled(Integer enabled) { this.enabled = enabled; }
    public Integer getSortNo() { return sortNo; }
    public void setSortNo(Integer sortNo) { this.sortNo = sortNo; }
    public List<OrgTreeNode> getChildren() { return children; }
    public void setChildren(List<OrgTreeNode> children) { this.children = children; }
}
