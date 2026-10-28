package com.teamwhite.thebuzz.Services;

import com.teamwhite.thebuzz.Repositories.CommentRepository;
import org.springframework.stereotype.Service;

@Service
public class CommentService {

    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public void createComment(
            String userId,
            int postId,
            Integer replyId,
            String commentContent) {

        if (commentContent == null
                || commentContent.isBlank()
                || commentContent.length() > 1000) {
            throw new IllegalArgumentException(
                "Comment must contain 1 to 1000 characters"
            );
        }

        int rowsInserted = commentRepository.createComment(
            userId,
            postId,
            replyId,
            commentContent
        );

        if (rowsInserted != 1) {
            throw new IllegalStateException("Comment was not created");
        }
    }

    public boolean deleteComment(int commentId, String userId) {
        return commentRepository.deleteCommentByIdAndUserId(
            commentId,
            userId
        ) > 0;
    }
}
