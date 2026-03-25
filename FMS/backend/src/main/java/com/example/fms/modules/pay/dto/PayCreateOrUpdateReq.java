package com.example.fms.modules.pay.dto;

import java.math.BigDecimal;

public class PayCreateOrUpdateReq {
    private Long reimbId;
    private String payMethod;
    private String voucherNo;
    private BigDecimal payAmount;
    private String paidAt;

    public Long getReimbId() { return reimbId; }
    public void setReimbId(Long reimbId) { this.reimbId = reimbId; }
    public String getPayMethod() { return payMethod; }
    public void setPayMethod(String payMethod) { this.payMethod = payMethod; }
    public String getVoucherNo() { return voucherNo; }
    public void setVoucherNo(String voucherNo) { this.voucherNo = voucherNo; }
    public BigDecimal getPayAmount() { return payAmount; }
    public void setPayAmount(BigDecimal payAmount) { this.payAmount = payAmount; }
    public String getPaidAt() { return paidAt; }
    public void setPaidAt(String paidAt) { this.paidAt = paidAt; }
}
