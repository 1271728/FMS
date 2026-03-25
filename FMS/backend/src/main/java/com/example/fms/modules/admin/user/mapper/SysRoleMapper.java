package com.example.fms.modules.admin.user.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SysRoleMapper {

    @Select("SELECT id FROM sys_role WHERE role_code = #{code} AND enabled = 1 LIMIT 1")
    Long selectIdByCode(@Param("code") String code);

    @Select("SELECT role_code AS roleCode, role_name AS roleName FROM sys_role WHERE enabled = 1 ORDER BY id")
    List<RoleOption> listRoleOptions();
}
