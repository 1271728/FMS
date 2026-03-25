package com.example.fms.modules.budgetAdjust.dto;

import java.math.BigDecimal;

public class BudgetAdjustLineVO {
    private Long id;
    private Long adjustId;
    private Long subjectId;
    private String subjectCode;
    private String subjectName;
    private BigDecimal deltaAmount;
    private String remark;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getAdjustId() { return adjustId; }
    public void setAdjustId(Long adjustId) { this.adjustId = adjustId; }
    public Long getSubjectId() { return subjectId; }
    public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }
    public String getSubjectCode() { return subjectCode; }
    public void setSubjectCode(String subjectCode) { this.subjectCode = subjectCode; }
    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }
    public BigDecimal getDeltaAmount() { return deltaAmount; }
    public void setDeltaAmount(BigDecimal deltaAmount) { this.deltaAmount = deltaAmount; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}
