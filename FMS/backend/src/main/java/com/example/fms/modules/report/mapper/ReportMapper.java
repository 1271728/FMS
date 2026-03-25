package com.example.fms.modules.report.mapper;

import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface ReportMapper {

    @Select("SELECT pb.subject_id AS subjectId, bs.subject_code AS subjectCode, bs.subject_name AS subjectName, pb.approved_amount AS approvedAmount, pb.adjusted_amount AS adjustedAmount, pb.used_amount AS usedAmount, (pb.approved_amount + pb.adjusted_amount - pb.used_amount) AS availableAmount, CASE WHEN (pb.approved_amount + pb.adjusted_amount) > 0 THEN ROUND(pb.used_amount / (pb.approved_amount + pb.adjusted_amount) * 100, 2) ELSE 0 END AS executionRate FROM rf_project_budget pb JOIN rf_budget_subject bs ON bs.id = pb.subject_id WHERE pb.project_id = #{projectId} ORDER BY bs.level_no ASC, bs.sort_no ASC, bs.id ASC")
    List<Map<String, Object>> budgetExecution(@Param("projectId") Long projectId);

    @Select({
            "<script>",
            "SELECT COUNT(1) AS orderCount, COALESCE(SUM(r.total_amount), 0) AS totalAmount ",
            "FROM rf_reimb r ",
            "WHERE 1 = 1 ",
            "  <if test='unitId != null'> AND r.unit_id = #{unitId} </if>",
            "  <if test='projectId != null'> AND r.project_id = #{projectId} </if>",
            "  <if test='dateFrom != null and dateFrom != \"\"'> AND DATE(COALESCE(r.submitted_at, r.created_at)) >= #{dateFrom} </if>",
            "  <if test='dateTo != null and dateTo != \"\"'> AND DATE(COALESCE(r.submitted_at, r.created_at)) &lt;= #{dateTo} </if>",
            "</script>"
    })
    Map<String, Object> reimbSummary(@Param("unitId") Long unitId,
                                     @Param("projectId") Long projectId,
                                     @Param("dateFrom") String dateFrom,
                                     @Param("dateTo") String dateTo);

    @Select({
            "<script>",
            "SELECT r.status AS status, COUNT(1) AS orderCount, COALESCE(SUM(r.total_amount), 0) AS totalAmount ",
            "FROM rf_reimb r ",
            "WHERE 1 = 1 ",
            "  <if test='unitId != null'> AND r.unit_id = #{unitId} </if>",
            "  <if test='projectId != null'> AND r.project_id = #{projectId} </if>",
            "  <if test='dateFrom != null and dateFrom != \"\"'> AND DATE(COALESCE(r.submitted_at, r.created_at)) >= #{dateFrom} </if>",
            "  <if test='dateTo != null and dateTo != \"\"'> AND DATE(COALESCE(r.submitted_at, r.created_at)) &lt;= #{dateTo} </if>",
            "GROUP BY r.status ORDER BY r.status ASC",
            "</script>"
    })
    List<Map<String, Object>> reimbStatByStatus(@Param("unitId") Long unitId,
                                                @Param("projectId") Long projectId,
                                                @Param("dateFrom") String dateFrom,
                                                @Param("dateTo") String dateTo);
}
