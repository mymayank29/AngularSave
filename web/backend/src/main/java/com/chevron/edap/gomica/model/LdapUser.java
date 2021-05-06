package com.chevron.edap.gomica.model;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public class LdapUser {

    private String uid;
    private String displayName;
    private String alias;

    private String created_by;
    private Date created_date;
    private String updated_by;
    private Date updated_date;
    private String mail;

    List<LdapRole> roles;
    List<String> permission;

    public LdapUser() {
    }

    public LdapUser(String alias) {
        this.alias = alias;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }



    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public Date getCreated_date() {
        return created_date;
    }

    public void setCreated_date(Date created_date) {
        this.created_date = created_date;
    }

    public String getUpdated_by() {
        return updated_by;
    }

    public void setUpdated_by(String updated_by) {
        this.updated_by = updated_by;
    }

    public Date getUpdated_date() {
        return updated_date;
    }

    public void setUpdated_date(Date updated_date) {
        this.updated_date = updated_date;
    }

    public List<LdapRole> getRoles() {
        return roles;
    }

    public void setRoles(List<LdapRole> roles) {
        this.roles = roles;
    }

    public List<String> getPermission() {
        return permission;
    }

    public void setPermission(List<String> permission) {
        this.permission = permission;
    }

    public void setMail(String email) {
        this.mail = email;
    }

    public String getMail() {
        return mail;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LdapUser ldapUser = (LdapUser) o;
        return Objects.equals(alias, ldapUser.alias);
    }

    @Override
    public int hashCode() {

        return Objects.hash(alias);
    }
}