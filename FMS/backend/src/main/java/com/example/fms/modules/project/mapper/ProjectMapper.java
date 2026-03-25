package com.example.fms.modules.project.mapper;

import com.example.fms.modules.project.dto.ProjectDetailVO;
import com.example.fms.modules.project.dto.ProjectEntity;
import com.example.fms.modules.project.dto.ProjectVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ProjectMapper {

    @Select({
            "<script>",
            "SELECT p.id, p.project_code AS projectCode, p.project_name AS projectName, p.remark AS projectType, ",
            "p.pi_user_id AS principalUserId, su.real_name AS principalName, p.unit_id AS unitId, ou.name AS unitName, ",
            "DATE_FORMAT(p.start_date, '%Y-%m-%d') AS startDate, DATE_FORMAT(p.end_date, '%Y-%m-%d') AS endDate, ",
            "p.budget_total AS totalBudget, p.status, ",
            "(SELECT COUNT(1) FROM rf_project_member pm WHERE pm.project_id = p.id) AS memberCount, ",
            "DATE_FORMAT((SELECT MAX(wl.created_at) FROM wf_log wl WHERE wl.biz_type = 'PROJECT' AND wl.biz_id = p.id AND wl.action = '提交立项审批'), '%Y-%m-%d %H:%i:%s') AS submittedAt ",
            "FROM rf_project p ",
            "LEFT JOIN sys_user su ON su.id = p.pi_user_id ",
            "LEFT JOIN org_unit ou ON ou.id = p.unit_id ",
            "<where>",
            "  <if test=\"keyword != null and keyword != ''\">",
            "    AND (p.project_code LIKE CONCAT('%',#{keyword},'%') OR p.project_name LIKE CONCAT('%',#{keyword},'%') OR su.real_name LIKE CONCAT('%',#{keyword},'%'))",
            "  </if>",
            "  <if test='status != null'> AND p.status = #{status} </if>",
            "  <if test='teacherScope and viewType == \"lead\"'> AND p.pi_user_id = #{userId} </if>",
            "  <if test='teacherScope and viewType == \"joined\"'> AND EXISTS (SELECT 1 FROM rf_project_member pmx WHERE pmx.project_id = p.id AND pmx.user_id = #{userId}) </if>",
            "  <if test='todoOnly != null and todoOnly'>",
            "    <choose>",
            "      <when test='adminAll'>",
            "        AND p.status IN (1,2)",
            "      </when>",
            "      <when test='financeCenter'>",
            "        AND p.status = 2",
            "      </when>",
            "      <when test='unitAuditor'>",
            "        AND p.status = 1",
            "      </when>",
            "      <otherwise>",
            "        AND 1 = 2",
            "      </otherwise>",
            "    </choose>",
            "  </if>",
            "  <if test='adminAll == false'>",
            "    <choose>",
            "      <when test='financeCenter'>",
            "        AND 1 = 1",
            "      </when>",
            "      <when test='unitAuditor'>",
            "        AND p.unit_id = #{unitId}",
            "      </when>",
            "      <when test='teacherScope'>",
            "        AND (p.pi_user_id = #{userId} OR EXISTS (SELECT 1 FROM rf_project_member pm0 WHERE pm0.project_id = p.id AND pm0.user_id = #{userId}))",
            "      </when>",
            "      <otherwise>",
            "        AND p.unit_id = #{unitId}",
            "      </otherwise>",
            "    </choose>",
            "  </if>",
            "</where>",
            "ORDER BY p.id DESC ",
            "LIMIT #{offset}, #{pageSize}",
            "</script>"
    })
    List<ProjectVO> selectPage(@Param("offset") int offset,
                               @Param("pageSize") int pageSize,
                               @Param("keyword") String keyword,
                               @Param("status") Integer status,
                               @Param("todoOnly") Boolean todoOnly,
                               @Param("viewType") String viewType,
                               @Param("userId") Long userId,
                               @Param("unitId") Long unitId,
                               @Param("adminAll") boolean adminAll,
                               @Param("teacherScope") boolean teacherScope,
                               @Param("unitAuditor") boolean unitAuditor,
                               @Param("financeCenter") boolean financeCenter);

    @Select({
            "<script>",
            "SELECT COUNT(1) FROM rf_project p ",
            "LEFT JOIN sys_user su ON su.id = p.pi_user_id ",
            "<where>",
            "  <if test=\"keyword != null and keyword != ''\">",
            "    AND (p.project_code LIKE CONCAT('%',#{keyword},'%') OR p.project_name LIKE CONCAT('%',#{keyword},'%') OR su.real_name LIKE CONCAT('%',#{keyword},'%'))",
            "  </if>",
            "  <if test='status != null'> AND p.status = #{status} </if>",
            "  <if test='teacherScope and viewType == \"lead\"'> AND p.pi_user_id = #{userId} </if>",
            "  <if test='teacherScope and viewType == \"joined\"'> AND EXISTS (SELECT 1 FROM rf_project_member pmx WHERE pmx.project_id = p.id AND pmx.user_id = #{userId}) </if>",
            "  <if test='todoOnly != null and todoOnly'>",
            "    <choose>",
            "      <when test='adminAll'>",
            "        AND p.status IN (1,2)",
            "      </when>",
            "      <when test='financeCenter'>",
            "        AND p.status = 2",
            "      </when>",
            "      <when test='unitAuditor'>",
            "        AND p.status = 1",
            "      </when>",
            "      <otherwise>",
            "        AND 1 = 2",
            "      </otherwise>",
            "    </choose>",
            "  </if>",
            "  <if test='adminAll == false'>",
            "    <choose>",
            "      <when test='financeCenter'>",
            "        AND 1 = 1",
            "      </when>",
            "      <when test='unitAuditor'>",
            "        AND p.unit_id = #{unitId}",
            "      </when>",
            "      <when test='teacherScope'>",
            "        AND (p.pi_user_id = #{userId} OR EXISTS (SELECT 1 FROM rf_project_member pm0 WHERE pm0.project_id = p.id AND pm0.user_id = #{userId}))",
            "      </when>",
            "      <otherwise>",
            "        AND p.unit_id = #{unitId}",
            "      </otherwise>",
            "    </choose>",
            "  </if>",
            "</where>",
            "</script>"
    })
    long count(@Param("keyword") String keyword,
               @Param("status") Integer status,
               @Param("todoOnly") Boolean todoOnly,
               @Param("viewType") String viewType,
               @Param("userId") Long userId,
               @Param("unitId") Long unitId,
               @Param("adminAll") boolean adminAll,
               @Param("teacherScope") boolean teacherScope,
               @Param("unitAuditor") boolean unitAuditor,
               @Param("financeCenter") boolean financeCenter);

    @Select("SELECT id, project_code AS projectCode, project_name AS projectName, remark AS description, pi_user_id AS principalUserId, unit_id AS unitId, DATE_FORMAT(start_date, '%Y-%m-%d') AS startDate, DATE_FORMAT(end_date, '%Y-%m-%d') AS endDate, budget_total AS totalBudget, status, DATE_FORMAT((SELECT MAX(wl.created_at) FROM wf_log wl WHERE wl.biz_type = 'PROJECT' AND wl.biz_id = p.id AND wl.action = '提交立项审批'), '%Y-%m-%d %H:%i:%s') AS submittedAt, (SELECT wl.actor_user_id FROM wf_log wl WHERE wl.biz_type = 'PROJECT' AND wl.biz_id = p.id AND wl.action IN ('二级单位审批通过','二级单位驳回') ORDER BY wl.id DESC LIMIT 1) AS unitAuditorUserId, DATE_FORMAT((SELECT wl.created_at FROM wf_log wl WHERE wl.biz_type = 'PROJECT' AND wl.biz_id = p.id AND wl.action IN ('二级单位审批通过','二级单位驳回') ORDER BY wl.id DESC LIMIT 1), '%Y-%m-%d %H:%i:%s') AS unitAuditAt, (SELECT wl.comment FROM wf_log wl WHERE wl.biz_type = 'PROJECT' AND wl.biz_id = p.id AND wl.action IN ('二级单位审批通过','二级单位驳回') ORDER BY wl.id DESC LIMIT 1) AS unitAuditComment, (SELECT wl.actor_user_id FROM wf_log wl WHERE wl.biz_type = 'PROJECT' AND wl.biz_id = p.id AND wl.action IN ('财务处审批通过','财务处驳回') ORDER BY wl.id DESC LIMIT 1) AS financeAuditorUserId, DATE_FORMAT((SELECT wl.created_at FROM wf_log wl WHERE wl.biz_type = 'PROJECT' AND wl.biz_id = p.id AND wl.action IN ('财务处审批通过','财务处驳回') ORDER BY wl.id DESC LIMIT 1), '%Y-%m-%d %H:%i:%s') AS financeAuditAt, (SELECT wl.comment FROM wf_log wl WHERE wl.biz_type = 'PROJECT' AND wl.biz_id = p.id AND wl.action IN ('财务处审批通过','财务处驳回') ORDER BY wl.id DESC LIMIT 1) AS financeAuditComment FROM rf_project p WHERE id = #{id} LIMIT 1")
    ProjectEntity selectEntityById(@Param("id") Long id);

    @Select("SELECT p.id, p.project_code AS projectCode, p.project_name AS projectName, p.remark AS projectType, p.pi_user_id AS principalUserId, su.real_name AS principalName, p.unit_id AS unitId, ou.name AS unitName, DATE_FORMAT(p.start_date, '%Y-%m-%d') AS startDate, DATE_FORMAT(p.end_date, '%Y-%m-%d') AS endDate, p.budget_total AS totalBudget, p.status, p.remark AS description, DATE_FORMAT(p.created_at, '%Y-%m-%d %H:%i:%s') AS createdAt, DATE_FORMAT(p.updated_at, '%Y-%m-%d %H:%i:%s') AS updatedAt, DATE_FORMAT((SELECT MAX(wl.created_at) FROM wf_log wl WHERE wl.biz_type = 'PROJECT' AND wl.biz_id = p.id AND wl.action = '提交立项审批'), '%Y-%m-%d %H:%i:%s') AS submittedAt, (SELECT su2.real_name FROM wf_log wl LEFT JOIN sys_user su2 ON su2.id = wl.actor_user_id WHERE wl.biz_type = 'PROJECT' AND wl.biz_id = p.id AND wl.action IN ('二级单位审批通过','二级单位驳回') ORDER BY wl.id DESC LIMIT 1) AS unitAuditorName, DATE_FORMAT((SELECT wl.created_at FROM wf_log wl WHERE wl.biz_type = 'PROJECT' AND wl.biz_id = p.id AND wl.action IN ('二级单位审批通过','二级单位驳回') ORDER BY wl.id DESC LIMIT 1), '%Y-%m-%d %H:%i:%s') AS unitAuditAt, (SELECT wl.comment FROM wf_log wl WHERE wl.biz_type = 'PROJECT' AND wl.biz_id = p.id AND wl.action IN ('二级单位审批通过','二级单位驳回') ORDER BY wl.id DESC LIMIT 1) AS unitAuditComment, (SELECT su3.real_name FROM wf_log wl LEFT JOIN sys_user su3 ON su3.id = wl.actor_user_id WHERE wl.biz_type = 'PROJECT' AND wl.biz_id = p.id AND wl.action IN ('财务处审批通过','财务处驳回') ORDER BY wl.id DESC LIMIT 1) AS financeAuditorName, DATE_FORMAT((SELECT wl.created_at FROM wf_log wl WHERE wl.biz_type = 'PROJECT' AND wl.biz_id = p.id AND wl.action IN ('财务处审批通过','财务处驳回') ORDER BY wl.id DESC LIMIT 1), '%Y-%m-%d %H:%i:%s') AS financeAuditAt, (SELECT wl.comment FROM wf_log wl WHERE wl.biz_type = 'PROJECT' AND wl.biz_id = p.id AND wl.action IN ('财务处审批通过','财务处驳回') ORDER BY wl.id DESC LIMIT 1) AS financeAuditComment FROM rf_project p LEFT JOIN sys_user su ON su.id = p.pi_user_id LEFT JOIN org_unit ou ON ou.id = p.unit_id WHERE p.id = #{id} LIMIT 1")
    ProjectDetailVO selectDetailById(@Param("id") Long id);

    @Select("SELECT COUNT(1) FROM rf_project WHERE project_code = #{projectCode} AND id != IFNULL(#{excludeId}, -1)")
    int countByProjectCode(@Param("projectCode") String projectCode, @Param("excludeId") Long excludeId);

    @Insert("INSERT INTO rf_project(project_code, project_name, pi_user_id, unit_id, start_date, end_date, budget_total, status, remark, created_at, updated_at) VALUES(#{projectCode}, #{projectName}, #{principalUserId}, #{unitId}, #{startDate}, #{endDate}, #{totalBudget}, #{status}, #{description}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ProjectEntity entity);

    @Update("UPDATE rf_project SET project_code = #{projectCode}, project_name = #{projectName}, start_date = #{startDate}, end_date = #{endDate}, budget_total = #{totalBudget}, remark = #{description}, updated_at = NOW() WHERE id = #{id}")
    int updateDraft(ProjectEntity entity);

    @Update("UPDATE rf_project SET status = #{status}, updated_at = NOW() WHERE id = #{id}")
    int markSubmitted(@Param("id") Long id, @Param("status") Integer status);

    @Update("UPDATE rf_project SET status = #{status}, updated_at = NOW() WHERE id = #{id}")
    int markWithdrawnToDraft(@Param("id") Long id, @Param("status") Integer status);

    @Update("UPDATE rf_project SET status = #{status}, updated_at = NOW() WHERE id = #{id}")
    int auditUnit(@Param("id") Long id, @Param("status") Integer status, @Param("auditorUserId") Long auditorUserId, @Param("comment") String comment);

    @Update("UPDATE rf_project SET status = #{status}, updated_at = NOW() WHERE id = #{id}")
    int auditFinance(@Param("id") Long id, @Param("status") Integer status, @Param("auditorUserId") Long auditorUserId, @Param("comment") String comment);


    @Select("SELECT COUNT(1) FROM rf_reimb WHERE project_id = #{projectId}")
    int countReimburseByProjectId(@Param("projectId") Long projectId);

    @Select("SELECT COUNT(1) FROM rf_budget_adjust WHERE project_id = #{projectId}")
    int countBudgetAdjustByProjectId(@Param("projectId") Long projectId);

    @Delete("DELETE FROM rf_project_member WHERE project_id = #{projectId}")
    int deleteMembersByProjectId(@Param("projectId") Long projectId);

    @Delete("DELETE FROM rf_project_budget WHERE project_id = #{projectId}")
    int deleteBudgetsByProjectId(@Param("projectId") Long projectId);

    @Delete("DELETE FROM wf_task WHERE biz_type = 'PROJECT' AND biz_id = #{projectId}")
    int deleteWfTasksByProjectId(@Param("projectId") Long projectId);

    @Delete("DELETE FROM wf_log WHERE biz_type = 'PROJECT' AND biz_id = #{projectId}")
    int deleteWfLogsByProjectId(@Param("projectId") Long projectId);

    @Delete("DELETE FROM rf_project WHERE id = #{projectId}")
    int deleteById(@Param("projectId") Long projectId);

    @Select("SELECT id, project_code AS projectCode, project_name AS projectName, pi_user_id AS principalUserId, unit_id AS unitId, budget_total AS totalBudget, status FROM rf_project ORDER BY id ASC")
    List<ProjectEntity> selectAllForBudgetInit();

}