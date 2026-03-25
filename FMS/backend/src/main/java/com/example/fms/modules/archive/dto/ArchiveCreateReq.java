package com.example.fms.modules.archive.dto;

public class ArchiveCreateReq {
    private Long reimbId;
    private String archiveNo;
    private String archivePath;

    public Long getReimbId() { return reimbId; }
    public void setReimbId(Long reimbId) { this.reimbId = reimbId; }
    public String getArchiveNo() { return archiveNo; }
    public void setArchiveNo(String archiveNo) { this.archiveNo = archiveNo; }
    public String getArchivePath() { return archivePath; }
    public void setArchivePath(String archivePath) { this.archivePath = archivePath; }
}
