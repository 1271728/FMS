package com.example.fms.modules.workflow.mapper;

import com.example.fms.modules.workflow.dto.WfLogVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface WfLogMapper {

    @Insert("INSERT INTO wf_log(biz_type, biz_id, node_code, action, actor_user_id, comment, created_at) VALUES(#{bizType}, #{bizId}, #{nodeCode}, #{action}, #{actorUserId}, #{comment}, NOW())")
    int insert(@Param("bizType") String bizType,
               @Param("bizId") Long bizId,
               @Param("nodeCode") String nodeCode,
               @Param("action") String action,
               @Param("actorUserId") Long actorUserId,
               @Param("comment") String comment);

    @Select("SELECT l.id, l.biz_type AS bizType, l.biz_id AS bizId, l.node_code AS nodeCode, l.action, l.actor_user_id AS actorUserId, su.real_name AS actorName, l.comment, DATE_FORMAT(l.created_at, '%Y-%m-%d %H:%i:%s') AS createdAt FROM wf_log l LEFT JOIN sys_user su ON su.id = l.actor_user_id WHERE l.biz_type = #{bizType} AND l.biz_id = #{bizId} ORDER BY l.id ASC")
    List<WfLogVO> selectByBiz(@Param("bizType") String bizType, @Param("bizId") Long bizId);
}
