package com.example.fms.modules.auth.mapper;

import com.example.fms.modules.auth.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface SysUserMapper {

    @Select("SELECT id, username, password_hash AS passwordHash, real_name AS realName, phone, email, unit_id AS unitId, status, last_login_at AS lastLoginAt " +
            "FROM sys_user WHERE username = #{username} LIMIT 1")
    SysUser selectByUsername(@Param("username") String username);

    @Select("SELECT id, username, password_hash AS passwordHash, real_name AS realName, phone, email, unit_id AS unitId, status, last_login_at AS lastLoginAt " +
            "FROM sys_user WHERE id = #{id} LIMIT 1")
    SysUser selectById(@Param("id") Long id);

    @Update("UPDATE sys_user SET last_login_at = NOW() WHERE id = #{id}")
    int updateLastLogin(@Param("id") Long id);

    @Update("UPDATE sys_user SET password_hash = #{hash}, updated_at = NOW() WHERE id = #{id}")
    int updatePassword(@Param("id") Long id, @Param("hash") String hash);

    // =========================
    // 管理员侧：用户维护
    // =========================
    @Select("SELECT COUNT(1) FROM sys_user WHERE username = #{username}")
    int countByUsername(@Param("username") String username);

    @Insert("INSERT INTO sys_user(username, password_hash, real_name, phone, email, unit_id, status, created_at, updated_at) " +
            "VALUES(#{username}, #{passwordHash}, #{realName}, #{phone}, #{email}, #{unitId}, #{status}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(SysUser user);

    @Update("UPDATE sys_user SET status = #{status}, updated_at = NOW() WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    @Update("UPDATE sys_user SET real_name = #{realName}, phone = #{phone}, email = #{email}, unit_id = #{unitId}, status = #{status}, updated_at = NOW() WHERE id = #{id}")
    int updateProfile(SysUser user);
}
