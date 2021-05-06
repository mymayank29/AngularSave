package com.chevron.edap.gomica.security.dto;

import com.chevron.edap.gomica.security.DataAccessPermissions;
import com.chevron.edap.gomica.security.Permitions;
import com.chevron.edap.gomica.security.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;


import java.io.Serializable;
import java.util.Set;

public class User implements Serializable{
    private String name;
    private String displayName;
    private Set<Role> roles;
    private Set<Permitions> permissions;
    private Set<DataAccessPermissions> dataAccessPermissions;
    private Set<DataAccessPermissions> dataEditPermissions;
    @JsonIgnore
    private Set<String> ldapRoles;

    public Set<String> getLdapRoles() {
        return ldapRoles;
    }

    public void setLdapRoles(Set<String> ldapRoles) {
        this.ldapRoles = ldapRoles;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDbName() {
        return name;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public Set<Permitions> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<Permitions> permissions) {
        this.permissions = permissions;
    }

    public Set<DataAccessPermissions> getDataAccessPermissions() {
        return dataAccessPermissions;
    }

    public void setDataAccessPermissions(Set<DataAccessPermissions> dataAccessPermissions) {
        this.dataAccessPermissions = dataAccessPermissions;
    }

    public Set<DataAccessPermissions> getDataEditPermissions() {
        return dataEditPermissions;
    }

    public void setDataEditPermissions(Set<DataAccessPermissions> dataEditPermissions) {
        this.dataEditPermissions = dataEditPermissions;
    }
}
