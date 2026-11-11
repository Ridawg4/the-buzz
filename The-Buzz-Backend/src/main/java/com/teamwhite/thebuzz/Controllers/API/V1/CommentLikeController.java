package com.teamwhite.thebuzz.Controllers.API.V1;

import com.teamwhite.thebuzz.Resources.PermissionNames;
import com.teamwhite.thebuzz.Services.CommentLikeService;
import com.teamwhite.thebuzz.model.BuzzUser;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/comment")
public class CommentLikeController {

    private final CommentLikeService commentLikeService;

    public CommentLikeController(CommentLikeService commentLikeService) {
        this.commentLikeService = commentLikeService;
    }

    @PostMapping("/like-comment")
    @PreAuthorize("hasAuthority('" + PermissionNames.CAN_LIKE_COMMENTS + "')")
    public ResponseEntity<Void> likeComment(
            @RequestParam("comment_id") int commentId,
            Authentication userAuth) {

        if (userAuth == null
                || !(userAuth.getPrincipal() instanceof BuzzUser user)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (user.getUserId() == null || commentId <= 0) {
            return ResponseEntity.badRequest().build();
        }

        commentLikeService.likeComment(
                user.getUserId().toString(), commentId);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/unlike-comment")
    @PreAuthorize("hasAuthority('" + PermissionNames.CAN_LIKE_COMMENTS + "')")
    public ResponseEntity<Void> unlikeComment(
            @RequestParam("comment_id") int commentId,
            Authentication userAuth) {

        if (userAuth == null
                || !(userAuth.getPrincipal() instanceof BuzzUser user)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (user.getUserId() == null || commentId <= 0) {
            return ResponseEntity.badRequest().build();
        }

        boolean deleted = commentLikeService.unlikeComment(
                user.getUserId().toString(), commentId);

        if (!deleted) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }
}
