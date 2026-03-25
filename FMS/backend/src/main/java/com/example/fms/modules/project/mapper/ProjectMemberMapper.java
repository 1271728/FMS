package com.example.fms.modules.project.mapper;

import com.example.fms.modules.project.dto.ProjectMemberVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ProjectMemberMapper {

    @Select("SELECT pm.user_id AS userId, u.username, u.real_name AS realName, u.unit_id AS unitId, ou.name AS unitName, pm.member_role AS memberRole, DATE_FORMAT(pm.joined_at, '%Y-%m-%d %H:%i:%s') AS joinedAt FROM rf_project_member pm JOIN sys_user u ON u.id = pm.user_id LEFT JOIN org_unit ou ON ou.id = u.unit_id WHERE pm.project_id = #{projectId} ORDER BY pm.id ASC")
    List<ProjectMemberVO> selectByProjectId(@Param("projectId") Long projectId);

    @Select("SELECT COUNT(1) FROM rf_project_member WHERE project_id = #{projectId} AND user_id = #{userId}")
    int countByProjectIdAndUserId(@Param("projectId") Long projectId, @Param("userId") Long userId);

    @Insert("INSERT INTO rf_project_member(project_id, user_id, member_role, joined_at) VALUES(#{projectId}, #{userId}, #{memberRole}, NOW())")
    int insert(@Param("projectId") Long projectId, @Param("userId") Long userId, @Param("memberRole") String memberRole);

    @Delete("DELETE FROM rf_project_member WHERE project_id = #{projectId} AND user_id = #{userId}")
    int delete(@Param("projectId") Long projectId, @Param("userId") Long userId);
}
