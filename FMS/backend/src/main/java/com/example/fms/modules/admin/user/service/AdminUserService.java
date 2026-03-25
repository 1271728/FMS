package com.example.fms.modules.admin.user.service;

import com.example.fms.common.api.PageResult;
import com.example.fms.modules.admin.user.dto.*;
import com.example.fms.modules.admin.user.mapper.RoleOption;
import com.example.fms.modules.admin.user.mapper.UnitOption;

import java.util.List;

public interface AdminUserService {

    Long create(AdminUserCreateReq req);

    void update(AdminUserUpdateReq req);

    PageResult<AdminUserVO> page(AdminUserPageReq req);

    void setRoles(AdminUserSetRolesReq req);

    void setStatus(AdminUserStatusReq req);

    void resetPassword(AdminUserResetPwdReq req);

    List<RoleOption> roleOptions();

    List<UnitOption> unitOptions();
}
