package com.example.fms.modules.admin.org.mapper;

import com.example.fms.modules.admin.org.dto.OrgTreeNode;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrgTreeMapper {

    @Select("SELECT id, parent_id AS parentId, code, name, enabled, sort_no AS sortNo FROM org_unit ORDER BY sort_no ASC, id ASC")
    List<OrgTreeNode> selectAll();
}
