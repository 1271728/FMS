package com.example.fms.modules.msg.dto;

public class MsgVO {
    private Long id;
    private String msgType;
    private String title;
    private String content;
    private String relatedBizType;
    private Long relatedBizId;
    private Integer isRead;
    private String readAt;
    private String createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getMsgType() { return msgType; }
    public void setMsgType(String msgType) { this.msgType = msgType; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getRelatedBizType() { return relatedBizType; }
    public void setRelatedBizType(String relatedBizType) { this.relatedBizType = relatedBizType; }
    public Long getRelatedBizId() { return relatedBizId; }
    public void setRelatedBizId(Long relatedBizId) { this.relatedBizId = relatedBizId; }
    public Integer getIsRead() { return isRead; }
    public void setIsRead(Integer isRead) { this.isRead = isRead; }
    public String getReadAt() { return readAt; }
    public void setReadAt(String readAt) { this.readAt = readAt; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
