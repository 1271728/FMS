package com.example.fms.modules.budget.mapper;

import com.example.fms.modules.budget.dto.ProjectBudgetVO;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface ProjectBudgetMapper {

    @Select("SELECT pb.id, pb.project_id AS projectId, pb.subject_id AS subjectId, bs.subject_code AS subjectCode, bs.subject_name AS subjectName, pb.approved_amount AS approvedAmount, pb.used_amount AS usedAmount, CASE WHEN pb.adjusted_amount < 0 THEN (0 - pb.adjusted_amount) ELSE 0 END AS frozenAmount, (pb.approved_amount + pb.adjusted_amount - pb.used_amount) AS availableAmount FROM rf_project_budget pb JOIN rf_budget_subject bs ON bs.id = pb.subject_id WHERE pb.project_id = #{projectId} ORDER BY bs.level_no ASC, bs.sort_no ASC, bs.id ASC")
    List<ProjectBudgetVO> selectByProjectId(@Param("projectId") Long projectId);

    @Select("SELECT COUNT(1) FROM rf_project_budget WHERE project_id = #{projectId}")
    int countByProjectId(@Param("projectId") Long projectId);

    @Select("SELECT COUNT(1) FROM rf_project_budget WHERE project_id = #{projectId} AND subject_id = #{subjectId}")
    int countByProjectIdAndSubjectId(@Param("projectId") Long projectId, @Param("subjectId") Long subjectId);

    @Insert("INSERT INTO rf_project_budget(project_id, subject_id, approved_amount, adjusted_amount, used_amount, created_at, updated_at) VALUES(#{projectId}, #{subjectId}, #{approvedAmount}, 0, 0, NOW(), NOW())")
    int insert(@Param("projectId") Long projectId, @Param("subjectId") Long subjectId, @Param("approvedAmount") BigDecimal approvedAmount);

    @Insert("INSERT INTO rf_project_budget(project_id, subject_id, approved_amount, adjusted_amount, used_amount, created_at, updated_at) VALUES(#{projectId}, #{subjectId}, 0, #{adjustedAmount}, 0, NOW(), NOW())")
    int insertWithAdjusted(@Param("projectId") Long projectId, @Param("subjectId") Long subjectId, @Param("adjustedAmount") BigDecimal adjustedAmount);

    @Update("UPDATE rf_project_budget SET adjusted_amount = adjusted_amount + #{amount}, updated_at = NOW() WHERE project_id = #{projectId} AND subject_id = #{subjectId}")
    int increaseAdjusted(@Param("projectId") Long projectId, @Param("subjectId") Long subjectId, @Param("amount") BigDecimal amount);

    @Update("UPDATE rf_project_budget SET adjusted_amount = adjusted_amount - #{amount}, updated_at = NOW() WHERE project_id = #{projectId} AND subject_id = #{subjectId} AND (approved_amount + adjusted_amount - used_amount) >= #{amount}")
    int decreaseAdjustedSafely(@Param("projectId") Long projectId, @Param("subjectId") Long subjectId, @Param("amount") BigDecimal amount);

    @Update("UPDATE rf_project_budget SET adjusted_amount = adjusted_amount - #{amount}, updated_at = NOW() WHERE project_id = #{projectId} AND subject_id = #{subjectId} AND (approved_amount + adjusted_amount - used_amount) >= #{amount}")
    int freeze(@Param("projectId") Long projectId, @Param("subjectId") Long subjectId, @Param("amount") BigDecimal amount);

    @Update("UPDATE rf_project_budget SET adjusted_amount = adjusted_amount + #{amount}, updated_at = NOW() WHERE project_id = #{projectId} AND subject_id = #{subjectId} AND (adjusted_amount < (0 - #{amount}) OR adjusted_amount = (0 - #{amount}))")
    int releaseFrozen(@Param("projectId") Long projectId, @Param("subjectId") Long subjectId, @Param("amount") BigDecimal amount);

    @Update("UPDATE rf_project_budget SET adjusted_amount = adjusted_amount + #{amount}, used_amount = used_amount + #{amount}, updated_at = NOW() WHERE project_id = #{projectId} AND subject_id = #{subjectId} AND (adjusted_amount < (0 - #{amount}) OR adjusted_amount = (0 - #{amount}))")
    int consumeFrozen(@Param("projectId") Long projectId, @Param("subjectId") Long subjectId, @Param("amount") BigDecimal amount);

    @Select("SELECT COALESCE(SUM(approved_amount), 0) FROM rf_project_budget WHERE project_id = #{projectId}")
    BigDecimal sumApprovedAmountByProjectId(@Param("projectId") Long projectId);

    @Update("UPDATE rf_project_budget SET approved_amount = #{approvedAmount}, updated_at = NOW() WHERE project_id = #{projectId} AND subject_id = #{subjectId}")
    int updateApprovedAmount(@Param("projectId") Long projectId, @Param("subjectId") Long subjectId, @Param("approvedAmount") BigDecimal approvedAmount);
}
