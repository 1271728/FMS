package com.example.fms.modules.admin.user.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AdminUserMapper {

    @Select({
            "<script>",
            "SELECT ",
            "  u.id, u.username, u.real_name AS realName, u.phone, u.email, ",
            "  u.unit_id AS unitId, ou.name AS unitName, u.status, u.last_login_at AS lastLoginAt, ",
            "  GROUP_CONCAT(DISTINCT r.role_code) AS roleCodes ",
            "FROM sys_user u ",
            "LEFT JOIN org_unit ou ON ou.id = u.unit_id ",
            "LEFT JOIN sys_user_role ur ON ur.user_id = u.id ",
            "LEFT JOIN sys_role r ON r.id = ur.role_id ",
            "<where>",
            "  <if test='unitId != null'> AND u.unit_id = #{unitId} </if>",
            "  <if test='status != null'> AND u.status = #{status} </if>",
            "  <if test='keyword != null and keyword != \"\"'> ",
            "    AND (u.username LIKE CONCAT('%',#{keyword},'%') ",
            "      OR u.real_name LIKE CONCAT('%',#{keyword},'%') ",
            "      OR u.phone LIKE CONCAT('%',#{keyword},'%') ",
            "      OR u.email LIKE CONCAT('%',#{keyword},'%')) ",
            "  </if>",
            "</where>",
            "GROUP BY u.id ",
            "ORDER BY u.id DESC ",
            "LIMIT #{offset}, #{pageSize}",
            "</script>"
    })
    List<AdminUserRow> selectPage(@Param("offset") int offset,
                                 @Param("pageSize") int pageSize,
                                 @Param("unitId") Long unitId,
                                 @Param("status") Integer status,
                                 @Param("keyword") String keyword);

    @Select({
            "<script>",
            "SELECT COUNT(1) FROM sys_user u ",
            "<where>",
            "  <if test='unitId != null'> AND u.unit_id = #{unitId} </if>",
            "  <if test='status != null'> AND u.status = #{status} </if>",
            "  <if test='keyword != null and keyword != \"\"'> ",
            "    AND (u.username LIKE CONCAT('%',#{keyword},'%') ",
            "      OR u.real_name LIKE CONCAT('%',#{keyword},'%') ",
            "      OR u.phone LIKE CONCAT('%',#{keyword},'%') ",
            "      OR u.email LIKE CONCAT('%',#{keyword},'%')) ",
            "  </if>",
            "</where>",
            "</script>"
    })
    long count(@Param("unitId") Long unitId,
               @Param("status") Integer status,
               @Param("keyword") String keyword);
}
