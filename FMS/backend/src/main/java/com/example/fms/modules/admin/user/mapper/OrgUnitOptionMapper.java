package com.example.fms.modules.admin.user.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrgUnitOptionMapper {

    @Select("SELECT id, name FROM org_unit WHERE enabled = 1 ORDER BY sort_no, id")
    List<UnitOption> listUnitOptions();
}
