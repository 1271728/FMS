package com.example.fms.modules.archive.mapper;

import com.example.fms.modules.archive.dto.ArchiveVO;
import org.apache.ibatis.annotations.*;

@Mapper
public interface ArchiveMapper {

    @Select("SELECT a.id, a.reimb_id AS reimbId, a.archive_no AS archiveNo, a.archive_path AS archivePath, a.operator_user_id AS operatorUserId, su.real_name AS operatorName, DATE_FORMAT(a.archived_at, '%Y-%m-%d %H:%i:%s') AS archivedAt, DATE_FORMAT(a.created_at, '%Y-%m-%d %H:%i:%s') AS createdAt, DATE_FORMAT(a.updated_at, '%Y-%m-%d %H:%i:%s') AS updatedAt FROM rf_archive a LEFT JOIN sys_user su ON su.id = a.operator_user_id WHERE a.reimb_id = #{reimbId} LIMIT 1")
    ArchiveVO selectByReimbId(@Param("reimbId") Long reimbId);

    @Select("SELECT COUNT(1) FROM rf_archive WHERE reimb_id = #{reimbId}")
    int countByReimbId(@Param("reimbId") Long reimbId);

    @Insert("INSERT INTO rf_archive(reimb_id, archive_no, archive_path, operator_user_id, archived_at, created_at, updated_at) VALUES(#{reimbId}, #{archiveNo}, #{archivePath}, #{operatorUserId}, NOW(), NOW(), NOW())")
    int insert(@Param("reimbId") Long reimbId,
               @Param("archiveNo") String archiveNo,
               @Param("archivePath") String archivePath,
               @Param("operatorUserId") Long operatorUserId);
}
