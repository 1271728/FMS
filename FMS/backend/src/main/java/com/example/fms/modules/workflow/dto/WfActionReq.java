package com.example.fms.modules.workflow.dto;

public class WfActionReq {
    private String bizType;
    private Long bizId;
    private String nodeCode;
    private String comment;

    public String getBizType() { return bizType; }
    public void setBizType(String bizType) { this.bizType = bizType; }
    public Long getBizId() { return bizId; }
    public void setBizId(Long bizId) { this.bizId = bizId; }
    public String getNodeCode() { return nodeCode; }
    public void setNodeCode(String nodeCode) { this.nodeCode = nodeCode; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}
