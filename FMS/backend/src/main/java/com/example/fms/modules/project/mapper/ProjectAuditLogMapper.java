package com.example.fms.modules.project.mapper;

import com.example.fms.modules.project.dto.ProjectAuditLogVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

@Mapper
public interface ProjectAuditLogMapper {

    @SelectProvider(type = SqlProvider.class, method = "selectByProjectId")
    List<ProjectAuditLogVO> selectByProjectId(@Param("projectId") Long projectId);

    @Insert("INSERT INTO wf_log(biz_type, biz_id, node_code, action, actor_user_id, comment, created_at) VALUES('PROJECT', #{projectId}, CASE WHEN #{action} = '创建项目' THEN 'CREATE' WHEN #{action} = '编辑项目' THEN 'CONTENT' WHEN #{action} = '提交立项审批' THEN 'SUBMIT' WHEN #{action} IN ('二级单位审批通过', '二级单位驳回') THEN 'UNIT_REVIEW' WHEN #{action} IN ('财务处审批通过', '财务处驳回') THEN 'FINANCE_REVIEW' WHEN #{action} = '撤回审批' THEN 'WITHDRAWN' WHEN #{action} = '删除项目' THEN 'DELETED' WHEN #{action} IN ('添加项目成员', '移除项目成员') THEN 'MEMBER' WHEN #{toStatus} = 3 THEN 'APPROVED' WHEN #{toStatus} = 4 THEN 'REJECTED' WHEN #{toStatus} = 5 THEN 'CLOSED' WHEN #{toStatus} = 6 THEN 'DISABLED' ELSE 'PROJECT_FLOW' END, #{action}, #{operatorUserId}, #{comment}, NOW())")
    int insert(@Param("projectId") Long projectId,
               @Param("action") String action,
               @Param("fromStatus") Integer fromStatus,
               @Param("toStatus") Integer toStatus,
               @Param("operatorUserId") Long operatorUserId,
               @Param("comment") String comment);

    class SqlProvider {
        public String selectByProjectId() {
            return "SELECT l.id, l.action, " +
                    "CASE l.node_code WHEN 'UNIT_REVIEW' THEN 1 WHEN 'FINANCE_REVIEW' THEN 2 WHEN 'APPROVED' THEN 3 WHEN 'REJECTED' THEN 4 WHEN 'CLOSED' THEN 5 WHEN 'DISABLED' THEN 6 WHEN 'CREATE' THEN 0 ELSE NULL END AS fromStatus, " +
                    "CASE l.node_code WHEN 'UNIT_REVIEW' THEN 1 WHEN 'FINANCE_REVIEW' THEN 2 WHEN 'APPROVED' THEN 3 WHEN 'REJECTED' THEN 4 WHEN 'CLOSED' THEN 5 WHEN 'DISABLED' THEN 6 WHEN 'CREATE' THEN 0 ELSE NULL END AS toStatus, " +
                    "l.actor_user_id AS operatorUserId, u.real_name AS operatorName, l.comment, " +
                    "DATE_FORMAT(l.created_at, '%Y-%m-%d %H:%i:%s') AS createdAt " +
                    "FROM wf_log l LEFT JOIN sys_user u ON u.id = l.actor_user_id " +
                    "WHERE l.biz_type = 'PROJECT' AND l.biz_id = #{projectId} AND l.node_code NOT IN ('CONTENT') " +
                    "ORDER BY l.id ASC";
        }
    }
}
