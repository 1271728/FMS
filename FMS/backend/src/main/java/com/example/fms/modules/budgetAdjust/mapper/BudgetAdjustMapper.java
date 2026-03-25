package com.example.fms.modules.budgetAdjust.mapper;

import com.example.fms.modules.budgetAdjust.dto.BudgetAdjustDetailVO;
import com.example.fms.modules.budgetAdjust.dto.BudgetAdjustEntity;
import com.example.fms.modules.budgetAdjust.dto.BudgetAdjustVO;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface BudgetAdjustMapper {

    @Select({
            "<script>",
            "SELECT ba.id, ba.adjust_no AS adjustNo, ba.project_id AS projectId, p.project_code AS projectCode, p.project_name AS projectName, ba.applicant_id AS applicantId, su.real_name AS applicantName, ba.unit_id AS unitId, ou.name AS unitName, ba.reason, ba.total_delta AS totalDelta, ba.status, DATE_FORMAT(ba.submitted_at, '%Y-%m-%d %H:%i:%s') AS submittedAt ",
            "FROM rf_budget_adjust ba ",
            "JOIN rf_project p ON p.id = ba.project_id ",
            "LEFT JOIN sys_user su ON su.id = ba.applicant_id ",
            "LEFT JOIN org_unit ou ON ou.id = ba.unit_id ",
            "<where>",
            "  <if test='keyword != null and keyword != \"\"'> AND (ba.adjust_no LIKE CONCAT('%',#{keyword},'%') OR ba.reason LIKE CONCAT('%',#{keyword},'%') OR p.project_name LIKE CONCAT('%',#{keyword},'%')) </if>",
            "  <if test='projectId != null'> AND ba.project_id = #{projectId} </if>",
            "  <if test='status != null'> AND ba.status = #{status} </if>",
            "  <if test='todoOnly != null and todoOnly'>",
            "    <choose>",
            "      <when test='adminAll'> AND ba.status IN (1,2) </when>",
            "      <when test='financeCenter'> AND ba.status = 2 </when>",
            "      <when test='unitAuditor'> AND ba.status = 1 </when>",
            "      <otherwise> AND 1 = 2 </otherwise>",
            "    </choose>",
            "  </if>",
            "  <if test='adminAll == false'>",
            "    <choose>",
            "      <when test='financeCenter'> AND 1 = 1 </when>",
            "      <when test='unitAuditor'> AND ba.unit_id = #{unitId} </when>",
            "      <when test='teacherScope'> AND ba.applicant_id = #{userId} </when>",
            "      <otherwise> AND ba.unit_id = #{unitId} </otherwise>",
            "    </choose>",
            "  </if>",
            "</where>",
            "ORDER BY ba.id DESC LIMIT #{offset}, #{pageSize}",
            "</script>"
    })
    List<BudgetAdjustVO> selectPage(@Param("offset") int offset,
                                    @Param("pageSize") int pageSize,
                                    @Param("keyword") String keyword,
                                    @Param("projectId") Long projectId,
                                    @Param("status") Integer status,
                                    @Param("todoOnly") Boolean todoOnly,
                                    @Param("userId") Long userId,
                                    @Param("unitId") Long unitId,
                                    @Param("adminAll") boolean adminAll,
                                    @Param("teacherScope") boolean teacherScope,
                                    @Param("unitAuditor") boolean unitAuditor,
                                    @Param("financeCenter") boolean financeCenter);

    @Select({
            "<script>",
            "SELECT COUNT(1) FROM rf_budget_adjust ba JOIN rf_project p ON p.id = ba.project_id ",
            "<where>",
            "  <if test='keyword != null and keyword != \"\"'> AND (ba.adjust_no LIKE CONCAT('%',#{keyword},'%') OR ba.reason LIKE CONCAT('%',#{keyword},'%') OR p.project_name LIKE CONCAT('%',#{keyword},'%')) </if>",
            "  <if test='projectId != null'> AND ba.project_id = #{projectId} </if>",
            "  <if test='status != null'> AND ba.status = #{status} </if>",
            "  <if test='todoOnly != null and todoOnly'>",
            "    <choose>",
            "      <when test='adminAll'> AND ba.status IN (1,2) </when>",
            "      <when test='financeCenter'> AND ba.status = 2 </when>",
            "      <when test='unitAuditor'> AND ba.status = 1 </when>",
            "      <otherwise> AND 1 = 2 </otherwise>",
            "    </choose>",
            "  </if>",
            "  <if test='adminAll == false'>",
            "    <choose>",
            "      <when test='financeCenter'> AND 1 = 1 </when>",
            "      <when test='unitAuditor'> AND ba.unit_id = #{unitId} </when>",
            "      <when test='teacherScope'> AND ba.applicant_id = #{userId} </when>",
            "      <otherwise> AND ba.unit_id = #{unitId} </otherwise>",
            "    </choose>",
            "  </if>",
            "</where>",
            "</script>"
    })
    long count(@Param("keyword") String keyword,
               @Param("projectId") Long projectId,
               @Param("status") Integer status,
               @Param("todoOnly") Boolean todoOnly,
               @Param("userId") Long userId,
               @Param("unitId") Long unitId,
               @Param("adminAll") boolean adminAll,
               @Param("teacherScope") boolean teacherScope,
               @Param("unitAuditor") boolean unitAuditor,
               @Param("financeCenter") boolean financeCenter);

    @Select("SELECT id, adjust_no AS adjustNo, project_id AS projectId, applicant_id AS applicantId, unit_id AS unitId, reason, total_delta AS totalDelta, status, current_node AS currentNode, last_comment AS lastComment, DATE_FORMAT(submitted_at, '%Y-%m-%d %H:%i:%s') AS submittedAt, DATE_FORMAT(effective_at, '%Y-%m-%d %H:%i:%s') AS effectiveAt FROM rf_budget_adjust WHERE id = #{id} LIMIT 1")
    BudgetAdjustEntity selectEntityById(@Param("id") Long id);

    @Select("SELECT ba.id, ba.adjust_no AS adjustNo, ba.project_id AS projectId, p.project_code AS projectCode, p.project_name AS projectName, ba.applicant_id AS applicantId, su.real_name AS applicantName, ba.unit_id AS unitId, ou.name AS unitName, ba.reason, ba.total_delta AS totalDelta, ba.status, DATE_FORMAT(ba.submitted_at, '%Y-%m-%d %H:%i:%s') AS submittedAt, DATE_FORMAT(ba.created_at, '%Y-%m-%d %H:%i:%s') AS createdAt, DATE_FORMAT(ba.updated_at, '%Y-%m-%d %H:%i:%s') AS updatedAt, DATE_FORMAT(ba.effective_at, '%Y-%m-%d %H:%i:%s') AS effectiveAt, ba.current_node AS currentNode, ba.last_comment AS lastComment FROM rf_budget_adjust ba JOIN rf_project p ON p.id = ba.project_id LEFT JOIN sys_user su ON su.id = ba.applicant_id LEFT JOIN org_unit ou ON ou.id = ba.unit_id WHERE ba.id = #{id} LIMIT 1")
    BudgetAdjustDetailVO selectDetailById(@Param("id") Long id);

    @Insert("INSERT INTO rf_budget_adjust(adjust_no, project_id, applicant_id, unit_id, reason, total_delta, status, current_node, last_comment, created_at, updated_at) VALUES(#{adjustNo}, #{projectId}, #{applicantId}, #{unitId}, #{reason}, #{totalDelta}, #{status}, 'DRAFT', NULL, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(BudgetAdjustEntity entity);

    @Update("UPDATE rf_budget_adjust SET project_id = #{projectId}, unit_id = #{unitId}, reason = #{reason}, total_delta = #{totalDelta}, updated_at = NOW() WHERE id = #{id}")
    int updateDraft(BudgetAdjustEntity entity);

    @Delete("DELETE FROM rf_budget_adjust WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    @Update("UPDATE rf_budget_adjust SET status = #{status}, current_node = #{currentNode}, submitted_at = NOW(), last_comment = NULL, updated_at = NOW() WHERE id = #{id}")
    int markSubmitted(@Param("id") Long id, @Param("status") Integer status, @Param("currentNode") String currentNode);

    @Update("UPDATE rf_budget_adjust SET status = #{status}, current_node = #{currentNode}, last_comment = #{comment}, updated_at = NOW() WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status, @Param("currentNode") String currentNode, @Param("comment") String comment);

    @Update("UPDATE rf_budget_adjust SET status = #{status}, current_node = #{currentNode}, last_comment = #{comment}, effective_at = NOW(), updated_at = NOW() WHERE id = #{id}")
    int markEffective(@Param("id") Long id, @Param("status") Integer status, @Param("currentNode") String currentNode, @Param("comment") String comment);
}
