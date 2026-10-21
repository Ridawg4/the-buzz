package com.teamwhite.thebuzz.Controllers.API.V1;

import com.teamwhite.thebuzz.Services.DjService;
import com.teamwhite.thebuzz.Records.UpdateDJProfileRequest;
import com.teamwhite.thebuzz.model.BuzzUser;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controller for handling DJ profile requests.
 */
@RestController
@RequestMapping("/api/v1/dj")
public class DjController {

    private final DjService djService;

    // Connects the controller to the service
    public DjController(DjService djService) {
        this.djService = djService;
    }

    /**
     * Test endpoint.
     */
    @GetMapping("/test")
    public String testDJEndpoint() {
        return "DJ controller is working!";
    }

    /**
     * Allows an authenticated DJ to edit their own profile.
     */
    @PutMapping("/profile")
    public ResponseEntity<String> updateProfile(
            Authentication authentication,
            @RequestBody UpdateDJProfileRequest request) {

        // Verify the request has an authenticated BuzzUser
        if (authentication == null ||
                !authentication.isAuthenticated() ||
                !(authentication.getPrincipal() instanceof BuzzUser)) {

            // Endpoint is secured by default, user would be redirected if authentication failed
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("You must be logged in.");
        }

        // Get the authenticated user
        BuzzUser user = (BuzzUser) authentication.getPrincipal();

        // Check permission to edit a DJ profile
        boolean canEdit = user.getAuthorities().stream()
                .anyMatch(authority ->
                        authority.getAuthority()
                                .equals("CAN_MODIFY_DJ_PROFILE"));

        if (!canEdit) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("You do not have permission to edit a DJ profile.");
        }

        // Convert the authenticated user's ID into a UUID
        // This could be done within the method call below
        UUID userId = UUID.fromString(user.getUserId());

        try {
            // Call the service to update this user's DJ profile
            boolean updated = djService.updateDJProfile(
                    userId,
                    request.djName(),
                    request.genre(),
                    request.djPfpLocation()
            );

            if (!updated) {
                // This could be false and not because of a missing profile, (ex. SQL query failed)
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body("DJ profile not found.");
            }

            return ResponseEntity.ok(
                    "DJ profile updated successfully!"
            );

        } catch (IllegalArgumentException exception) {
            return ResponseEntity
                    .badRequest()
                    .body(exception.getMessage());
        }
    }
}