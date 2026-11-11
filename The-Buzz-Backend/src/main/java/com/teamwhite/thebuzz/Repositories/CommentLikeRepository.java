package com.teamwhite.thebuzz.Repositories;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class CommentLikeRepository {

    private final JdbcTemplate jdbcTemplate;

    public CommentLikeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int likeComment(String userId, int commentId) {
        String sql = """
            INSERT INTO development.comment_likes
                (user_id, comment_id, created_at)
            VALUES
                (UUID_TO_BIN(?), ?, CURRENT_TIMESTAMP)
            """;

        return jdbcTemplate.update(sql, userId, commentId);
    }

    public int unlikeComment(String userId, int commentId) {
        String sql = """
            DELETE FROM development.comment_likes
            WHERE user_id = UUID_TO_BIN(?)
              AND comment_id = ?
            """;

        return jdbcTemplate.update(sql, userId, commentId);
    }
}
