package com.teamwhite.thebuzz.Repositories;

import com.teamwhite.thebuzz.model.BuzzUser;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

/**
 * Repository for handling all user SQL queries
 */
@Repository
public interface UserRepository extends CrudRepository<BuzzUser, UUID> {

    /**
     * Gets all records in development.users table with a given email
     *
     * List<Object[]> is returned over ResultSet, as Spring Boot does not like ResultSet for some reason
     * @param email email used for finding a specific table entry
     * @return List<Object[]> List is the row entries, Object[] is the individual column values
     */
    @Query(value = "SELECT BIN_TO_UUID(user_id), username, password_hash, oauth_key, join_date, user_desc, full_name, user_pfp_location, email, favorites, volume_settings, notification_settings, permission_data FROM development.users WHERE email = ?1", nativeQuery = true)
    List<Object[]> getUserRecordByEmail(String email);


    /**
     * Gets all records in development.users table
     * @return List<Object[]> List is the row entries, Object[] is the individual column values
     */
    @Query(value = "SELECT * FROM development.users", nativeQuery = true)
    List<Object[]> getAllUsers();

    /**
     * Retrieves any entry containing the email provided
     * @param email email used for finding a specific table entry
     * @return List<Object[]> List is the row entries, Object[] is the individual column values
     */
    @Query(value="SELECT email FROM development.users WHERE email = ?1", nativeQuery = true)
    List<Object[]> doesUserExist(String email);

    /**
     * Adds an entry to the development.users table with the provided parameters
     *
     * @param userId String version of the UUID
     * @param username String
     * @param passwordHash String | PASSWORD MUST BE HASHED BEFORE ENTERING DATABASE
     * @param oauthKey String
     * @param joinTime Timestamp
     * @param userDesc String
     * @param fullName String
     * @param userPfpLocation String
     * @param email String
     * @param favoritesList String representation of the JsonObject
     * @param volumeSettings String representation of the JsonObject
     * @param notificationSettings String representation of the JsonObject
     * @param permissions String representation of the SessionPermissions object
     */
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO development.users VALUES(UUID_TO_BIN(?1), ?2, ?3, ?4, ?5, ?6, ?7, ?8, ?9, ?10, ?11, ?12, ?13)", nativeQuery = true)
    void createUser(String userId, String username, String passwordHash, String oauthKey,
                    Timestamp joinTime, String userDesc, String fullName, String userPfpLocation,
                    String email, String favoritesList, String volumeSettings,
                    String notificationSettings, String permissions);

    /**
     * Updates a users username based off their old email
     * @param email String email of the record to modify
     * @param username String new username
     */
    @Modifying
    @Transactional
    @Query(value="UPDATE development.users SET username = ?2 WHERE email = ?1", nativeQuery = true)
    void updateUserUsernameByOldEmail(String email, String username);

    /**
     * Updates a users email based off their old email
     * @param oldEmail String email of the record to modify
     * @param newEmail String new email
     */
    @Modifying
    @Transactional
    @Query(value="UPDATE development.users SET email = ?2 WHERE email = ?1", nativeQuery = true)
    void updateUserEmailByOldEmail(String oldEmail, String newEmail);

    /**
     * Updates a users passwordHash based off their email
     * @param email String email of the record to modify
     * @param passwordHash String new passwordHash
     */
    @Modifying
    @Transactional
    @Query(value="UPDATE development.users SET password_hash = ?2 WHERE email = ?1", nativeQuery = true)
    void updateUserPasswordHashByEmail(String email, String passwordHash);

    /**
     * Updates the OAuthKey with a new value based off the email
     * @param email String email of the record to modify
     * @param oauthKey String new OAuthKey
     */
    @Modifying
    @Transactional
    @Query(value="UPDATE development.users SET oauth_key = ?2 WHERE email = ?1", nativeQuery = true)
    void updateUserOauthKeyByEmail(String email, String oauthKey);

    /**
     * Updates the userDesc with a new value based off the email
     * @param email String email of the record to modify
     * @param userDesc String new description
     */
    @Modifying
    @Transactional
    @Query(value="UPDATE development.users SET user_desc = ?2 WHERE email = ?1", nativeQuery = true)
    void updateUserDescByEmail(String email, String userDesc);

    /**
     * Updates the full name with a new value based off the email
     * @param email String email of the record to modify
     * @param fullName String new fullName
     */
    @Modifying
    @Transactional
    @Query(value="UPDATE development.users SET full_name = ?2 WHERE email = ?1", nativeQuery = true)
    void updateUserFullNameByEmail(String email, String fullName);

    /**
     * Updates the userPfpLocation based off the email
     * @param email String email of the record to modify
     * @param userPfpLocation String new userPfpLocation
     */
    @Modifying
    @Transactional
    @Query(value="UPDATE development.users SET user_pfp_location = ?2 WHERE email = ?1", nativeQuery = true)
    void updateUserPfpLocationByEmail(String email, String userPfpLocation);

    /**
     * Updates the users favorites list based off the email
     * @param email String email of the record to modify
     * @param favoritesList String new favoritesList
     */
    @Modifying
    @Transactional
    @Query(value="UPDATE development.users SET favorites = ?2 WHERE email = ?1", nativeQuery = true)
    void updateUserFavoritesListByEmail(String email, String favoritesList);

    /**
     * Updates the users volume settings based off the email
     * @param email String email of the record to modify
     * @param volumeSettings String new volumeSettings
     */
    @Modifying
    @Transactional
    @Query(value="UPDATE development.users SET volume_settings = ?2 WHERE email = ?1", nativeQuery = true)
    void updateUserVolumeSettingsByEmail(String email, String volumeSettings);

    /**
     * Updates the users notification settings based off the email
     * @param email String email of the record to modify
     * @param notificationSettings String new notificationSettings
     */
    @Modifying
    @Transactional
    @Query(value="UPDATE development.users SET notification_settings = ?2 WHERE email = ?1", nativeQuery = true)
    void updateUserNotificationsSettingsByEmail(String email, String notificationSettings);

    /**
     * Updates the users permissions based off the email
     * @param email String email of the record to modify
     * @param permissions String new permissions
     */
    @Modifying
    @Transactional
    @Query(value="UPDATE development.users SET permission_data = ?2 WHERE email = ?1", nativeQuery = true)
    void updateUserPermissionsByEmail(String email, String permissions);

    /**
     * Removes permanently a user based off an email
     * @param email String email of the record to remove
     */
    @Modifying
    @Transactional
    @Query(value="DELETE FROM development.users WHERE email = ?1", nativeQuery = true)
    void removeUserByEmail(String email);
}
