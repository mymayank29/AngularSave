package com.chevron.edap.gomica.security;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum Role {

    DEV_TEAM("DEV_TEAM", "g_EDAP_GOMICA_TEAM_DEV", 2,
            Arrays.stream(new Permitions[]{ Permitions.SUPPORT_ADMIN, Permitions.ACCESS, Permitions.EDIT }).collect(Collectors.toSet()),
            RoleType.ENV_SPECIFIC),

    GOM_ICA_TEAM("EDAP_GOMICA_OWNER", "EDAP_GOMICA_OWNER", 1,
            Arrays.stream(new Permitions[]{ Permitions.ACCESS, Permitions.EDIT }).collect(Collectors.toSet()),
            RoleType.APPLICATION),

    DEMO("g_EDAP_GOMICA_TEAM_DEMO", "g_EDAP_GOMICA_TEAM_DEMO", 3,
            Arrays.stream(new Permitions[]{ Permitions.ACCESS, Permitions.EDIT }).collect(Collectors.toSet()),
            RoleType.APPLICATION),

    PROD_USER("GOMBU_AISA_PROD_USER", "GOMBU_AISA_PROD_USER", 1,
         Arrays.stream(new Permitions[]{ Permitions.ACCESS, Permitions.EDIT }).collect(Collectors.toSet()),
    RoleType.APPLICATION),

    DAST_USER("GOMBU_AISA_UAT_DAST", "GOMBU_AISA_UAT_DAST", 2,
              Arrays.stream(new Permitions[]{ Permitions.ACCESS, Permitions.EDIT }).collect(Collectors.toSet()),
    RoleType.APPLICATION);

    private Set<Permitions> permitions;
    private String role;
    private String ldapRole;
    private int priority;
    private RoleType roleType;

    Role(String role, String ldapRole, int priority, Set<Permitions> permitions, RoleType roleType) {
        this.role = role;
        this.ldapRole = ldapRole;
        this.priority = priority;
        this.permitions = permitions;
        this.roleType = roleType;
    }

    public String getLdapRole() {
        return ldapRole;
    }

    public String getRole() {
        return role;
    }

    public int getPriority() {
        return priority;
    }

    public RoleType getRoleType() {
        return roleType;
    }

    public Set<Permitions> getRolesPermitions() {
        return permitions;
    }

    public static Set<Permitions> getRolesPermitions(Set<Role> roles) {
        return roles.stream().map(Role::getRolesPermitions).flatMap(p -> p.stream()).collect(Collectors.toSet());
    }

    public static Set<PermissionType> getRolesPermitionsTypes(Set<Role> roles) {
        return roles.stream().map(Role::getRolesPermitions).flatMap(p -> p.stream()).map(Permitions::getPermissionType).collect(Collectors.toSet());
    }

    public static Role getRoleFromBusinessName(String role) {
        return Arrays.stream(Role.values()).filter(r -> role.trim().equals((r.getRole()))).findFirst().orElse(null);
    }

    public static Role mapToRole(String role) {
        return Arrays.stream(Role.values()).filter(r -> role.equals(r.toString())).findFirst().orElse(null);
    }

    public static Set<Role> ldapRolesToRoles(Set<String> ldapRoles, Set<Role> enabledEnvSpecificRoles) {
        return Arrays.stream(Role.values()).filter(role -> ldapRoles.contains(role.getLdapRole())).
                filter(role -> isValidRole(role, enabledEnvSpecificRoles) ).collect(Collectors.toSet());
    }

    private static boolean isValidRole(Role role, Set<Role> enabledEnvSpecificRoles){
        if (RoleType.APPLICATION == role.roleType){
            return true;
        }
        if (RoleType.ENV_SPECIFIC == role.roleType){
            return enabledEnvSpecificRoles.contains(role);
        }
        return false;
    }

    public static Set<Role> rolesAsStringToRoles(Set<String> roles) {
        return Arrays.stream(Role.values()).filter(role -> roles.contains(role.name())).collect(Collectors.toSet());
    }
}