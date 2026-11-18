package com.teamwhite.thebuzz.Repositories;

import com.teamwhite.thebuzz.model.AccountModeration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
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
            SELECT action_id,
                   BIN_TO_UUID(user_id) AS user_id,
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

            moderation.setActionId(resultSet.getInt("action_id"));
            moderation.setUserId(UUID.fromString(resultSet.getString("user_id")));
            moderation.setModerationDesc(resultSet.getString("moderation_desc"));
            moderation.setDateApplied(resultSet.getTimestamp("date_applied"));
            moderation.setModeratorAppliedAction(
                resultSet.getString("moderator_applied_action"));
            moderation.setModerationSeverity(
                resultSet.getString("moderation_severity"));
            moderation.setDateExpires(resultSet.getTimestamp("date_expires"));

            return moderation;
        }, userId.toString());
    }

    public int createAccountAction(AccountModeration moderation) {
        String sql = """
            INSERT INTO development.account_moderation
            (user_id, moderation_desc, date_applied,
             moderator_applied_action, moderation_severity, date_expires)
            VALUES (UUID_TO_BIN(?), ?, ?, ?, ?, ?)
            """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS);

            statement.setString(1, moderation.getUserId().toString());
            statement.setString(2, moderation.getModerationDesc());
            statement.setTimestamp(3, moderation.getDateApplied());
            statement.setString(4, moderation.getModeratorAppliedAction());
            statement.setString(5, moderation.getModerationSeverity());
            statement.setTimestamp(6, moderation.getDateExpires());

            return statement;
        }, keyHolder);

        return keyHolder.getKey().intValue();
    }
}
