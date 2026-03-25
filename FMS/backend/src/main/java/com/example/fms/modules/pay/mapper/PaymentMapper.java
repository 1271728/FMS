package com.example.fms.modules.pay.mapper;

import com.example.fms.modules.pay.dto.PaymentVO;
import org.apache.ibatis.annotations.*;

@Mapper
public interface PaymentMapper {

    @Select("SELECT p.id, p.reimb_id AS reimbId, p.pay_method AS payMethod, p.voucher_no AS voucherNo, p.pay_amount AS payAmount, DATE_FORMAT(p.paid_at, '%Y-%m-%d %H:%i:%s') AS paidAt, p.operator_user_id AS operatorUserId, su.real_name AS operatorName, DATE_FORMAT(p.created_at, '%Y-%m-%d %H:%i:%s') AS createdAt, DATE_FORMAT(p.updated_at, '%Y-%m-%d %H:%i:%s') AS updatedAt FROM rf_payment p LEFT JOIN sys_user su ON su.id = p.operator_user_id WHERE p.reimb_id = #{reimbId} LIMIT 1")
    PaymentVO selectByReimbId(@Param("reimbId") Long reimbId);

    @Select("SELECT COUNT(1) FROM rf_payment WHERE reimb_id = #{reimbId}")
    int countByReimbId(@Param("reimbId") Long reimbId);

    @Insert("INSERT INTO rf_payment(reimb_id, pay_method, voucher_no, pay_amount, paid_at, operator_user_id, created_at, updated_at) VALUES(#{reimbId}, #{payMethod}, #{voucherNo}, #{payAmount}, #{paidAt}, #{operatorUserId}, NOW(), NOW())")
    int insert(@Param("reimbId") Long reimbId,
               @Param("payMethod") String payMethod,
               @Param("voucherNo") String voucherNo,
               @Param("payAmount") java.math.BigDecimal payAmount,
               @Param("paidAt") String paidAt,
               @Param("operatorUserId") Long operatorUserId);

    @Update("UPDATE rf_payment SET pay_method = #{payMethod}, voucher_no = #{voucherNo}, pay_amount = #{payAmount}, paid_at = #{paidAt}, operator_user_id = #{operatorUserId}, updated_at = NOW() WHERE reimb_id = #{reimbId}")
    int updateByReimbId(@Param("reimbId") Long reimbId,
                        @Param("payMethod") String payMethod,
                        @Param("voucherNo") String voucherNo,
                        @Param("payAmount") java.math.BigDecimal payAmount,
                        @Param("paidAt") String paidAt,
                        @Param("operatorUserId") Long operatorUserId);
}
