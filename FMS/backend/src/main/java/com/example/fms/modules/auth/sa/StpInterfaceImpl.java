package com.example.fms.modules.auth.sa;

import cn.dev33.satoken.stp.StpInterface;
import com.example.fms.modules.auth.mapper.RbacMapper;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * Sa-Token 权限/角色回调（后续做接口鉴权时直接可用）
 */
@Component
public class StpInterfaceImpl implements StpInterface {

    private final RbacMapper rbacMapper;

    public StpInterfaceImpl(RbacMapper rbacMapper) {
        this.rbacMapper = rbacMapper;
    }

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        try {
            Long userId = Long.valueOf(String.valueOf(loginId));
            return rbacMapper.selectPermCodesByUserId(userId);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        try {
            Long userId = Long.valueOf(String.valueOf(loginId));
            return rbacMapper.selectRoleCodesByUserId(userId);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
