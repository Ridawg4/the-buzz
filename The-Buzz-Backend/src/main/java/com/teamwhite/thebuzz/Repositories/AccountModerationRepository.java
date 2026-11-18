package com.teamwhite.thebuzz.Repositories;

import com.teamwhite.thebuzz.model.AccountModeration;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public interface AccountModerationRepository extends CrudRepository<AccountModeration, UUID> {

    @Query(value="SELECT moderation_id, BIN_TO_UUID(user_id) AS user_id, moderation_desc, date_applied, BIN_TO_UUID(moderator_applied_action), moderation_severity, date_expires, is_acknowledged FROM development.account_moderation WHERE user_id = UUID_TO_BIN(?1) ORDER BY date_applied DESC", nativeQuery = true)
    public List<Object[]> getModerationByUserId(String userId);

    @Query(value="SELECT moderation_id, BIN_TO_UUID(user_id) AS user_id, moderation_desc, date_applied, BIN_TO_UUID(moderator_applied_action), moderation_severity, date_expires, is_acknowledged FROM development.account_moderation WHERE moderation_id = ?1", nativeQuery = true)
    public List<Object[]> getModerationById(int id);

    @Query(value="SELECT moderation_id, BIN_TO_UUID(user_id) AS user_id, moderation_desc, date_applied, BIN_TO_UUID(moderator_applied_action), moderation_severity, date_expires, is_acknowledged FROM development.account_moderation WHERE user_id = UUID_TO_BIN(?1) AND is_acknowledged = FALSE ORDER BY date_applied DESC", nativeQuery = true)
    public List<Object[]> getActionsNotAcknowledgedByUserId(String userId);

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO account_moderation (moderation_id, user_id, moderation_desc, date_applied, moderator_applied_action, moderation_severity, date_expires, is_acknowledged) VALUES (0, UUID_TO_BIN(?1), ?2, ?3, UUID_TO_BIN(?4), ?5, ?6, ?7)", nativeQuery = true)
    public void createAccountModeration(String actedUser, String moderationDesc, Timestamp dateApplied, String moderatorWhoActed, String moderationSeverity, Timestamp dateExpires, boolean isAcknowledged);

    @Transactional
    @Modifying
    @Query(value = "UPDATE account_moderation SET is_acknowledged = ?2 WHERE moderation_id = ?1", nativeQuery = true)
    public void acknowledgeAccountModeration(int reportId, boolean isAcknowledged);

}
