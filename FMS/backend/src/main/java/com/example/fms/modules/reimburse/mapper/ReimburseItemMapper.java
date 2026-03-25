package com.example.fms.modules.reimburse.mapper;

import com.example.fms.modules.reimburse.dto.ReimburseItemVO;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface ReimburseItemMapper {

    @Select("SELECT ri.id, ri.reimb_id AS reimburseId, ri.subject_id AS subjectId, bs.subject_code AS subjectCode, bs.subject_name AS subjectName, ri.description AS itemName, DATE_FORMAT(ri.expense_date, '%Y-%m-%d') AS expenseDate, ri.amount, ri.base_amount AS baseAmount, ri.travel_days AS travelDays, ri.subsidy_per_day AS subsidyPerDay, ri.subsidy_amount AS subsidyAmount, ri.remark FROM rf_reimb_line ri JOIN rf_budget_subject bs ON bs.id = ri.subject_id WHERE ri.reimb_id = #{reimburseId} ORDER BY ri.id ASC")
    List<ReimburseItemVO> selectByReimburseId(@Param("reimburseId") Long reimburseId);

    @Insert("INSERT INTO rf_reimb_line(reimb_id, subject_id, description, expense_date, amount, base_amount, travel_days, subsidy_per_day, subsidy_amount, tax_amount, vendor_name, remark) VALUES(#{reimburseId}, #{subjectId}, #{itemName}, #{expenseDate}, #{amount}, #{baseAmount}, #{travelDays}, #{subsidyPerDay}, #{subsidyAmount}, 0, NULL, #{remark})")
    int insert(@Param("reimburseId") Long reimburseId,
               @Param("subjectId") Long subjectId,
               @Param("itemName") String itemName,
               @Param("expenseDate") String expenseDate,
               @Param("amount") BigDecimal amount,
               @Param("baseAmount") BigDecimal baseAmount,
               @Param("travelDays") Integer travelDays,
               @Param("subsidyPerDay") BigDecimal subsidyPerDay,
               @Param("subsidyAmount") BigDecimal subsidyAmount,
               @Param("remark") String remark);

    @Delete("DELETE FROM rf_reimb_line WHERE reimb_id = #{reimburseId}")
    int deleteByReimburseId(@Param("reimburseId") Long reimburseId);
}
