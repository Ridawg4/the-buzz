package com.teamwhite.thebuzz.Services;

import com.teamwhite.thebuzz.Repositories.CommentRepository;
import org.springframework.stereotype.Service;

@Service
public class CommentService {

    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    // @TODO THIS DOESN'T ACTUALLY WORK | MYSQL DOES NOT AUTO_INCREMENT PRIMARY KEY BY DEFAULT
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

    // Checking greater than zero will return true, even if we delete every row in the table,
    // which would not be intended behavior. It is unlikely to be an issue, but something
    // to consider
    public boolean deleteComment(int commentId, String userId) {
        return commentRepository.deleteCommentByIdAndUserId(
            commentId,
            userId
        ) > 0;
    }
}
