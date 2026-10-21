package com.teamwhite.thebuzz.model;

import java.sql.Timestamp;
import java.util.UUID;

public class Permissions extends SessionPermissions{

    // @TODO IMPLEMENT LATER WHEN ADDING PERMISSION GROUPS
    private String permissionsName;
    private UUID createdBy;
    private Timestamp createdAt;
    private String permissionDesc;

    public Permissions(String jsonString) {
        super(jsonString);
    }
}
