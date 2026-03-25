package com.example.fms.modules.reimburse.dto;

import java.math.BigDecimal;

public class ReimburseItemVO {
    private Long id;
    private Long reimburseId;
    private Long subjectId;
    private String subjectCode;
    private String subjectName;
    private String itemName;
    private String expenseDate;
    private BigDecimal amount;
    private BigDecimal baseAmount;
    private Integer travelDays;
    private BigDecimal subsidyPerDay;
    private BigDecimal subsidyAmount;
    private String remark;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getReimburseId() { return reimburseId; }
    public void setReimburseId(Long reimburseId) { this.reimburseId = reimburseId; }
    public Long getSubjectId() { return subjectId; }
    public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }
    public String getSubjectCode() { return subjectCode; }
    public void setSubjectCode(String subjectCode) { this.subjectCode = subjectCode; }
    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }
    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    public String getExpenseDate() { return expenseDate; }
    public void setExpenseDate(String expenseDate) { this.expenseDate = expenseDate; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public BigDecimal getBaseAmount() { return baseAmount; }
    public void setBaseAmount(BigDecimal baseAmount) { this.baseAmount = baseAmount; }
    public Integer getTravelDays() { return travelDays; }
    public void setTravelDays(Integer travelDays) { this.travelDays = travelDays; }
    public BigDecimal getSubsidyPerDay() { return subsidyPerDay; }
    public void setSubsidyPerDay(BigDecimal subsidyPerDay) { this.subsidyPerDay = subsidyPerDay; }
    public BigDecimal getSubsidyAmount() { return subsidyAmount; }
    public void setSubsidyAmount(BigDecimal subsidyAmount) { this.subsidyAmount = subsidyAmount; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}
