package com.teamwhite.thebuzz.Controllers.API.V1;

import com.teamwhite.thebuzz.Services.ShowService;
import com.teamwhite.thebuzz.model.BuzzUser;
import com.teamwhite.thebuzz.model.Show;
import com.teamwhite.thebuzz.model.SessionPermissions;
import com.teamwhite.thebuzz.Resources.PermissionValues;
import com.teamwhite.thebuzz.Resources.PermissionNames;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.List;

/**
 * Controller for creating, modifying, deleting,
 * and viewing radio shows and showtimes.
 */
@RestController
@RequestMapping("/api/v1/shows")
public class ShowController {

    private final ShowService showService;

    // Constructor connects controller to service
    public ShowController(ShowService showService) {
        this.showService = showService;
    }

    /**
     * Test endpoint.
     */
    @GetMapping("/test")
    public String testShowEndpoint() {
        return "Show controller is working!";
    }

    /**
     * Checks if a user has an allowed permission.
     */
    private boolean hasPermission(
            BuzzUser user,
            String permissionName) {

        return user.getAuthorities().stream()
                .filter(SessionPermissions.class::isInstance)
                .map(SessionPermissions.class::cast)
                .anyMatch(permissions ->
                        PermissionValues.ALLOWED.equals(
                                permissions.getPermission(permissionName)
                        ));
    }

    /**
     * Retrieves all radio shows.
     */
    @GetMapping
    public ResponseEntity<List<Show>> getAllShows() {

        List<Show> shows = showService.getAllShows();

        return ResponseEntity.ok(shows);
    }

    /**
     * Retrieves visible shows ordered by
     * their scheduled date and start time.
     */
    @GetMapping("/schedule")
    public ResponseEntity<List<Show>> getShowSchedule() {

        List<Show> schedule = showService.getShowSchedule();

        return ResponseEntity.ok(schedule);
    }

    /**
     * Creates a new radio show listing.
     */
    @PostMapping
    public ResponseEntity<String> createShow(
            Authentication authentication,
            @RequestBody Show show) {

        if (authentication == null ||
                !authentication.isAuthenticated() ||
                !(authentication.getPrincipal() instanceof BuzzUser)) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("You must be logged in.");
        }

        BuzzUser user = (BuzzUser) authentication.getPrincipal();

        boolean canCreate = hasPermission(
                user,
                "CAN_CREATE_SHOW_LISTING"
        );

        if (!canCreate) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("You do not have permission to create show listings.");
        }

        try {
            boolean created = showService.createShow(show);

            if (!created) {
                return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Failed to create show.");
            }

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body("Show created successfully!");

        } catch (IllegalArgumentException exception) {
            return ResponseEntity
                    .badRequest()
                    .body(exception.getMessage());
        }
    }

    /**
     * Modifies an existing radio show listing.
     */
    @PutMapping("/{showId}")
    public ResponseEntity<String> updateShow(
            Authentication authentication,
            @PathVariable UUID showId,
            @RequestBody Show updatedShow) {

        if (authentication == null ||
                !authentication.isAuthenticated() ||
                !(authentication.getPrincipal() instanceof BuzzUser)) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("You must be logged in.");
        }

        BuzzUser user = (BuzzUser) authentication.getPrincipal();

        boolean canModify = hasPermission(
                user,
                "CAN_MODIFY_SHOW_LISTING"
        );

        if (!canModify) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("You do not have permission to modify show listings.");
        }

        try {
            boolean updated = showService.updateShow(
                    showId,
                    updatedShow
            );

            if (!updated) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body("Show not found.");
            }

            return ResponseEntity.ok(
                    "Show updated successfully!");

        } catch (IllegalArgumentException exception) {
            return ResponseEntity
                    .badRequest()
                    .body(exception.getMessage());
        }
    }

    /**
     * Hides a show from the public schedule.
     * The show remains stored in the database.
     */
    @PutMapping("/{showId}/hide")
    public ResponseEntity<String> hideShow(
            Authentication authentication,
            @PathVariable UUID showId) {

        if (authentication == null ||
                !authentication.isAuthenticated() ||
                !(authentication.getPrincipal() instanceof BuzzUser)) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("You must be logged in.");
        }

        BuzzUser user = (BuzzUser) authentication.getPrincipal();

        boolean canChangeVisibility = hasPermission(
                user,
                PermissionNames.CAN_MODIFY_SHOW_SCHEDULE_VISIBILITY
        );

        if (!canChangeVisibility) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("You do not have permission to change show visibility.");
        }

        try {
            boolean hidden = showService.hideShow(showId);

            if (!hidden) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body("Show not found.");
            }

            return ResponseEntity.ok(
                    "Show hidden from the public schedule successfully!");

        } catch (IllegalArgumentException exception) {
            return ResponseEntity
                    .badRequest()
                    .body(exception.getMessage());
        }
    }

    /**
     * Makes a hidden show visible on the public schedule again.
     */
    @PutMapping("/{showId}/show")
    public ResponseEntity<String> showShow(
            Authentication authentication,
            @PathVariable UUID showId) {

        if (authentication == null ||
                !authentication.isAuthenticated() ||
                !(authentication.getPrincipal() instanceof BuzzUser)) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("You must be logged in.");
        }

        BuzzUser user = (BuzzUser) authentication.getPrincipal();

        boolean canChangeVisibility = hasPermission(
                user,
                PermissionNames.CAN_MODIFY_SHOW_SCHEDULE_VISIBILITY
        );

        if (!canChangeVisibility) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("You do not have permission to change show visibility.");
        }

        try {
            boolean visible = showService.showShow(showId);

            if (!visible) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body("Show not found.");
            }

            return ResponseEntity.ok(
                    "Show is visible on the public schedule again!");

        } catch (IllegalArgumentException exception) {
            return ResponseEntity
                    .badRequest()
                    .body(exception.getMessage());
        }
    }

    /**
     * Deletes an existing radio show listing.
     */
    @DeleteMapping("/{showId}")
    public ResponseEntity<String> deleteShow(
            Authentication authentication,
            @PathVariable UUID showId) {

        if (authentication == null ||
                !authentication.isAuthenticated() ||
                !(authentication.getPrincipal() instanceof BuzzUser)) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("You must be logged in.");
        }

        BuzzUser user = (BuzzUser) authentication.getPrincipal();

        boolean canDelete = hasPermission(
                user,
                "CAN_DELETE_SHOW_LISTING"
        );

        if (!canDelete) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("You do not have permission to delete show listings.");
        }

        try {
            boolean deleted = showService.deleteShow(showId);

            if (!deleted) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body("Show not found.");
            }

            return ResponseEntity.ok(
                    "Show deleted successfully!");

        } catch (IllegalArgumentException exception) {
            return ResponseEntity
                    .badRequest()
                    .body(exception.getMessage());
        }
    }
}
