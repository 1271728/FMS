package com.example.fms.modules.workflow.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MsgInboxMapper {

    @Insert("INSERT INTO msg_inbox(user_id, msg_type, title, content, related_biz_type, related_biz_id, is_read, created_at) VALUES(#{userId}, #{msgType}, #{title}, #{content}, #{bizType}, #{bizId}, 0, NOW())")
    int insert(@Param("userId") Long userId,
               @Param("msgType") String msgType,
               @Param("title") String title,
               @Param("content") String content,
               @Param("bizType") String bizType,
               @Param("bizId") Long bizId);
}
