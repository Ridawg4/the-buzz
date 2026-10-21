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

/**
 * The User class
 */
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
    @JsonIgnore // @TODO Might be an issue soon
    private JsonObject favorites;
    @JdbcTypeCode(SqlTypes.JSON)
    @JsonIgnore // @TODO Might be an issue soon
    private JsonObject volumeSettings;
    @JdbcTypeCode(SqlTypes.JSON)
    @JsonIgnore // @TODO Might be an issue soon
    private JsonObject notificationSettings;
    @JdbcTypeCode(SqlTypes.JSON)
    private SessionPermissions permissionData;

    /**
     * Default constructor; Only sets the ID
     * Unsure if this is called at all, is required for repository maybe?
     */
    public BuzzUser() {
        this.userId = UUID.randomUUID();
    }

    /**
     * Creates a user with a specified username, HASHED password, and email,
     * with all else set to defaults
     * @param username String username of the user
     * @param passwordHash String hashed password of the user
     * @param email String email of the user
     */
    public BuzzUser(String username, String passwordHash, String email) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
        this.userId = UUID.randomUUID();
        this.oauthKey = "";
        this.permissionData = new SessionPermissions();
        this.favorites = JsonObject.EMPTY_JSON_OBJECT;
        this.volumeSettings = JsonObject.EMPTY_JSON_OBJECT;
        this.notificationSettings = JsonObject.EMPTY_JSON_OBJECT;
        this.joinDate = Timestamp.from(Instant.now());
        this.userPfpLocation = "";
        this.fullName = "";
        this.userDesc = "";
    }

    /**
     * Creates a BuzzUser with all variables specified, used when mapping db entry to BuzzUser
     * @param userId String user's ID
     * @param username String user's username
     * @param passwordHash String HASHED password
     * @param oauthKey String user's OAuthKey
     * @param joinDate Timestamp user's join date
     * @param userDesc String user's description
     * @param fullName String user's full name
     * @param userPfpLocation String user's pfp location
     * @param email String user's email
     * @param favorites String user's favorites list as Json formatted String
     * @param volumeSettings String user's volume settings as Json formatted String
     * @param notificationSettings String user's notification settings as Json formatted String
     * @param permissionData String user's permissions as Json formatted String
     */
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

    /**
     * Gets the userId
     * @return String userId
     */
    public String getUserId() {
        return userId.toString();
    }

    /**
     * Gets the HASHED password
     * @apiNote SHOULD NOT BE ACCESSED BESIDE FROM AUTHENTICATION PURPOSES
     * @return String HASHED password
     */
    public String getPasswordHash() {
        return passwordHash;
    }

    /**
     * Gets the users OAuthKey
     * @apiNote SHOULD NOT BE ACCESSED BESIDE FROM AUTHENTICATION PURPOSES
     * @return String OAuthKey
     */
    public String getOauthKey() {
        return oauthKey;
    }

    /**
     * Sets the OAuthKey
     * @param oauthKey String OAuthKey
     */
    public void setOauthKey(String oauthKey) {
        this.oauthKey = oauthKey;
    }

    /**
     * Gets the user's join date
     * @return Timestamp join date
     */
    public Timestamp getJoinDate() {
        return joinDate;
    }

    /**
     * Gets the user's description
     * @return String user description
     */
    public String getUserDesc() {
        return userDesc;
    }

    /**
     * Sets the user's description
     * @param userDesc String user description
     */
    public void setUserDesc(String userDesc) {
        this.userDesc = userDesc;
    }

    /**
     * Gets the user's full name
     * @return String fullName
     * @TODO maybe add delimiter to split name for front end
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Sets the user's full name
     * @param fullName String new fullName
     * @TODO maybe add delimiter to split name for front end
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /**
     * Gets the user's pfp location on storage
     * @return String user's pfp location
     */
    public String getUserPfpLocation() {
        return userPfpLocation;
    }

    /**
     * Sets the user's pfp location
     * @param userPfpLocation String new pfp location
     */
    public void setUserPfpLocation(String userPfpLocation) {
        this.userPfpLocation = userPfpLocation;
    }

    /**
     * Gets a user's email
     * @return String email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Gets the user's favorites list
     * @return JsonObject favorites list
     */
    public JsonObject getFavorites() {
        return favorites;
    }

    /**
     * Sets the user's favorites list
     * @param favorites JsonObject new favorites
     */
    public void setFavorites(JsonObject favorites) {
        this.favorites = favorites;
    }

    /**
     * Gets the user's volume settings
     * @return JsonObject volume settings
     */
    public JsonObject getVolumeSettings() {
        return volumeSettings;
    }

    /**
     * Sets the user's volume settings
     * @param volumeSettings new volume settings
     */
    public void setVolumeSettings(JsonObject volumeSettings) {
        this.volumeSettings = volumeSettings;
    }

    /**
     * Gets the user's notification settings
     * @return JsonObject notification settings
     */
    public JsonObject getNotificationSettings() {
        return notificationSettings;
    }

    /**
     * Sets the user's notification settings
     * @param notificationSettings JsonObject new notification settings
     */
    public void setNotificationSettings(JsonObject notificationSettings) {
        this.notificationSettings = notificationSettings;
    }

    /**
     * Gets the user's permissions
     * @return SessionPermissions permissions
     */
    public SessionPermissions getPermissions() {
        return permissionData;
    }

    /**
     * Sets a user's permissions
     * @param permissionData new SessionPermissions
     */
    public void setPermissions(SessionPermissions permissionData) {
        this.permissionData = permissionData;
    }

    /**
     * Maps the user's details to a string
     * Can be accessed with Principal.getName()
     * @return String the userId
     */
    @Override
    public String toString() {
        return userId.toString();
    }

    /**
     * Erases the user's credentials
     * @apiNote THIS IS PERMANENT
     */
    @Override
    public void eraseCredentials() {
        // @TODO CHECK IMPLEMENTATION
        passwordHash = null;
    }

    /**
     * Gets all authorities authorized by this user's permissions
     * @return Collection<? extends GrantedAuthority> collection of authorities
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Turn permissionData into a collection of authorities
        // Required to use Spring's @PreAuthorize and @PostAuthorize annotations
        // Also, other ways would be more complexity and its complicated enough
        ArrayList<String> allowedPermissions = permissionData.getListOfPermissions();
        ArrayList<SimpleGrantedAuthority> grantedAuthorities = new ArrayList<>();

        // Maps all our permissions to a SimpleGrantedAuthority for Spring's convenience
        for(int i = 0; i < allowedPermissions.size(); i++) {
            grantedAuthorities.add(new SimpleGrantedAuthority(allowedPermissions.get(i)));
        }

        return grantedAuthorities;
    }

    /**
     * Gets a user's HASHED password
     * @apiNote THIS SHOULD ONLY BE CALLED FOR AUTHENTICATION PURPOSES
     * @return String HASHED password
     */
    @Override
    public @Nullable String getPassword() {
        return passwordHash;
    }

    /**
     * Gets a user's username
     * @return String username
     */
    @Override
    public String getUsername() {
        return username;
    }

    /**
     * Checks if the user's account is not expired
     * @return boolean true if not expired, false otherwise
     */
    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    /**
     * Checks if the user's account is not locked from logins
     * @return boolean true if account is not locked, false otherwise
     * I do not know how this is used by Spring Security
     */
    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    /**
     * Checks if the user's credentials are not expired
     * @return boolean true if account's credentials are not expired, false otherwise
     * @apiNote Always returns true, as system does not expire passwords
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Checks if the account is enabled
     * @return boolean true if the account is enabled, false otherwise
     * @TODO determine if we want to make bans block this way
     */
    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
