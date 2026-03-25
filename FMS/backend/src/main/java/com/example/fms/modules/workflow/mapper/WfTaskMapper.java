package com.example.fms.modules.workflow.mapper;

import com.example.fms.modules.workflow.dto.WfTaskVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface WfTaskMapper {

    @Insert("INSERT INTO wf_task(biz_type, biz_id, node_code, task_title, assignee_role_code, assignee_user_id, applicant_user_id, unit_id, task_status, created_at, updated_at) VALUES(#{bizType}, #{bizId}, #{nodeCode}, #{taskTitle}, #{assigneeRoleCode}, #{assigneeUserId}, #{applicantUserId}, #{unitId}, 'TODO', NOW(), NOW())")
    int insert(@Param("bizType") String bizType,
               @Param("bizId") Long bizId,
               @Param("nodeCode") String nodeCode,
               @Param("taskTitle") String taskTitle,
               @Param("assigneeRoleCode") String assigneeRoleCode,
               @Param("assigneeUserId") Long assigneeUserId,
               @Param("applicantUserId") Long applicantUserId,
               @Param("unitId") Long unitId);

    @Update("UPDATE wf_task SET task_status = 'DONE', completed_action = #{action}, completed_by = #{completedBy}, completed_at = NOW(), updated_at = NOW() WHERE biz_type = #{bizType} AND biz_id = #{bizId} AND node_code = #{nodeCode} AND task_status = 'TODO'")
    int markDone(@Param("bizType") String bizType,
                 @Param("bizId") Long bizId,
                 @Param("nodeCode") String nodeCode,
                 @Param("action") String action,
                 @Param("completedBy") Long completedBy);

    @Update("UPDATE wf_task SET task_status = 'CANCELED', completed_action = #{action}, completed_by = #{completedBy}, completed_at = NOW(), updated_at = NOW() WHERE biz_type = #{bizType} AND biz_id = #{bizId} AND task_status = 'TODO'")
    int cancelTodoByBiz(@Param("bizType") String bizType,
                        @Param("bizId") Long bizId,
                        @Param("action") String action,
                        @Param("completedBy") Long completedBy);

    @Select("SELECT COUNT(1) FROM wf_task WHERE biz_type = #{bizType} AND biz_id = #{bizId} AND node_code = #{nodeCode} AND task_status = 'TODO'")
    int countTodo(@Param("bizType") String bizType, @Param("bizId") Long bizId, @Param("nodeCode") String nodeCode);

    @Select("SELECT COUNT(1) FROM wf_task WHERE biz_type = #{bizType} AND biz_id = #{bizId} AND task_status = 'TODO'")
    int countAnyTodo(@Param("bizType") String bizType, @Param("bizId") Long bizId);

    @Select({
            "<script>",
            "SELECT t.id, t.biz_type AS bizType, t.biz_id AS bizId, t.node_code AS nodeCode, t.assignee_role_code AS assigneeRoleCode, t.task_status AS taskStatus, DATE_FORMAT(t.created_at, '%Y-%m-%d %H:%i:%s') AS createdAt, DATE_FORMAT(t.completed_at, '%Y-%m-%d %H:%i:%s') AS completedAt, ",
            "CASE WHEN t.biz_type = 'REIMB' THEN r.reimb_no WHEN t.biz_type = 'BUDGET_ADJUST' THEN ba.adjust_no ELSE NULL END AS bizNo, ",
            "CASE WHEN t.biz_type = 'REIMB' THEN r.title WHEN t.biz_type = 'BUDGET_ADJUST' THEN ba.reason ELSE t.task_title END AS title, ",
            "CASE WHEN t.biz_type = 'REIMB' THEN p1.project_name WHEN t.biz_type = 'BUDGET_ADJUST' THEN p2.project_name ELSE NULL END AS projectName, ",
            "CASE WHEN t.biz_type = 'REIMB' THEN r.project_id WHEN t.biz_type = 'BUDGET_ADJUST' THEN ba.project_id ELSE NULL END AS projectId, ",
            "CASE WHEN t.biz_type = 'REIMB' THEN r.applicant_id WHEN t.biz_type = 'BUDGET_ADJUST' THEN ba.applicant_id ELSE t.applicant_user_id END AS applicantUserId, ",
            "su.real_name AS applicantName, t.unit_id AS unitId, ou.name AS unitName ",
            "FROM wf_task t ",
            "LEFT JOIN rf_reimb r ON t.biz_type = 'REIMB' AND r.id = t.biz_id ",
            "LEFT JOIN rf_project p1 ON p1.id = r.project_id ",
            "LEFT JOIN rf_budget_adjust ba ON t.biz_type = 'BUDGET_ADJUST' AND ba.id = t.biz_id ",
            "LEFT JOIN rf_project p2 ON p2.id = ba.project_id ",
            "LEFT JOIN sys_user su ON su.id = COALESCE(r.applicant_id, ba.applicant_id, t.applicant_user_id) ",
            "LEFT JOIN org_unit ou ON ou.id = t.unit_id ",
            "<where>",
            "  t.task_status = 'TODO' ",
            "  <if test='bizType != null and bizType != \"\"'> AND t.biz_type = #{bizType} </if>",
            "  <if test='nodeCode != null and nodeCode != \"\"'> AND t.node_code = #{nodeCode} </if>",
            "  <if test='assigneeRoleCode != null and assigneeRoleCode != \"\"'> AND t.assignee_role_code = #{assigneeRoleCode} </if>",
            "  <if test='unitScoped'> AND t.unit_id = #{unitId} </if>",
            "  <if test='keyword != null and keyword != \"\"'> AND (CASE WHEN t.biz_type = 'REIMB' THEN r.reimb_no WHEN t.biz_type = 'BUDGET_ADJUST' THEN ba.adjust_no ELSE '' END LIKE CONCAT('%',#{keyword},'%') OR CASE WHEN t.biz_type = 'REIMB' THEN r.title WHEN t.biz_type = 'BUDGET_ADJUST' THEN ba.reason ELSE t.task_title END LIKE CONCAT('%',#{keyword},'%') OR CASE WHEN t.biz_type = 'REIMB' THEN p1.project_name WHEN t.biz_type = 'BUDGET_ADJUST' THEN p2.project_name ELSE '' END LIKE CONCAT('%',#{keyword},'%')) </if>",
            "</where>",
            "ORDER BY t.id DESC LIMIT #{offset}, #{pageSize}",
            "</script>"
    })
    List<WfTaskVO> selectTodoPage(@Param("offset") int offset,
                                  @Param("pageSize") int pageSize,
                                  @Param("bizType") String bizType,
                                  @Param("nodeCode") String nodeCode,
                                  @Param("assigneeRoleCode") String assigneeRoleCode,
                                  @Param("unitScoped") boolean unitScoped,
                                  @Param("unitId") Long unitId,
                                  @Param("keyword") String keyword);

    @Select({
            "<script>",
            "SELECT COUNT(1) ",
            "FROM wf_task t ",
            "LEFT JOIN rf_reimb r ON t.biz_type = 'REIMB' AND r.id = t.biz_id ",
            "LEFT JOIN rf_project p1 ON p1.id = r.project_id ",
            "LEFT JOIN rf_budget_adjust ba ON t.biz_type = 'BUDGET_ADJUST' AND ba.id = t.biz_id ",
            "LEFT JOIN rf_project p2 ON p2.id = ba.project_id ",
            "<where>",
            "  t.task_status = 'TODO' ",
            "  <if test='bizType != null and bizType != \"\"'> AND t.biz_type = #{bizType} </if>",
            "  <if test='nodeCode != null and nodeCode != \"\"'> AND t.node_code = #{nodeCode} </if>",
            "  <if test='assigneeRoleCode != null and assigneeRoleCode != \"\"'> AND t.assignee_role_code = #{assigneeRoleCode} </if>",
            "  <if test='unitScoped'> AND t.unit_id = #{unitId} </if>",
            "  <if test='keyword != null and keyword != \"\"'> AND (CASE WHEN t.biz_type = 'REIMB' THEN r.reimb_no WHEN t.biz_type = 'BUDGET_ADJUST' THEN ba.adjust_no ELSE '' END LIKE CONCAT('%',#{keyword},'%') OR CASE WHEN t.biz_type = 'REIMB' THEN r.title WHEN t.biz_type = 'BUDGET_ADJUST' THEN ba.reason ELSE t.task_title END LIKE CONCAT('%',#{keyword},'%') OR CASE WHEN t.biz_type = 'REIMB' THEN p1.project_name WHEN t.biz_type = 'BUDGET_ADJUST' THEN p2.project_name ELSE '' END LIKE CONCAT('%',#{keyword},'%')) </if>",
            "</where>",
            "</script>"
    })
    long countTodoPage(@Param("bizType") String bizType,
                       @Param("nodeCode") String nodeCode,
                       @Param("assigneeRoleCode") String assigneeRoleCode,
                       @Param("unitScoped") boolean unitScoped,
                       @Param("unitId") Long unitId,
                       @Param("keyword") String keyword);

    @Select({
            "<script>",
            "SELECT t.id, t.biz_type AS bizType, t.biz_id AS bizId, t.node_code AS nodeCode, t.assignee_role_code AS assigneeRoleCode, t.task_status AS taskStatus, DATE_FORMAT(t.created_at, '%Y-%m-%d %H:%i:%s') AS createdAt, DATE_FORMAT(t.completed_at, '%Y-%m-%d %H:%i:%s') AS completedAt, ",
            "CASE WHEN t.biz_type = 'REIMB' THEN r.reimb_no WHEN t.biz_type = 'BUDGET_ADJUST' THEN ba.adjust_no ELSE NULL END AS bizNo, ",
            "CASE WHEN t.biz_type = 'REIMB' THEN r.title WHEN t.biz_type = 'BUDGET_ADJUST' THEN ba.reason ELSE t.task_title END AS title, ",
            "CASE WHEN t.biz_type = 'REIMB' THEN p1.project_name WHEN t.biz_type = 'BUDGET_ADJUST' THEN p2.project_name ELSE NULL END AS projectName, ",
            "CASE WHEN t.biz_type = 'REIMB' THEN r.project_id WHEN t.biz_type = 'BUDGET_ADJUST' THEN ba.project_id ELSE NULL END AS projectId, ",
            "CASE WHEN t.biz_type = 'REIMB' THEN r.applicant_id WHEN t.biz_type = 'BUDGET_ADJUST' THEN ba.applicant_id ELSE t.applicant_user_id END AS applicantUserId, ",
            "su.real_name AS applicantName, t.unit_id AS unitId, ou.name AS unitName ",
            "FROM wf_task t ",
            "LEFT JOIN rf_reimb r ON t.biz_type = 'REIMB' AND r.id = t.biz_id ",
            "LEFT JOIN rf_project p1 ON p1.id = r.project_id ",
            "LEFT JOIN rf_budget_adjust ba ON t.biz_type = 'BUDGET_ADJUST' AND ba.id = t.biz_id ",
            "LEFT JOIN rf_project p2 ON p2.id = ba.project_id ",
            "LEFT JOIN sys_user su ON su.id = COALESCE(r.applicant_id, ba.applicant_id, t.applicant_user_id) ",
            "LEFT JOIN org_unit ou ON ou.id = t.unit_id ",
            "<where>",
            "  t.task_status = 'DONE' ",
            "  AND t.completed_by = #{completedBy} ",
            "  <if test='bizType != null and bizType != \"\"'> AND t.biz_type = #{bizType} </if>",
            "  <if test='nodeCode != null and nodeCode != \"\"'> AND t.node_code = #{nodeCode} </if>",
            "  <if test='keyword != null and keyword != \"\"'> AND (CASE WHEN t.biz_type = 'REIMB' THEN r.reimb_no WHEN t.biz_type = 'BUDGET_ADJUST' THEN ba.adjust_no ELSE '' END LIKE CONCAT('%',#{keyword},'%') OR CASE WHEN t.biz_type = 'REIMB' THEN r.title WHEN t.biz_type = 'BUDGET_ADJUST' THEN ba.reason ELSE t.task_title END LIKE CONCAT('%',#{keyword},'%') OR CASE WHEN t.biz_type = 'REIMB' THEN p1.project_name WHEN t.biz_type = 'BUDGET_ADJUST' THEN p2.project_name ELSE '' END LIKE CONCAT('%',#{keyword},'%')) </if>",
            "</where>",
            "ORDER BY t.completed_at DESC, t.id DESC LIMIT #{offset}, #{pageSize}",
            "</script>"
    })
    List<WfTaskVO> selectDonePage(@Param("offset") int offset,
                                  @Param("pageSize") int pageSize,
                                  @Param("bizType") String bizType,
                                  @Param("nodeCode") String nodeCode,
                                  @Param("completedBy") Long completedBy,
                                  @Param("keyword") String keyword);

    @Select({
            "<script>",
            "SELECT COUNT(1) ",
            "FROM wf_task t ",
            "LEFT JOIN rf_reimb r ON t.biz_type = 'REIMB' AND r.id = t.biz_id ",
            "LEFT JOIN rf_project p1 ON p1.id = r.project_id ",
            "LEFT JOIN rf_budget_adjust ba ON t.biz_type = 'BUDGET_ADJUST' AND ba.id = t.biz_id ",
            "LEFT JOIN rf_project p2 ON p2.id = ba.project_id ",
            "<where>",
            "  t.task_status = 'DONE' AND t.completed_by = #{completedBy} ",
            "  <if test='bizType != null and bizType != \"\"'> AND t.biz_type = #{bizType} </if>",
            "  <if test='nodeCode != null and nodeCode != \"\"'> AND t.node_code = #{nodeCode} </if>",
            "  <if test='keyword != null and keyword != \"\"'> AND (CASE WHEN t.biz_type = 'REIMB' THEN r.reimb_no WHEN t.biz_type = 'BUDGET_ADJUST' THEN ba.adjust_no ELSE '' END LIKE CONCAT('%',#{keyword},'%') OR CASE WHEN t.biz_type = 'REIMB' THEN r.title WHEN t.biz_type = 'BUDGET_ADJUST' THEN ba.reason ELSE t.task_title END LIKE CONCAT('%',#{keyword},'%') OR CASE WHEN t.biz_type = 'REIMB' THEN p1.project_name WHEN t.biz_type = 'BUDGET_ADJUST' THEN p2.project_name ELSE '' END LIKE CONCAT('%',#{keyword},'%')) </if>",
            "</where>",
            "</script>"
    })
    long countDonePage(@Param("bizType") String bizType,
                       @Param("nodeCode") String nodeCode,
                       @Param("completedBy") Long completedBy,
                       @Param("keyword") String keyword);
}
