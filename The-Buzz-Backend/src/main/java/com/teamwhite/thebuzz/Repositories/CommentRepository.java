package com.teamwhite.thebuzz.Repositories;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class CommentRepository {

    private final JdbcTemplate jdbcTemplate;

    public CommentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int createComment(
            String userId,
            int postId,
            Integer replyId,
            String commentContent) {

        String sql = """
            INSERT INTO development.comments
                (user_id, post_id, reply_id, comment_content,
                 last_updated, created_at)
            VALUES
                (UUID_TO_BIN(?), ?, ?, ?, NULL, CURRENT_TIMESTAMP)
            """;

        return jdbcTemplate.update(
            sql,
            userId,
            postId,
            replyId,
            commentContent
        );
    }

    public int deleteCommentByIdAndUserId(int commentId, String userId) {

        String sql = """
            DELETE FROM development.comments
            WHERE comment_id = ?
              AND user_id = UUID_TO_BIN(?)
            """;

        return jdbcTemplate.update(sql, commentId, userId);
    }
}
