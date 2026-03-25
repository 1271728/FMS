package com.example.fms.modules.auth.service.impl;

import cn.dev33.satoken.secure.SaSecureUtil;
import cn.dev33.satoken.stp.StpUtil;
import com.example.fms.common.exception.BizException;
import com.example.fms.modules.auth.dto.ChangePasswordReq;
import com.example.fms.modules.auth.dto.LoginReq;
import com.example.fms.modules.auth.dto.LoginResp;
import com.example.fms.modules.auth.dto.MeResp;
import com.example.fms.modules.auth.entity.SysUser;
import com.example.fms.modules.auth.mapper.OrgUnitMapper;
import com.example.fms.modules.auth.mapper.RbacMapper;
import com.example.fms.modules.auth.mapper.SysUserMapper;
import com.example.fms.modules.auth.service.AuthService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 登录服务（最小闭环版）
 *
 * 密码策略：
 * 1) 推荐你把 sys_user.password_hash 存 SaSecureUtil.md5(password) 或更安全算法；
 * 2) 为了兼容你初始化数据里可能是 "HASH_123456" 这种占位，本实现也支持。
 */
@Service
public class AuthServiceImpl implements AuthService {

    private final SysUserMapper sysUserMapper;
    private final RbacMapper rbacMapper;
    private final OrgUnitMapper orgUnitMapper;

    public AuthServiceImpl(SysUserMapper sysUserMapper, RbacMapper rbacMapper, OrgUnitMapper orgUnitMapper) {
        this.sysUserMapper = sysUserMapper;
        this.rbacMapper = rbacMapper;
        this.orgUnitMapper = orgUnitMapper;
    }

    @Override
    public LoginResp login(LoginReq req) {
        if (req == null || isBlank(req.getUsername()) || isBlank(req.getPassword())) {
            throw BizException.badRequest("username/password不能为空");
        }

        SysUser user = sysUserMapper.selectByUsername(req.getUsername().trim());
        if (user == null) throw BizException.unauthorized("用户名或密码错误");
        if (user.getStatus() == null || user.getStatus() != 1) throw BizException.forbidden("账号已禁用");

        if (!passwordMatches(req.getPassword(), user.getPasswordHash())) {
            throw BizException.unauthorized("用户名或密码错误");
        }

        // Sa-Token 登录：loginId 用 userId
        StpUtil.login(user.getId());
        sysUserMapper.updateLastLogin(user.getId());

        String token = StpUtil.getTokenValue();
        List<String> roles = rbacMapper.selectRoleCodesByUserId(user.getId());
        List<String> perms = rbacMapper.selectPermCodesByUserId(user.getId());
        String unitName = user.getUnitId() == null ? null : orgUnitMapper.selectNameById(user.getUnitId());

        LoginResp.UserInfo ui = new LoginResp.UserInfo(
                user.getId(), user.getUsername(), user.getRealName(),
                user.getUnitId(), unitName, roles, perms,
                user.getPhone(), user.getEmail(), user.getStatus(), user.getLastLoginAt()
        );
        return new LoginResp(token, ui);
    }

    @Override
    public void logout() {
        StpUtil.logout();
    }

    @Override
    public MeResp me() {
        StpUtil.checkLogin();
        Long userId = StpUtil.getLoginIdAsLong();
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) throw BizException.unauthorized("用户不存在");

        List<String> roles = rbacMapper.selectRoleCodesByUserId(userId);
        List<String> perms = rbacMapper.selectPermCodesByUserId(userId);
        String unitName = user.getUnitId() == null ? null : orgUnitMapper.selectNameById(user.getUnitId());
        return new MeResp(
                user.getId(), user.getUsername(), user.getRealName(), user.getUnitId(), unitName,
                roles, perms, user.getPhone(), user.getEmail(), user.getStatus(), user.getLastLoginAt()
        );
    }

    @Override
    public void changePassword(ChangePasswordReq req) {
        StpUtil.checkLogin();
        if (req == null || isBlank(req.getOldPassword()) || isBlank(req.getNewPassword())) {
            throw BizException.badRequest("oldPassword/newPassword不能为空");
        }

        Long userId = StpUtil.getLoginIdAsLong();
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) throw BizException.unauthorized("用户不存在");

        if (!passwordMatches(req.getOldPassword(), user.getPasswordHash())) {
            throw BizException.badRequest("旧密码不正确");
        }

        // 建议存 md5 或更强（可后续改为 BCrypt）
        String newHash = SaSecureUtil.md5(req.getNewPassword());
        sysUserMapper.updatePassword(userId, newHash);
    }

    private boolean passwordMatches(String raw, String stored) {
        if (stored == null) return false;

        // 兼容初始化数据占位：HASH_123456
        if (stored.startsWith("HASH_")) {
            return stored.equals("HASH_" + raw);
        }

        // 推荐：password_hash 存 md5
        // 如果你库里已经存 md5，这里直接匹配
        if (stored.length() == 32 && stored.matches("[0-9a-fA-F]{32}")) {
            return stored.equalsIgnoreCase(SaSecureUtil.md5(raw));
        }

        // 兜底：明文（不推荐，仅调试）
        return stored.equals(raw);
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
