package com.example.fms.modules.admin.user.dto;

/**
 * 用户分页查询
 */
public class AdminUserPageReq {
    private Integer pageNo;
    private Integer pageSize;
    private Long unitId;
    private Integer status;
    private String keyword; // username/realName/phone/email

    public Integer getPageNo() { return pageNo; }
    public void setPageNo(Integer pageNo) { this.pageNo = pageNo; }
    public Integer getPageSize() { return pageSize; }
    public void setPageSize(Integer pageSize) { this.pageSize = pageSize; }
    public Long getUnitId() { return unitId; }
    public void setUnitId(Long unitId) { this.unitId = unitId; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }
}
