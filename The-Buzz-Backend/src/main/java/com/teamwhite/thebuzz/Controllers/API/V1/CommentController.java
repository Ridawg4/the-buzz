package com.teamwhite.thebuzz.Controllers.API.V1;

import com.teamwhite.thebuzz.Resources.PermissionNames;
import com.teamwhite.thebuzz.Services.CommentService;
import com.teamwhite.thebuzz.model.BuzzUser;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/comment")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/create-comment")
    @PreAuthorize("hasAuthority('" + PermissionNames.CAN_POST_COMMENTS + "')")
    public ResponseEntity<Void> createComment(
            @RequestParam("post_id") int postId,
            @RequestParam(value = "reply_id", required = false) Integer replyId,
            @RequestParam("comment_content") String commentContent,
            Authentication userAuth) {

        if (userAuth == null
                || !(userAuth.getPrincipal() instanceof BuzzUser user)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (user.getUserId() == null
                || commentContent == null
                || commentContent.isBlank()
                || commentContent.length() > 1000) {
            return ResponseEntity.badRequest().build();
        }

        commentService.createComment(
            user.getUserId().toString(),
            postId,
            replyId,
            commentContent
        );

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/delete-comment")
    @PreAuthorize("hasAuthority('" + PermissionNames.CAN_DELETE_COMMENTS + "')")
    public ResponseEntity<Void> deleteComment(
            @RequestParam("comment_id") int commentId,
            Authentication userAuth) {

        if (userAuth == null
                || !(userAuth.getPrincipal() instanceof BuzzUser user)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (user.getUserId() == null || commentId <= 0) {
            return ResponseEntity.badRequest().build();
        }

        boolean deleted = commentService.deleteComment(
            commentId,
            user.getUserId().toString()
        );

        if (!deleted) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }
}
