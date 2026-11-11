


package com.teamwhite.thebuzz.Controllers.API.V1;

import com.teamwhite.thebuzz.Services.ShowService;
import com.teamwhite.thebuzz.model.BuzzUser;
import com.teamwhite.thebuzz.model.Show;

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
     * Creates a new radio show listing.
     */
    @PostMapping
    public ResponseEntity<String> createShow(
            Authentication authentication,
            @RequestBody Show show) {

        // Verify that the user is logged in
        if (authentication == null ||
                !authentication.isAuthenticated() ||
                !(authentication.getPrincipal() instanceof BuzzUser)) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("You must be logged in.");
        }

        BuzzUser user = (BuzzUser) authentication.getPrincipal();

        // Check permission to create show listings
        boolean canCreate = user.getAuthorities().stream()
                .anyMatch(authority ->
                        authority.getAuthority()
                                .equals("CAN_CREATE_SHOW_LISTING"));

        if (!canCreate) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("You do not have permission to create show listings.");
        }

        try {
            // Create the show using the service
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

        // Verify that the user is logged in
        if (authentication == null ||
                !authentication.isAuthenticated() ||
                !(authentication.getPrincipal() instanceof BuzzUser)) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("You must be logged in.");
        }

        BuzzUser user = (BuzzUser) authentication.getPrincipal();

        // Check permission to modify show listings
        boolean canModify = user.getAuthorities().stream()
                .anyMatch(authority ->
                        authority.getAuthority()
                                .equals("CAN_MODIFY_SHOW_LISTING"));

        if (!canModify) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("You do not have permission to modify show listings.");
        }

        try {
            // Update the show using the service
            boolean updated = showService.updateShow(
                    showId, updatedShow);

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

        // Verify that the user is logged in
        if (authentication == null ||
                !authentication.isAuthenticated() ||
                !(authentication.getPrincipal() instanceof BuzzUser)) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("You must be logged in.");
        }

        BuzzUser user = (BuzzUser) authentication.getPrincipal();

        // Check permission to delete show listings
        boolean canDelete = user.getAuthorities().stream()
                .anyMatch(authority ->
                        authority.getAuthority()
                                .equals("CAN_DELETE_SHOW_LISTING"));

        if (!canDelete) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("You do not have permission to delete show listings.");
        }

        try {
            // Delete the show using the service
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
