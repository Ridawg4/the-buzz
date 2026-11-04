package com.teamwhite.thebuzz.Repositories;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ShowLikeRepository {

    private final JdbcTemplate jdbcTemplate;

    public ShowLikeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int likeShow(String userId, String showId) {
        String sql = """
            INSERT INTO development.show_likes
                (user_id, show_id, created_at)
            VALUES
                (UUID_TO_BIN(?), UUID_TO_BIN(?), CURRENT_TIMESTAMP)
            """;

        return jdbcTemplate.update(sql, userId, showId);
    }

    public int unlikeShow(String userId, String showId) {
        String sql = """
            DELETE FROM development.show_likes
            WHERE user_id = UUID_TO_BIN(?)
              AND show_id = UUID_TO_BIN(?)
            """;

        return jdbcTemplate.update(sql, userId, showId);
    }
}
