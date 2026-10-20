package com.teamwhite.thebuzz.model;

import jakarta.json.JsonObject;
import jakarta.json.JsonValue;

import java.sql.Timestamp;
import java.util.UUID;

public class Permissions extends SessionPermissions{

    private String permissionsName;
    private UUID createdBy;
    private Timestamp createdAt;
    private String permissionDesc;

    public Permissions(String jsonString) {
        super(jsonString);
    }
}
