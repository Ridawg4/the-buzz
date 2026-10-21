package com.teamwhite.thebuzz.Repositories;

import com.teamwhite.thebuzz.model.AccountModeration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class AccountModerationRepository {

    private final JdbcTemplate jdbcTemplate;

    public AccountModerationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<AccountModeration> getModerationByUserId(UUID userId) {
        String sql = """
            SELECT BIN_TO_UUID(user_id) AS user_id,
                   moderation_desc,
                   date_applied,
                   moderator_applied_action,
                   moderation_severity,
                   date_expires
            FROM development.account_moderation
            WHERE user_id = UUID_TO_BIN(?)
            ORDER BY date_applied DESC
            """;

        return jdbcTemplate.query(sql, (resultSet, rowNum) -> {
            AccountModeration moderation = new AccountModeration();
            moderation.setUserId(UUID.fromString(resultSet.getString("user_id")));
            moderation.setModerationDesc(
                resultSet.getString("moderation_desc"));
            moderation.setDateApplied(
                resultSet.getTimestamp("date_applied"));
            moderation.setModeratorAppliedAction(
                resultSet.getString("moderator_applied_action"));
            moderation.setModerationSeverity(
                resultSet.getString("moderation_severity"));
            moderation.setDateExpires(
                resultSet.getTimestamp("date_expires"));

            return moderation;
        }, userId.toString());
    }
}
