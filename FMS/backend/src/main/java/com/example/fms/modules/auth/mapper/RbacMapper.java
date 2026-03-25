package com.example.fms.modules.auth.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RbacMapper {

    @Select("SELECT r.role_code " +
            "FROM sys_user_role ur " +
            "JOIN sys_role r ON r.id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND r.enabled = 1")
    List<String> selectRoleCodesByUserId(@Param("userId") Long userId);

    @Select("SELECT p.perm_code " +
            "FROM sys_user_role ur " +
            "JOIN sys_role_perm rp ON rp.role_id = ur.role_id " +
            "JOIN sys_perm p ON p.id = rp.perm_id " +
            "WHERE ur.user_id = #{userId}")
    List<String> selectPermCodesByUserId(@Param("userId") Long userId);
}
