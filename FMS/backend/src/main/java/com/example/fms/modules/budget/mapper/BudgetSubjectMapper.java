package com.example.fms.modules.budget.mapper;

import com.example.fms.modules.budget.dto.BudgetSubjectNode;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BudgetSubjectMapper {

    @Select("SELECT id, parent_id AS parentId, subject_code AS code, subject_name AS name, level_no AS levelNo, sort_no AS sortNo, enabled FROM rf_budget_subject WHERE enabled = 1 ORDER BY level_no ASC, sort_no ASC, id ASC")
    List<BudgetSubjectNode> selectAllEnabled();

    @Select("SELECT id FROM rf_budget_subject WHERE subject_code = #{code} LIMIT 1")
    Long selectIdByCode(@Param("code") String code);

    @Select("SELECT COUNT(1) FROM rf_budget_subject WHERE id = #{id} AND enabled = 1")
    int existsEnabled(@Param("id") Long id);

    @Select("SELECT subject_code FROM rf_budget_subject WHERE id = #{id} LIMIT 1")
    String selectCodeById(@Param("id") Long id);

    @Select("SELECT id FROM rf_budget_subject WHERE enabled = 1 AND is_leaf = 1 ORDER BY sort_no ASC, id ASC")
    List<Long> selectAllEnabledLeafIds();
}
