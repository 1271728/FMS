package com.example.fms.modules.reimburse.mapper;

import com.example.fms.modules.reimburse.dto.ReimburseDetailVO;
import com.example.fms.modules.reimburse.dto.ReimburseEntity;
import com.example.fms.modules.reimburse.dto.ReimburseVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ReimburseMapper {

    @Select({
            "<script>",
            "SELECT r.id, r.reimb_no AS reimburseNo, r.project_id AS projectId, p.project_code AS projectCode, p.project_name AS projectName, ",
            "r.applicant_id AS applicantUserId, su.real_name AS applicantName, r.unit_id AS unitId, ou.name AS unitName, ",
            "r.title, r.total_amount AS totalAmount, r.status, ",
            "(SELECT COUNT(1) FROM rf_reimb_line ri WHERE ri.reimb_id = r.id) AS itemCount, ",
            "DATE_FORMAT(r.submitted_at, '%Y-%m-%d %H:%i:%s') AS submittedAt ",
            "FROM rf_reimb r ",
            "JOIN rf_project p ON p.id = r.project_id ",
            "LEFT JOIN sys_user su ON su.id = r.applicant_id ",
            "LEFT JOIN org_unit ou ON ou.id = r.unit_id ",
            "<where>",
            "  <if test='keyword != null and keyword != \"\"'> AND (r.reimb_no LIKE CONCAT('%',#{keyword},'%') OR r.title LIKE CONCAT('%',#{keyword},'%') OR p.project_name LIKE CONCAT('%',#{keyword},'%')) </if>",
            "  <if test='status != null'> AND r.status = #{status} </if>",
            "  <if test='projectId != null'> AND r.project_id = #{projectId} </if>",
            "  <if test='todoOnly != null and todoOnly'>",
            "    <choose>",
            "      <when test='adminAll'> AND r.status IN (1,2,3,7) </when>",
            "      <when test='financeCenter'> AND r.status IN (2,3) </when>",
            "      <when test='unitAuditor'> AND r.status = 1 </when>",
            "      <when test='teacherScope'> AND r.status = 7 AND p.pi_user_id = #{userId} AND r.applicant_id &lt;&gt; #{userId} </when>",
            "      <otherwise> AND 1 = 2 </otherwise>",
            "    </choose>",
            "  </if>",
            "  <if test='adminAll == false'>",
            "    <choose>",
            "      <when test='financeCenter'> AND 1 = 1 </when>",
            "      <when test='unitAuditor'> AND r.unit_id = #{unitId} </when>",
            "      <when test='teacherScope'> AND (r.applicant_id = #{userId} OR p.pi_user_id = #{userId} OR EXISTS (SELECT 1 FROM rf_project_member pm WHERE pm.project_id = r.project_id AND pm.user_id = #{userId})) </when>",
            "      <otherwise> AND r.unit_id = #{unitId} </otherwise>",
            "    </choose>",
            "  </if>",
            "</where>",
            "ORDER BY r.id DESC LIMIT #{offset}, #{pageSize}",
            "</script>"
    })
    List<ReimburseVO> selectPage(@Param("offset") int offset,
                                 @Param("pageSize") int pageSize,
                                 @Param("keyword") String keyword,
                                 @Param("status") Integer status,
                                 @Param("projectId") Long projectId,
                                 @Param("todoOnly") Boolean todoOnly,
                                 @Param("userId") Long userId,
                                 @Param("unitId") Long unitId,
                                 @Param("adminAll") boolean adminAll,
                                 @Param("teacherScope") boolean teacherScope,
                                 @Param("unitAuditor") boolean unitAuditor,
                                 @Param("financeCenter") boolean financeCenter);

    @Select({
            "<script>",
            "SELECT COUNT(1) FROM rf_reimb r JOIN rf_project p ON p.id = r.project_id ",
            "<where>",
            "  <if test='keyword != null and keyword != \"\"'> AND (r.reimb_no LIKE CONCAT('%',#{keyword},'%') OR r.title LIKE CONCAT('%',#{keyword},'%') OR p.project_name LIKE CONCAT('%',#{keyword},'%')) </if>",
            "  <if test='status != null'> AND r.status = #{status} </if>",
            "  <if test='projectId != null'> AND r.project_id = #{projectId} </if>",
            "  <if test='todoOnly != null and todoOnly'>",
            "    <choose>",
            "      <when test='adminAll'> AND r.status IN (1,2,3,7) </when>",
            "      <when test='financeCenter'> AND r.status IN (2,3) </when>",
            "      <when test='unitAuditor'> AND r.status = 1 </when>",
            "      <when test='teacherScope'> AND r.status = 7 AND p.pi_user_id = #{userId} AND r.applicant_id &lt;&gt; #{userId} </when>",
            "      <otherwise> AND 1 = 2 </otherwise>",
            "    </choose>",
            "  </if>",
            "  <if test='adminAll == false'>",
            "    <choose>",
            "      <when test='financeCenter'> AND 1 = 1 </when>",
            "      <when test='unitAuditor'> AND r.unit_id = #{unitId} </when>",
            "      <when test='teacherScope'> AND (r.applicant_id = #{userId} OR p.pi_user_id = #{userId} OR EXISTS (SELECT 1 FROM rf_project_member pm WHERE pm.project_id = r.project_id AND pm.user_id = #{userId})) </when>",
            "      <otherwise> AND r.unit_id = #{unitId} </otherwise>",
            "    </choose>",
            "  </if>",
            "</where>",
            "</script>"
    })
    long count(@Param("keyword") String keyword,
               @Param("status") Integer status,
               @Param("projectId") Long projectId,
               @Param("todoOnly") Boolean todoOnly,
               @Param("userId") Long userId,
               @Param("unitId") Long unitId,
               @Param("adminAll") boolean adminAll,
               @Param("teacherScope") boolean teacherScope,
               @Param("unitAuditor") boolean unitAuditor,
               @Param("financeCenter") boolean financeCenter);

    @Select("SELECT id, reimb_no AS reimburseNo, project_id AS projectId, applicant_id AS applicantUserId, unit_id AS unitId, title, total_amount AS totalAmount, status, current_node AS currentNode, (SELECT wl.comment FROM wf_log wl WHERE wl.biz_type = 'REIMB' AND wl.biz_id = r.id AND wl.node_code IN ('CONTENT','CREATE') ORDER BY CASE WHEN wl.node_code = 'CONTENT' THEN 0 ELSE 1 END, wl.id DESC LIMIT 1) AS description, DATE_FORMAT(submitted_at, '%Y-%m-%d %H:%i:%s') AS submittedAt, (SELECT wl.actor_user_id FROM wf_log wl WHERE wl.biz_type = 'REIMB' AND wl.biz_id = r.id AND wl.node_code = 'UNIT_AUDIT' ORDER BY wl.id DESC LIMIT 1) AS unitAuditorUserId, DATE_FORMAT((SELECT wl.created_at FROM wf_log wl WHERE wl.biz_type = 'REIMB' AND wl.biz_id = r.id AND wl.node_code = 'UNIT_AUDIT' ORDER BY wl.id DESC LIMIT 1), '%Y-%m-%d %H:%i:%s') AS unitAuditAt, (SELECT wl.comment FROM wf_log wl WHERE wl.biz_type = 'REIMB' AND wl.biz_id = r.id AND wl.node_code = 'UNIT_AUDIT' ORDER BY wl.id DESC LIMIT 1) AS unitAuditComment, (SELECT wl.actor_user_id FROM wf_log wl WHERE wl.biz_type = 'REIMB' AND wl.biz_id = r.id AND wl.node_code = 'FIN_REVIEW' ORDER BY wl.id DESC LIMIT 1) AS financeAuditorUserId, DATE_FORMAT((SELECT wl.created_at FROM wf_log wl WHERE wl.biz_type = 'REIMB' AND wl.biz_id = r.id AND wl.node_code = 'FIN_REVIEW' ORDER BY wl.id DESC LIMIT 1), '%Y-%m-%d %H:%i:%s') AS financeAuditAt, (SELECT wl.comment FROM wf_log wl WHERE wl.biz_type = 'REIMB' AND wl.biz_id = r.id AND wl.node_code = 'FIN_REVIEW' ORDER BY wl.id DESC LIMIT 1) AS financeAuditComment FROM rf_reimb r WHERE id = #{id} LIMIT 1")
    ReimburseEntity selectEntityById(@Param("id") Long id);

    @Select("SELECT r.id, r.reimb_no AS reimburseNo, r.project_id AS projectId, p.project_code AS projectCode, p.project_name AS projectName, r.applicant_id AS applicantUserId, su.real_name AS applicantName, r.unit_id AS unitId, ou.name AS unitName, r.title, r.total_amount AS totalAmount, r.status, (SELECT wl.comment FROM wf_log wl WHERE wl.biz_type = 'REIMB' AND wl.biz_id = r.id AND wl.node_code IN ('CONTENT','CREATE') ORDER BY CASE WHEN wl.node_code = 'CONTENT' THEN 0 ELSE 1 END, wl.id DESC LIMIT 1) AS description, DATE_FORMAT(r.created_at, '%Y-%m-%d %H:%i:%s') AS createdAt, DATE_FORMAT(r.updated_at, '%Y-%m-%d %H:%i:%s') AS updatedAt, DATE_FORMAT(r.submitted_at, '%Y-%m-%d %H:%i:%s') AS submittedAt, (SELECT ua.real_name FROM wf_log wl LEFT JOIN sys_user ua ON ua.id = wl.actor_user_id WHERE wl.biz_type = 'REIMB' AND wl.biz_id = r.id AND wl.node_code = 'UNIT_AUDIT' ORDER BY wl.id DESC LIMIT 1) AS unitAuditorName, DATE_FORMAT((SELECT wl.created_at FROM wf_log wl WHERE wl.biz_type = 'REIMB' AND wl.biz_id = r.id AND wl.node_code = 'UNIT_AUDIT' ORDER BY wl.id DESC LIMIT 1), '%Y-%m-%d %H:%i:%s') AS unitAuditAt, (SELECT wl.comment FROM wf_log wl WHERE wl.biz_type = 'REIMB' AND wl.biz_id = r.id AND wl.node_code = 'UNIT_AUDIT' ORDER BY wl.id DESC LIMIT 1) AS unitAuditComment, (SELECT fa.real_name FROM wf_log wl LEFT JOIN sys_user fa ON fa.id = wl.actor_user_id WHERE wl.biz_type = 'REIMB' AND wl.biz_id = r.id AND wl.node_code = 'FIN_REVIEW' ORDER BY wl.id DESC LIMIT 1) AS financeAuditorName, DATE_FORMAT((SELECT wl.created_at FROM wf_log wl WHERE wl.biz_type = 'REIMB' AND wl.biz_id = r.id AND wl.node_code = 'FIN_REVIEW' ORDER BY wl.id DESC LIMIT 1), '%Y-%m-%d %H:%i:%s') AS financeAuditAt, (SELECT wl.comment FROM wf_log wl WHERE wl.biz_type = 'REIMB' AND wl.biz_id = r.id AND wl.node_code = 'FIN_REVIEW' ORDER BY wl.id DESC LIMIT 1) AS financeAuditComment FROM rf_reimb r JOIN rf_project p ON p.id = r.project_id LEFT JOIN sys_user su ON su.id = r.applicant_id LEFT JOIN org_unit ou ON ou.id = r.unit_id WHERE r.id = #{id} LIMIT 1")
    ReimburseDetailVO selectDetailById(@Param("id") Long id);

    @Insert("INSERT INTO rf_reimb(reimb_no, project_id, applicant_id, unit_id, title, total_amount, status, current_node, last_comment, created_at, updated_at) VALUES(#{reimburseNo}, #{projectId}, #{applicantUserId}, #{unitId}, #{title}, #{totalAmount}, #{status}, 'DRAFT', NULL, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ReimburseEntity entity);

    @Update("UPDATE rf_reimb SET project_id = #{projectId}, unit_id = #{unitId}, title = #{title}, total_amount = #{totalAmount}, updated_at = NOW() WHERE id = #{id}")
    int updateDraft(ReimburseEntity entity);

    @Update("UPDATE rf_reimb SET status = #{status}, submitted_at = NOW(), current_node = #{currentNode}, last_comment = NULL, updated_at = NOW() WHERE id = #{id}")
    int markSubmitted(@Param("id") Long id, @Param("status") Integer status, @Param("currentNode") String currentNode);

    @Update("UPDATE rf_reimb SET status = #{status}, current_node = #{currentNode}, last_comment = #{comment}, updated_at = NOW() WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status, @Param("currentNode") String currentNode, @Param("comment") String comment);

    @Select("SELECT COUNT(1) FROM rf_reimb WHERE reimb_no LIKE CONCAT(#{prefix}, '%')")
    int countLikePrefix(@Param("prefix") String prefix);
}
