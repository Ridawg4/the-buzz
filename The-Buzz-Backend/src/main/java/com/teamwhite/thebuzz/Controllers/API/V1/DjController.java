
package com.teamwhite.thebuzz.Controllers.API.V1;

import com.teamwhite.thebuzz.Services.DjService;
import com.teamwhite.thebuzz.Records.UpdateDJProfileRequest;
import com.teamwhite.thebuzz.model.BuzzUser;
import com.teamwhite.thebuzz.model.DJ;
import com.teamwhite.thebuzz.model.SessionPermissions;
import com.teamwhite.thebuzz.Resources.PermissionValues;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.List;
import java.util.Optional;

/**
 * Controller for viewing and updating DJ profiles.
 */
@RestController
@RequestMapping("/api/v1/dj")
public class DjController {

    private final DjService djService;

    // Connects controller to service
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
     * Checks whether a user has an allowed permission.
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
     * Retrieves all DJ profiles.
     * Only public DJ information is returned.
     */
    @GetMapping
    public ResponseEntity<List<DJProfileResponse>> getAllDJProfiles() {

        List<DJProfileResponse> djs = djService.getAllDJProfiles()
                .stream()
                .map(this::toPublicProfile)
                .toList();

        return ResponseEntity.ok(djs);
    }

    /**
     * Retrieves a specific DJ profile by ID.
     */
    @GetMapping("/{djId}")
    public ResponseEntity<DJProfileResponse> getDJProfile(
            @PathVariable UUID djId) {

        Optional<DJ> dj = djService.getDJProfileById(djId);

        return dj
                .map(this::toPublicProfile)
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.notFound().build());
    }

    /**
     * Converts a DJ entity into a public response.
     * Does not expose the linked user ID.
     */
    private DJProfileResponse toPublicProfile(DJ dj) {

        return new DJProfileResponse(
                dj.getDjId(),
                dj.getDjName(),
                dj.getGenre(),
                dj.getDjPfpLocation()
        );
    }

    /**
     * Public information returned by DJ endpoints.
     */
    public record DJProfileResponse(
            UUID djId,
            String djName,
            String genre,
            String djPfpLocation
    ) {}

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

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("You must be logged in.");
        }

        // Get authenticated user
        BuzzUser user = (BuzzUser) authentication.getPrincipal();

        // Check permission to edit a DJ profile
        boolean canEdit = hasPermission(
                user,
                "CAN_MODIFY_DJ_PROFILE"
        );

        if (!canEdit) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("You do not have permission to edit a DJ profile.");
        }

        try {
            // Convert authenticated user's ID to UUID
            UUID userId = UUID.fromString(user.getUserId());

            // Update this user's DJ profile
            boolean updated = djService.updateDJProfile(
                    userId,
                    request.djName(),
                    request.genre(),
                    request.djPfpLocation()
            );

            if (!updated) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body("DJ profile not found.");
            }

            return ResponseEntity.ok(
                    "DJ profile updated successfully!");

        } catch (IllegalArgumentException exception) {

            return ResponseEntity
                    .badRequest()
                    .body(exception.getMessage());
        }
    }
}
