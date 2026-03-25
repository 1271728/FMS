package com.example.fms.modules.admin.user.service.impl;

import cn.dev33.satoken.secure.SaSecureUtil;
import com.example.fms.common.api.PageResult;
import com.example.fms.common.exception.BizException;
import com.example.fms.modules.admin.user.dto.*;
import com.example.fms.modules.admin.user.mapper.*;
import com.example.fms.modules.admin.user.service.AdminUserService;
import com.example.fms.modules.auth.entity.SysUser;
import com.example.fms.modules.auth.mapper.SysUserMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminUserServiceImpl implements AdminUserService {

    private final SysUserMapper sysUserMapper;
    private final AdminUserMapper adminUserMapper;
    private final SysRoleMapper sysRoleMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final OrgUnitOptionMapper orgUnitOptionMapper;

    public AdminUserServiceImpl(SysUserMapper sysUserMapper,
                               AdminUserMapper adminUserMapper,
                               SysRoleMapper sysRoleMapper,
                               SysUserRoleMapper sysUserRoleMapper,
                               OrgUnitOptionMapper orgUnitOptionMapper) {
        this.sysUserMapper = sysUserMapper;
        this.adminUserMapper = adminUserMapper;
        this.sysRoleMapper = sysRoleMapper;
        this.sysUserRoleMapper = sysUserRoleMapper;
        this.orgUnitOptionMapper = orgUnitOptionMapper;
    }

    @Override
    public Long create(AdminUserCreateReq req) {
        if (req == null) throw BizException.badRequest("请求不能为空");
        String username = safeTrim(req.getUsername());
        String password = req.getPassword();
        String realName = safeTrim(req.getRealName());

        if (isBlank(username) || isBlank(password) || isBlank(realName)) {
            throw BizException.badRequest("username/password/realName不能为空");
        }
        if (sysUserMapper.countByUsername(username) > 0) {
            throw BizException.badRequest("用户名已存在");
        }

        List<String> roleCodes = req.getRoleCodes() == null ? Collections.emptyList() : req.getRoleCodes();
        roleCodes = roleCodes.stream().filter(s -> !isBlank(s)).map(String::trim).distinct().collect(Collectors.toList());
        if (roleCodes.isEmpty()) {
            throw BizException.badRequest("请至少选择一个角色roleCodes");
        }

        SysUser u = new SysUser();
        u.setUsername(username);
        u.setPasswordHash(SaSecureUtil.md5(password));
        u.setRealName(realName);
        u.setPhone(safeTrim(req.getPhone()));
        u.setEmail(safeTrim(req.getEmail()));
        u.setUnitId(req.getUnitId());
        u.setStatus(req.getStatus() == null ? 1 : req.getStatus());

        sysUserMapper.insert(u);
        if (u.getId() == null) throw BizException.serverError("创建用户失败");

        // 绑定角色
        for (String code : roleCodes) {
            Long roleId = sysRoleMapper.selectIdByCode(code);
            if (roleId == null) {
                throw BizException.badRequest("角色不存在或未启用: " + code);
            }
            sysUserRoleMapper.insert(u.getId(), roleId);
        }
        return u.getId();
    }

    @Override
    public void update(AdminUserUpdateReq req) {
        if (req == null || req.getId() == null) throw BizException.badRequest("id不能为空");
        SysUser old = sysUserMapper.selectById(req.getId());
        if (old == null) throw BizException.notFound("用户不存在");

        String realName = safeTrim(req.getRealName());
        if (isBlank(realName)) throw BizException.badRequest("realName不能为空");

        old.setRealName(realName);
        old.setPhone(safeTrim(req.getPhone()));
        old.setEmail(safeTrim(req.getEmail()));
        old.setUnitId(req.getUnitId());
        old.setStatus(req.getStatus() == null ? old.getStatus() : req.getStatus());
        sysUserMapper.updateProfile(old);
    }

    @Override
    public PageResult<AdminUserVO> page(AdminUserPageReq req) {
        int pageNo = req == null || req.getPageNo() == null || req.getPageNo() < 1 ? 1 : req.getPageNo();
        int pageSize = req == null || req.getPageSize() == null || req.getPageSize() < 1 ? 10 : req.getPageSize();
        int offset = (pageNo - 1) * pageSize;

        Long unitId = req == null ? null : req.getUnitId();
        Integer status = req == null ? null : req.getStatus();
        String keyword = req == null ? null : safeTrim(req.getKeyword());

        long total = adminUserMapper.count(unitId, status, keyword);
        List<AdminUserRow> rows = adminUserMapper.selectPage(offset, pageSize, unitId, status, keyword);
        List<AdminUserVO> list = new ArrayList<>();
        for (AdminUserRow r : rows) {
            AdminUserVO vo = new AdminUserVO();
            vo.setId(r.getId());
            vo.setUsername(r.getUsername());
            vo.setRealName(r.getRealName());
            vo.setPhone(r.getPhone());
            vo.setEmail(r.getEmail());
            vo.setUnitId(r.getUnitId());
            vo.setUnitName(r.getUnitName());
            vo.setStatus(r.getStatus());
            vo.setLastLoginAt(r.getLastLoginAt());
            vo.setRoles(splitRoleCodes(r.getRoleCodes()));
            list.add(vo);
        }
        return new PageResult<>(list, total, pageNo, pageSize);
    }

    @Override
    public void setRoles(AdminUserSetRolesReq req) {
        if (req == null || req.getUserId() == null) throw BizException.badRequest("userId不能为空");
        List<String> roleCodes = req.getRoleCodes() == null ? Collections.emptyList() : req.getRoleCodes();
        roleCodes = roleCodes.stream().filter(s -> !isBlank(s)).map(String::trim).distinct().collect(Collectors.toList());
        if (roleCodes.isEmpty()) throw BizException.badRequest("roleCodes不能为空");

        SysUser u = sysUserMapper.selectById(req.getUserId());
        if (u == null) throw BizException.notFound("用户不存在");

        sysUserRoleMapper.deleteByUserId(req.getUserId());
        for (String code : roleCodes) {
            Long roleId = sysRoleMapper.selectIdByCode(code);
            if (roleId == null) throw BizException.badRequest("角色不存在或未启用: " + code);
            sysUserRoleMapper.insert(req.getUserId(), roleId);
        }
    }

    @Override
    public void setStatus(AdminUserStatusReq req) {
        if (req == null || req.getUserId() == null || req.getStatus() == null) {
            throw BizException.badRequest("userId/status不能为空");
        }
        SysUser u = sysUserMapper.selectById(req.getUserId());
        if (u == null) throw BizException.notFound("用户不存在");
        sysUserMapper.updateStatus(req.getUserId(), req.getStatus());
    }

    @Override
    public void resetPassword(AdminUserResetPwdReq req) {
        if (req == null || req.getUserId() == null) throw BizException.badRequest("userId不能为空");
        SysUser u = sysUserMapper.selectById(req.getUserId());
        if (u == null) throw BizException.notFound("用户不存在");

        String newPwd = isBlank(req.getNewPassword()) ? "123456" : req.getNewPassword();
        sysUserMapper.updatePassword(req.getUserId(), SaSecureUtil.md5(newPwd));
    }

    @Override
    public List<RoleOption> roleOptions() {
        return sysRoleMapper.listRoleOptions();
    }

    @Override
    public List<UnitOption> unitOptions() {
        return orgUnitOptionMapper.listUnitOptions();
    }

    private List<String> splitRoleCodes(String roleCodes) {
        if (isBlank(roleCodes)) return Collections.emptyList();
        return Arrays.stream(roleCodes.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .collect(Collectors.toList());
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private String safeTrim(String s) {
        return s == null ? null : s.trim();
    }
}
