package com.teamwhite.thebuzz.Repositories;

import com.teamwhite.thebuzz.model.BuzzUser;
import jakarta.json.JsonObject;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Repository
public interface UserRepository extends CrudRepository<BuzzUser, UUID> {

    @Query(value = "SELECT BIN_TO_UUID(user_id), username, password_hash, oauth_key, join_date, user_desc, full_name, user_pfp_location, email, favorites, volume_settings, notification_settings, permission_data FROM development.users WHERE email = ?1", nativeQuery = true)
    List<Object[]> getUserRecordByEmail(String email);

    @Query(value = "SELECT * FROM development.users", nativeQuery = true)
    ResultSet getAllUsers();

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO development.users VALUES(UUID_TO_BIN(?1), ?2, ?3, ?4, ?5, ?6, ?7, ?8, ?9, ?10, ?11, ?12, ?13)", nativeQuery = true)
    void createUser(String userId, String username, String passwordHash, String oauthKey,
                    Timestamp joinTime, String userDesc, String fullName, String userPfpLocation,
                    String email, String favoritesList, String volumeSettings,
                    String notificationSettings, String permissions);

    @Modifying
    @Transactional
    @Query(value="UPDATE development.users SET oauth_key = ?2 WHERE email = ?1", nativeQuery = true)
    void updateUserOauthKeyByEmail(String email, String oauthKey);

    @Modifying
    @Transactional
    @Query(value="UPDATE development.users SET user_desc = ?2 WHERE email = ?1", nativeQuery = true)
    void updateUserDescByEmail(String email, String userDesc);

    @Modifying
    @Transactional
    @Query(value="UPDATE development.users SET full_name = ?2 WHERE email = ?1", nativeQuery = true)
    void updateUserFullNameByEmail(String email, String fullName);

    @Modifying
    @Transactional
    @Query(value="UPDATE development.users SET user_pfp_location = ?2 WHERE email = ?1", nativeQuery = true)
    void updateUserPfpLocationByEmail(String email, String userPfpLocation);

    @Modifying
    @Transactional
    @Query(value="UPDATE development.users SET favorites = ?2 WHERE email = ?1", nativeQuery = true)
    void updateUserFavoritesListByEmail(String email, JsonObject favoritesList);

    @Modifying
    @Transactional
    @Query(value="UPDATE development.users SET volume_settings = ?2 WHERE email = ?1", nativeQuery = true)
    void updateUserVolumeSettingsByEmail(String email, JsonObject volumeSettings);

    @Modifying
    @Transactional
    @Query(value="UPDATE development.users SET notification_settings = ?2 WHERE email = ?1", nativeQuery = true)
    void updateUserNotificationsSettingsByEmail(String email, JsonObject notificationSettings);

    @Modifying
    @Transactional
    @Query(value="UPDATE development.users SET permission_data = ?2 WHERE email = ?1", nativeQuery = true)
    void updateUserPermissionsByEmail(String email, JsonObject permissions);

    @Modifying
    @Transactional
    @Query(value="DELETE FROM development.users WHERE email = ?1", nativeQuery = true)
    void removeUserByEmail(String email);

    @Query(value="SELECT email FROM development.users WHERE email = ?1", nativeQuery = true)
    ResultSet doesUserExist(String email);

    @Modifying
    @Transactional
    @Query(value="UPDATE development.users SET password_hash = ?2 WHERE email = ?1", nativeQuery = true)
    void updateUserPasswordHashByEmail(String email, String passwordHash);
}
