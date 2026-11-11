package com.teamwhite.thebuzz.model;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.springframework.session.Session;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class Permissions extends SessionPermissions{

    @Id
    private int dontId;
    private String permissionsName;
    private String permissionDesc;
    private UUID createdBy;
    private Timestamp createdAt;

    public Permissions() {
//        super();
    }

    public Permissions(String jsonString) {
        super(jsonString);
    }

    public Permissions(Integer id, String permissionsName, String permissionDesc, String jsonString, String createdBy, LocalDateTime createdAt) {
        super(jsonString);

        this.permissionId = id;
        this.permissionsName = permissionsName;
        this.permissionDesc = permissionDesc;
        this.createdBy = UUID.fromString(createdBy);
        this.createdAt = Timestamp.valueOf(createdAt);
    }

    public Permissions(String permissionsName, String permissionDesc, SessionPermissions sessionPermissions, String createdBy, LocalDateTime createdAt) {
        super(sessionPermissions.asJsonObject());

        this.permissionsName = permissionsName;
        this.permissionDesc = permissionDesc;
        this.createdBy = UUID.fromString(createdBy);
        this.createdAt = Timestamp.valueOf(createdAt);
    }

    public String getPermissionsName() {
        return permissionsName;
    }

    public void setPermissionsName(String permissionsName) {
        this.permissionsName = permissionsName;
    }

    public String getCreatedBy() {
        return createdBy.toString();
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = UUID.fromString(createdBy);
    }

    public String getPermissionDesc() {
        return permissionDesc;
    }

    public void setPermissionDesc(String permissionDesc) {
        this.permissionDesc = permissionDesc;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public JsonObject getSessionPermissionsAsJsonObject() {
        return super.asJsonObject();
    }

    @Override
    public JsonObject asJsonObject() {
        JsonObjectBuilder builder = Json.createObjectBuilder();

        builder.add("permission_id", permissionId);
        builder.add("permission_name", permissionsName);
        builder.add("permission_desc", permissionDesc);
        builder.add("created_by", createdBy.toString());
        builder.add("created_at", createdAt.toString());
        builder.add("permission_data", buildPermissionsJsonArray());

        return builder.build();
    }
}
