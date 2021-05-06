package com.chevron.edap.gomica.security;


import static com.chevron.edap.gomica.security.Operation.EDIT;
import static javax.print.attribute.standard.PrintQuality.DRAFT;

public enum Permitions {

    ACCESS("ACCESS", PermissionType.ACCESS, null),
    EDIT("EDIT", PermissionType.EDIT, null),
    SUPPORT_ADMIN("SUPPORT_ADMIN", PermissionType.ADMIN, null);

    private String businessName;
    private DataAccessPermissions dataAccessPermissions;
    private PermissionType permissionType;

    Permitions(String permition, PermissionType permissionType, DataAccessPermissions dataAccessPermissions) {
        this.businessName = permition;
        this.permissionType = permissionType;
        this.dataAccessPermissions = dataAccessPermissions;
    }

    public PermissionType getPermissionType() {
        return permissionType;
    }

    public String getBusinessName() {
        return businessName;
    }

    public DataAccessPermissions getDataPermissions() {
        return dataAccessPermissions;
    }
}
