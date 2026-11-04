package com.teamwhite.thebuzz.Controllers.API.V1;

import com.teamwhite.thebuzz.Resources.PermissionNames;
import com.teamwhite.thebuzz.Services.ShowLikeService;
import com.teamwhite.thebuzz.model.BuzzUser;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/show")
public class ShowLikeController {

    private final ShowLikeService showLikeService;

    public ShowLikeController(ShowLikeService showLikeService) {
        this.showLikeService = showLikeService;
    }

    @PostMapping("/like-show")
    @PreAuthorize("hasAuthority('" + PermissionNames.CAN_LIKE_SHOWS + "')")
    public ResponseEntity<Void> likeShow(
            @RequestParam("show_id") String showId,
            Authentication userAuth) {

        if (userAuth == null || !(userAuth.getPrincipal() instanceof BuzzUser user)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (user.getUserId() == null) {
            return ResponseEntity.badRequest().build();
        }

        try {
            UUID.fromString(showId);
            showLikeService.likeShow(user.getUserId().toString(), showId);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/unlike-show")
    @PreAuthorize("hasAuthority('" + PermissionNames.CAN_LIKE_SHOWS + "')")
    public ResponseEntity<Void> unlikeShow(
            @RequestParam("show_id") String showId,
            Authentication userAuth) {

        if (userAuth == null || !(userAuth.getPrincipal() instanceof BuzzUser user)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (user.getUserId() == null) {
            return ResponseEntity.badRequest().build();
        }

        try {
            UUID.fromString(showId);

            boolean deleted = showLikeService.unlikeShow(
                    user.getUserId().toString(), showId);

            if (!deleted) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.noContent().build();

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
