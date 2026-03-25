package com.example.fms.modules.msg.mapper;

import com.example.fms.modules.msg.dto.MsgVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MsgCenterMapper {

    @Select({
            "<script>",
            "SELECT id, msg_type AS msgType, title, content, related_biz_type AS relatedBizType, related_biz_id AS relatedBizId, is_read AS isRead, DATE_FORMAT(read_at, '%Y-%m-%d %H:%i:%s') AS readAt, DATE_FORMAT(created_at, '%Y-%m-%d %H:%i:%s') AS createdAt ",
            "FROM msg_inbox ",
            "WHERE user_id = #{userId} ",
            "  <if test='isRead != null'> AND is_read = #{isRead} </if>",
            "  <if test='msgType != null and msgType != \"\"'> AND msg_type = #{msgType} </if>",
            "ORDER BY id DESC LIMIT #{offset}, #{pageSize}",
            "</script>"
    })
    List<MsgVO> selectPage(@Param("offset") int offset,
                           @Param("pageSize") int pageSize,
                           @Param("userId") Long userId,
                           @Param("isRead") Integer isRead,
                           @Param("msgType") String msgType);

    @Select({
            "<script>",
            "SELECT COUNT(1) FROM msg_inbox WHERE user_id = #{userId} ",
            "  <if test='isRead != null'> AND is_read = #{isRead} </if>",
            "  <if test='msgType != null and msgType != \"\"'> AND msg_type = #{msgType} </if>",
            "</script>"
    })
    long countPage(@Param("userId") Long userId,
                   @Param("isRead") Integer isRead,
                   @Param("msgType") String msgType);

    @Select("SELECT COUNT(1) FROM msg_inbox WHERE user_id = #{userId} AND is_read = 0")
    int countUnread(@Param("userId") Long userId);

    @Update("UPDATE msg_inbox SET is_read = 1, read_at = NOW() WHERE id = #{id} AND user_id = #{userId} AND is_read = 0")
    int markRead(@Param("userId") Long userId, @Param("id") Long id);

    @Update({
            "<script>",
            "UPDATE msg_inbox SET is_read = 1, read_at = NOW() WHERE user_id = #{userId} AND is_read = 0 AND id IN ",
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>#{id}</foreach>",
            "</script>"
    })
    int markReadBatch(@Param("userId") Long userId, @Param("ids") List<Long> ids);
}
