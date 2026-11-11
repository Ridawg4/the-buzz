package com.teamwhite.thebuzz.Services;

import com.teamwhite.thebuzz.Repositories.CommentLikeRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CommentLikeService {

    private final CommentLikeRepository commentLikeRepository;

    public CommentLikeService(CommentLikeRepository commentLikeRepository) {
        this.commentLikeRepository = commentLikeRepository;
    }

    public void likeComment(String userId, int commentId) {
        UUID.fromString(userId);

        if (commentId <= 0) {
            throw new IllegalArgumentException("Invalid comment ID");
        }

        int rowsInserted = commentLikeRepository.likeComment(userId, commentId);

        if (rowsInserted != 1) {
            throw new IllegalStateException("Comment was not liked");
        }
    }

    public boolean unlikeComment(String userId, int commentId) {
        UUID.fromString(userId);

        if (commentId <= 0) {
            throw new IllegalArgumentException("Invalid comment ID");
        }

        return commentLikeRepository.unlikeComment(userId, commentId) > 0;
    }
}
