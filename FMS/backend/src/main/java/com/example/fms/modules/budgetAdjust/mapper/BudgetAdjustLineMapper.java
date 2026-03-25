package com.example.fms.modules.budgetAdjust.mapper;

import com.example.fms.modules.budgetAdjust.dto.BudgetAdjustLineVO;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface BudgetAdjustLineMapper {

    @Select("SELECT l.id, l.adjust_id AS adjustId, l.subject_id AS subjectId, s.subject_code AS subjectCode, s.subject_name AS subjectName, l.delta_amount AS deltaAmount, l.remark FROM rf_budget_adjust_line l JOIN rf_budget_subject s ON s.id = l.subject_id WHERE l.adjust_id = #{adjustId} ORDER BY l.id ASC")
    List<BudgetAdjustLineVO> selectByAdjustId(@Param("adjustId") Long adjustId);

    @Insert("INSERT INTO rf_budget_adjust_line(adjust_id, subject_id, delta_amount, remark) VALUES(#{adjustId}, #{subjectId}, #{deltaAmount}, #{remark})")
    int insert(@Param("adjustId") Long adjustId,
               @Param("subjectId") Long subjectId,
               @Param("deltaAmount") BigDecimal deltaAmount,
               @Param("remark") String remark);

    @Delete("DELETE FROM rf_budget_adjust_line WHERE adjust_id = #{adjustId}")
    int deleteByAdjustId(@Param("adjustId") Long adjustId);
}
