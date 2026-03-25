package com.example.fms.modules.dashboard.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;

@Mapper
public interface DashboardMapper {

    @Select("SELECT COUNT(1) FROM rf_reimb WHERE applicant_id = #{userId} AND status = 0")
    int countPiDraft(@Param("userId") Long userId);

    @Select("SELECT COUNT(1) FROM rf_reimb WHERE applicant_id = #{userId} AND status IN (1,2,3,7)")
    int countPiProcessing(@Param("userId") Long userId);

    @Select("SELECT COUNT(1) FROM rf_reimb WHERE applicant_id = #{userId} AND status = 5")
    int countPiRejected(@Param("userId") Long userId);

    @Select("SELECT COALESCE(SUM(total_amount), 0) FROM rf_reimb WHERE applicant_id = #{userId} AND submitted_at IS NOT NULL AND YEAR(submitted_at) = YEAR(CURDATE()) AND MONTH(submitted_at) = MONTH(CURDATE())")
    BigDecimal sumPiMonthSubmittedAmount(@Param("userId") Long userId);

    @Select("SELECT COUNT(1) FROM wf_task WHERE task_status = 'TODO' AND assignee_role_code = 'UNIT_ADMIN' AND node_code = 'UNIT_AUDIT' AND unit_id = #{unitId}")
    int countUnitTodo(@Param("unitId") Long unitId);

    @Select("SELECT COALESCE(SUM(total_amount), 0) FROM rf_reimb WHERE unit_id = #{unitId} AND status IN (3,4) AND updated_at IS NOT NULL AND YEAR(updated_at) = YEAR(CURDATE()) AND MONTH(updated_at) = MONTH(CURDATE())")
    BigDecimal sumUnitMonthApprovedAmount(@Param("unitId") Long unitId);

    @Select("SELECT COUNT(1) FROM rf_reimb WHERE unit_id = #{unitId} AND status = 5")
    int countUnitRejected(@Param("unitId") Long unitId);

    @Select("SELECT COUNT(1) FROM rf_project WHERE unit_id = #{unitId} AND status = 3")
    int countUnitApprovedProject(@Param("unitId") Long unitId);

    @Select("SELECT COUNT(1) FROM wf_task WHERE task_status = 'TODO' AND assignee_role_code = 'FINANCE' AND node_code = 'FIN_REVIEW'")
    int countFinanceReviewTodo();

    @Select("SELECT COUNT(1) FROM wf_task WHERE task_status = 'TODO' AND assignee_role_code = 'FINANCE' AND node_code = 'PAY_ARCHIVE'")
    int countFinancePayTodo();

    @Select("SELECT COALESCE(SUM(pay_amount), 0) FROM rf_payment WHERE paid_at IS NOT NULL AND YEAR(paid_at) = YEAR(CURDATE()) AND MONTH(paid_at) = MONTH(CURDATE())")
    BigDecimal sumFinanceMonthPaidAmount();

    @Select("SELECT COUNT(1) FROM rf_archive WHERE archived_at IS NOT NULL AND YEAR(archived_at) = YEAR(CURDATE()) AND MONTH(archived_at) = MONTH(CURDATE())")
    int countFinanceMonthArchived();

    @Select("SELECT COUNT(1) FROM sys_user WHERE status = 1")
    int countActiveUsers();

    @Select("SELECT COUNT(1) FROM rf_project WHERE status = 3")
    int countApprovedProjects();

    @Select("SELECT COUNT(1) FROM rf_reimb")
    int countAllReimb();

    @Select("SELECT COUNT(1) FROM wf_task WHERE task_status = 'TODO'")
    int countAllTodo();
}
