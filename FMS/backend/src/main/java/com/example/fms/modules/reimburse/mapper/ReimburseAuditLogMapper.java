package com.example.fms.modules.reimburse.mapper;

import com.example.fms.modules.reimburse.dto.ReimburseAuditLogVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

@Mapper
public interface ReimburseAuditLogMapper {
    @Insert("INSERT INTO wf_log(biz_type, biz_id, node_code, action, actor_user_id, comment, created_at) VALUES('REIMB', #{reimburseId}, CASE WHEN #{action} = '保存内容' THEN 'CONTENT' WHEN #{action} = '创建报销单' THEN 'CREATE' WHEN #{action} = '提交报销审批' THEN 'PI_AUDIT' WHEN #{toStatus} = 7 THEN 'PI_AUDIT' WHEN #{toStatus} = 1 THEN 'UNIT_AUDIT' WHEN #{toStatus} = 2 THEN 'FIN_REVIEW' WHEN #{toStatus} = 3 THEN 'PAY_ARCHIVE' WHEN #{toStatus} = 5 THEN 'REJECTED' WHEN #{toStatus} = 6 THEN 'WITHDRAWN' ELSE 'REIMB_FLOW' END, #{action}, #{operatorUserId}, #{comment}, NOW())")
    int insert(@Param("reimburseId") Long reimburseId,
               @Param("action") String action,
               @Param("fromStatus") Integer fromStatus,
               @Param("toStatus") Integer toStatus,
               @Param("operatorUserId") Long operatorUserId,
               @Param("comment") String comment);

    @SelectProvider(type = SqlProvider.class, method = "selectByReimburseId")
    List<ReimburseAuditLogVO> selectByReimburseId(@Param("reimburseId") Long reimburseId);

    class SqlProvider {
        public String selectByReimburseId() {
            return "SELECT l.id, l.action, " +
                    "CASE l.node_code WHEN 'PI_AUDIT' THEN 7 WHEN 'UNIT_AUDIT' THEN 1 WHEN 'FIN_REVIEW' THEN 2 WHEN 'PAY_ARCHIVE' THEN 3 WHEN 'DONE' THEN 4 WHEN 'REJECTED' THEN 5 WHEN 'WITHDRAWN' THEN 6 ELSE NULL END AS fromStatus, " +
                    "CASE l.node_code WHEN 'PI_AUDIT' THEN 7 WHEN 'UNIT_AUDIT' THEN 1 WHEN 'FIN_REVIEW' THEN 2 WHEN 'PAY_ARCHIVE' THEN 3 WHEN 'DONE' THEN 4 WHEN 'REJECTED' THEN 5 WHEN 'WITHDRAWN' THEN 6 ELSE NULL END AS toStatus, " +
                    "l.actor_user_id AS operatorUserId, su.real_name AS operatorName, l.comment, " +
                    "DATE_FORMAT(l.created_at, '%Y-%m-%d %H:%i:%s') AS createdAt " +
                    "FROM wf_log l LEFT JOIN sys_user su ON su.id = l.actor_user_id " +
                    "WHERE l.biz_type = 'REIMB' AND l.biz_id = #{reimburseId} AND l.node_code NOT IN ('CONTENT') " +
                    "ORDER BY l.id ASC";
        }
    }
}
