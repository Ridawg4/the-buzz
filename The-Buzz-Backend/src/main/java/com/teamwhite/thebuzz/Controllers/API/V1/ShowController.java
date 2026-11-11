
package com.teamwhite.thebuzz.Controllers.API.V1;

import com.teamwhite.thebuzz.Services.ShowService;
import com.teamwhite.thebuzz.model.BuzzUser;
import com.teamwhite.thebuzz.model.Show;
import com.teamwhite.thebuzz.model.SessionPermissions;
import com.teamwhite.thebuzz.Resources.PermissionValues;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controller for creating, modifying, and deleting
 * radio show listings.
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
     * Creates a new radio show listing.
     */
    @PostMapping
    public ResponseEntity<String> createShow(
            Authentication authentication,
            @RequestBody Show show) {

        // Check if user is authenticated
        if (authentication == null ||
                !authentication.isAuthenticated() ||
                !(authentication.getPrincipal() instanceof BuzzUser)) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("You must be logged in.");
        }

        // Get authenticated user
        BuzzUser user = (BuzzUser) authentication.getPrincipal();

        // Check permission to create show listings
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
            // Create show using service
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

        // Check if user is authenticated
        if (authentication == null ||
                !authentication.isAuthenticated() ||
                !(authentication.getPrincipal() instanceof BuzzUser)) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("You must be logged in.");
        }

        // Get authenticated user
        BuzzUser user = (BuzzUser) authentication.getPrincipal();

        // Check permission to modify show listings
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
            // Update show using service
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
     * Deletes an existing radio show listing.
     */
    @DeleteMapping("/{showId}")
    public ResponseEntity<String> deleteShow(
            Authentication authentication,
            @PathVariable UUID showId) {

        // Check if user is authenticated
        if (authentication == null ||
                !authentication.isAuthenticated() ||
                !(authentication.getPrincipal() instanceof BuzzUser)) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("You must be logged in.");
        }

        // Get authenticated user
        BuzzUser user = (BuzzUser) authentication.getPrincipal();

        // Check permission to delete show listings
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
            // Delete show using service
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
