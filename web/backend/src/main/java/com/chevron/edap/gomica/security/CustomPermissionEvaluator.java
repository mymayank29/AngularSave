package com.chevron.edap.gomica.security;


import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;

import java.io.Serializable;

public class CustomPermissionEvaluator implements PermissionEvaluator {
    @Override
    public boolean hasPermission(
            Authentication auth, Object permissionType, Object permission) {
        if ((auth == null) || (permissionType == null) || !(permission instanceof String)) {
            return false;
        }
        PermissionType pType = StringUtils.isEmpty((String) permissionType) ? null : PermissionType.valueOf((String) permissionType);
        Permitions perm = StringUtils.isEmpty((String) permission) ? null : Permitions.valueOf(((String) permission).toUpperCase());
        return AuthService.isAutorized(auth, pType, perm);
    }

    @Override
    public boolean hasPermission(
            Authentication auth, Serializable targetId, String permissionType, Object permission) {
        if ((auth == null) || (permissionType == null) || !(permission instanceof String)) {
            return false;
        }

        PermissionType pType = StringUtils.isEmpty(permissionType) ? null : PermissionType.valueOf(permissionType);
        Permitions perm = StringUtils.isEmpty((String) permission) ? null : Permitions.valueOf(((String) permission).toUpperCase());

        return AuthService.isAutorized(auth, pType, perm);
    }

}
