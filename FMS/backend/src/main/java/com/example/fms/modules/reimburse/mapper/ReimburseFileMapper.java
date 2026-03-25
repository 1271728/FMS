package com.example.fms.modules.reimburse.mapper;

import com.example.fms.modules.reimburse.dto.ReimburseAttachmentVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ReimburseFileMapper {

    @Select("SELECT id, reimb_id AS reimburseId, file_type AS fileCategory, file_name AS originalName, NULL AS storageName, file_url AS fileUrl, file_size AS fileSize, DATE_FORMAT(uploaded_at, '%Y-%m-%d %H:%i:%s') AS createdAt FROM rf_reimb_attachment WHERE reimb_id = #{reimburseId} ORDER BY id ASC")
    List<ReimburseAttachmentVO> selectByReimburseId(@Param("reimburseId") Long reimburseId);

    @Delete("DELETE FROM rf_reimb_attachment WHERE reimb_id = #{reimburseId}")
    int deleteByReimburseId(@Param("reimburseId") Long reimburseId);

    @Insert("INSERT INTO rf_reimb_attachment(reimb_id, file_type, file_name, file_url, file_size, uploaded_by, uploaded_at) VALUES(#{reimburseId}, #{fileCategory}, #{originalName}, #{fileUrl}, #{fileSize}, #{uploadedBy}, NOW())")
    int insert(@Param("reimburseId") Long reimburseId,
               @Param("fileCategory") String fileCategory,
               @Param("originalName") String originalName,
               @Param("fileUrl") String fileUrl,
               @Param("fileSize") Long fileSize,
               @Param("uploadedBy") Long uploadedBy);
}
