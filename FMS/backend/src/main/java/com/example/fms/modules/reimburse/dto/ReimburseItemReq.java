package com.example.fms.modules.reimburse.dto;

import java.math.BigDecimal;

public class ReimburseItemReq {
    private Long subjectId;
    private String itemName;
    private String expenseDate;
    private BigDecimal amount;
    private BigDecimal baseAmount;
    private Integer travelDays;
    private BigDecimal subsidyPerDay;
    private BigDecimal subsidyAmount;
    private String remark;

    public Long getSubjectId() { return subjectId; }
    public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }
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
