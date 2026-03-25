package com.example.fms.modules.project.service;

import com.example.fms.common.api.PageResult;
import com.example.fms.modules.project.dto.*;

import java.util.List;

public interface ProjectService {
    PageResult<ProjectVO> page(ProjectPageReq req);
    ProjectDetailVO detail(Long id);
    Long create(ProjectCreateReq req);
    void update(ProjectUpdateReq req);
    void delete(ProjectDeleteReq req);
    void submit(ProjectSubmitReq req);
    void withdraw(ProjectWithdrawReq req);
    void audit(ProjectAuditReq req);
    List<ProjectMemberVO> members(Long projectId);
    void addMember(ProjectAddMemberReq req);
    void removeMember(ProjectRemoveMemberReq req);
}
