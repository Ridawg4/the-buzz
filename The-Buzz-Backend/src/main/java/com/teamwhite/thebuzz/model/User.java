package com.teamwhite.thebuzz.model;

import jakarta.json.JsonObject;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.sql.Timestamp;
import java.util.UUID;

@Entity
public class User {

    @Id
    private UUID userId;
    private String username;
    private String passwordHash;
    private String oauthKey;
    private Timestamp joinDate;
    private String userDesc;
    private String fullName;
    private String userPfpLocation;
    private String email;
    @JdbcTypeCode(SqlTypes.JSON)
    private JsonObject favorites;
    @JdbcTypeCode(SqlTypes.JSON)
    private JsonObject volumeSettings;
    @JdbcTypeCode(SqlTypes.JSON)
    private JsonObject notificationSettings;
    @JdbcTypeCode(SqlTypes.JSON)
    private JsonObject permissionData;

    public User() {
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getOauthKey() {
        return oauthKey;
    }

    public void setOauthKey(String oauthKey) {
        this.oauthKey = oauthKey;
    }

    public Timestamp getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(Timestamp joinDate) {
        this.joinDate = joinDate;
    }

    public String getUserDesc() {
        return userDesc;
    }

    public void setUserDesc(String userDesc) {
        this.userDesc = userDesc;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUserPfpLocation() {
        return userPfpLocation;
    }

    public void setUserPfpLocation(String userPfpLocation) {
        this.userPfpLocation = userPfpLocation;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public JsonObject getFavorites() {
        return favorites;
    }

    public void setFavorites(JsonObject favorites) {
        this.favorites = favorites;
    }

    public JsonObject getVolumeSettings() {
        return volumeSettings;
    }

    public void setVolumeSettings(JsonObject volumeSettings) {
        this.volumeSettings = volumeSettings;
    }

    public JsonObject getNotificationSettings() {
        return notificationSettings;
    }

    public void setNotificationSettings(JsonObject notificationSettings) {
        this.notificationSettings = notificationSettings;
    }

    public JsonObject getPermissionData() {
        return permissionData;
    }

    public void setPermissionData(JsonObject permissionData) {
        this.permissionData = permissionData;
    }

    @Override
    public String toString() {
        return username + email;
    }
}
