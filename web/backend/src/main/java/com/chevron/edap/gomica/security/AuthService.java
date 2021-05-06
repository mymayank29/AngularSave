package com.chevron.edap.gomica.security;


import com.chevron.edap.gomica.config.ConfigurationProvider;
import com.chevron.edap.gomica.security.dto.User;
import com.chevron.edap.gomica.security.dto.UserDetailsWrapper;
//import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AuthService implements IAuthService, InitializingBean {
    Logger LOGGER = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private ConfigurationProvider configurationProvider;

    private static AuthService instance;

    @Override
    public void afterPropertiesSet() throws Exception {
        instance = this;
    }

    public ConfigurationProvider getConfigurationProvider() {
        return configurationProvider;
    }



    @Override
    public User whoAmI() {
        return getUser();
    }


    public static User getUser() {
        Authentication authentication = getAuthentication();
        User user = new User();
        user.setName(authentication.getName());
        Set<Role> userRoles = getUserRoles();
        user.setRoles(userRoles);
        user.setPermissions(Role.getRolesPermitions(userRoles));
        user.setDataAccessPermissions(getDataAccessPermissions());
        user.setLdapRoles(getLdapRoles());
        user.setDataEditPermissions(getDataEditPermissions());

        if (authentication.getPrincipal() instanceof UserDetailsWrapper) {
            user.setDisplayName(((UserDetailsWrapper) authentication.getPrincipal()).getDisplayName());
        } else {
            user.setDisplayName(authentication.getName());
        }
        return user;
    }

    @Override
    public String getUserDisplayName() {
        Authentication authentication = getAuthentication();
        String CAI = authentication.getName();
        String displayName;
        if (authentication.getPrincipal() instanceof UserDetailsWrapper) {
            displayName = ((UserDetailsWrapper) authentication.getPrincipal()).getDisplayName();
        } else {
            displayName = authentication.getName();
        }
        return CAI + " , " + displayName;
    }


    public static Set<DataAccessPermissions> getDataAccessPermissions() {
        Set<Role> userRoles = getUserRoles();
        return Role.getRolesPermitions(userRoles).stream().map(Permitions::getDataPermissions)
                .filter(Objects::nonNull).filter(e -> e.getOperation().equals(Operation.READ)).collect(Collectors.toSet());
    }

    public static Set<DataAccessPermissions> getDataEditPermissions() {
        Set<Role> userRoles = getUserRoles();
        return Role.getRolesPermitions(userRoles).stream().map(Permitions::getDataPermissions)
                .filter(Objects::nonNull).filter(e -> e.getOperation().equals(Operation.EDIT)).collect(Collectors.toSet());
    }

    public static Set<DataAccessPermissions> getDataEditReleaseRoles() {
        Set<Role> userRoles = getUserRoles();
        return Role.getRolesPermitions(userRoles).stream().map(Permitions::getDataPermissions)
                .filter(Objects::nonNull).filter(e -> e.getOperation().equals(Operation.CREATE_RELEASE)).collect(Collectors.toSet());
    }


    public static void runAsSystemUser() {
        String schedulerJobRoles = Role.DEV_TEAM.getRole();
        List<GrantedAuthority> authorities = Arrays.stream(schedulerJobRoles.split(",")).map(Role::getRoleFromBusinessName).filter(Objects::nonNull).map(role -> new SimpleGrantedAuthority(role.getLdapRole())).collect(Collectors.toSet()).stream().collect(Collectors.toList());
        Authentication auth = new UsernamePasswordAuthenticationToken("System", "", authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    public static void provideAuthIntoSecurityContext(Authentication authentication) {
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private static Set<Role> getUserRoles(Authentication auth) {

        return Role.ldapRolesToRoles(auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet()), getEnabledEnvSpecificRoles());
    }

    private static Set<Role> getEnabledEnvSpecificRoles() {
        if (instance == null || instance.getConfigurationProvider() == null) {
            return Collections.emptySet();
        }
        return instance.configurationProvider.getEnabledEnvSpecificRoles();
    }

    private static Set<Role> getUserRoles() {

        return getUserRoles(getAuthentication());
    }

    private static Set<String> getLdapRoles() {
        return getAuthentication().getAuthorities().stream().map(GrantedAuthority::getAuthority).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public static boolean isAutorized(Authentication auth, PermissionType permissionType, Permitions permission) {

        if (permissionType == null && permission == null) {
            return false;
        }
        Set<Role> roles = getUserRoles(auth);
        Set<Permitions> userPermissions = Role.getRolesPermitions(roles);
        Set<PermissionType> userPermissionTypes = Role.getRolesPermitionsTypes(roles);
        if (permissionType != null && !userPermissionTypes.contains(permissionType)) {
            return false;
        }
        if (permission != null && !userPermissions.contains(permission)) {
            return false;
        }
        return true;
    }

    private static boolean checkMaintenance(Set<Permitions> userPermissions){
//        if (CollectionUtils.isNotEmpty(userPermissions) && userPermissions.contains(Permitions.CREATE_RELEASE)){
//            return false;
//        }
        return true;
    }
}