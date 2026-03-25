package com.example.fms.modules.reimburse.dto;

public class ReimburseAuditReq {
    private Long reimburseId;
    private String action;
    private String comment;

    public Long getReimburseId() { return reimburseId; }
    public void setReimburseId(Long reimburseId) { this.reimburseId = reimburseId; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}
