package com.example.fms.modules.auth.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface OrgUnitMapper {

    @Select("SELECT name FROM org_unit WHERE id = #{id}")
    String selectNameById(@Param("id") Long id);
}
