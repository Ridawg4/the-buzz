package com.teamwhite.thebuzz.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.json.*;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.*;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.logging.Logger;

import static com.teamwhite.thebuzz.Resources.ResourcePaths.DEFAULT_USER_PERMISSIONS_LOCATION;

@Entity
public class BuzzUser implements UserDetails, CredentialsContainer {

    @Id
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID userId;
    private String username;
    private String passwordHash;
    @Nullable
    private String oauthKey;
    @Nullable
    private Timestamp joinDate;
    @Nullable
    private String userDesc;
    @Nullable
    private String fullName;
    @Nullable
    private String userPfpLocation;
    private String email;
    @JdbcTypeCode(SqlTypes.JSON)
    @JsonIgnore
    private JsonObject favorites;
    @JdbcTypeCode(SqlTypes.JSON)
    @JsonIgnore
    private JsonObject volumeSettings;
    @JdbcTypeCode(SqlTypes.JSON)
    @JsonIgnore
    private JsonObject notificationSettings;
    @JdbcTypeCode(SqlTypes.JSON)
    private SessionPermissions permissionData;

    public BuzzUser() {
        this.userId = UUID.randomUUID();
    }

    public BuzzUser(String username, String passwordHash, String email) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
        this.userId = UUID.randomUUID();
        this.oauthKey = "";

        File defaultUserPermissions = new File(DEFAULT_USER_PERMISSIONS_LOCATION);
        BufferedReader reader;
        String permissionsJsonString = "";
        try {
            reader = new BufferedReader(new FileReader(defaultUserPermissions));

            while(reader.ready()) {
                permissionsJsonString += reader.readLine();
            }
        } catch (IOException IOException) {
            Logger.getGlobal().warning("Default permissions not found, aborting");
        }

        this.permissionData = new SessionPermissions(Json.createReader(new StringReader(permissionsJsonString)).readObject());
        this.favorites = JsonObject.EMPTY_JSON_OBJECT;
        this.volumeSettings = JsonObject.EMPTY_JSON_OBJECT;
        this.notificationSettings = JsonObject.EMPTY_JSON_OBJECT;
        this.joinDate = Timestamp.from(Instant.now());
        this.userPfpLocation = "";
        this.fullName = "";
        this.userDesc = "";
    }

    public BuzzUser(String userId, String username, String passwordHash, String oauthKey,
                    Timestamp joinDate, String userDesc, String fullName, String userPfpLocation,
                    String email, String favorites, String volumeSettings,
                    String notificationSettings, String permissionData) {
        this.userId = UUID.fromString(userId);
        this.username = username;
        this.passwordHash = passwordHash;
        this.oauthKey = oauthKey;
        this.joinDate = joinDate;
        this.userDesc = userDesc;
        this.fullName = fullName;
        this.userPfpLocation = userPfpLocation;
        this.email = email;
        this.favorites = Json.createReader(new StringReader(favorites)).readObject();
        this.volumeSettings = Json.createReader(new StringReader(volumeSettings)).readObject();
        this.notificationSettings = Json.createReader(new StringReader(notificationSettings)).readObject();
        this.permissionData = new SessionPermissions(permissionData);

    }

    public String getUserId() {
        return userId.toString();
    }

    public String getPasswordHash() {
        return passwordHash;
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

    public SessionPermissions getPermissions() {
        return permissionData;
    }

    public void setPermissions(SessionPermissions permissionData) {
        this.permissionData = permissionData;
    }



    @Override
    public String toString() {
        return userId.toString();
    }

    @Override
    public void eraseCredentials() {

    }

    @Override
    // @TODO IMPLEMENT AUTHORITIES
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Turn permissionData into a collection of authorities
        ArrayList<String> allowedPermissions = permissionData.getListOfPermissions();
        ArrayList<SimpleGrantedAuthority> grantedAuthorities = new ArrayList<>();

        for(int i = 0; i < allowedPermissions.size(); i++) {
            grantedAuthorities.add(new SimpleGrantedAuthority(allowedPermissions.get(i)));
        }

        return grantedAuthorities;
    }

    @Override
    public @Nullable String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
