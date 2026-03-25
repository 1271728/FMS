package com.example.fms.modules.archive.service;

import com.example.fms.modules.archive.dto.ArchiveCreateReq;
import com.example.fms.modules.archive.dto.ArchiveVO;

public interface ArchiveService {
    void create(ArchiveCreateReq req);
    ArchiveVO detailByReimbId(Long reimbId);
}
