package com.teamwhite.thebuzz.Controllers.API.V1;

import com.teamwhite.thebuzz.Resources.PermissionNames;
import com.teamwhite.thebuzz.Resources.PermissionValues;
import com.teamwhite.thebuzz.Services.AccountModerationService;
import com.teamwhite.thebuzz.model.AccountModeration;
import com.teamwhite.thebuzz.model.BuzzUser;
import com.teamwhite.thebuzz.model.SessionPermissions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/moderation")
public class AccountModerationController {

    private final AccountModerationService moderationService;

    public AccountModerationController(
            AccountModerationService moderationService) {
        this.moderationService = moderationService;
    }

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

    @GetMapping("/{userId}")
    public ResponseEntity<List<AccountModeration>> getModerationByUserId(
            @PathVariable String userId) {

        List<AccountModeration> records =
                moderationService.getModerationByUserId(
                        UUID.fromString(userId));

        return ResponseEntity.ok(records);
    }

    @PostMapping
    public ResponseEntity<String> createAccountAction(
            Authentication authentication,
            @RequestBody AccountModeration moderation) {

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
                PermissionNames.CAN_CREATE_ACCOUNT_ACTION_ENTRY
        );

        if (!canCreate) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("You do not have permission to create account actions.");
        }

        try {
            int actionId =
                    moderationService.createAccountAction(moderation);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body("Account action created successfully! Action ID: "
                            + actionId);

        } catch (IllegalArgumentException exception) {
            return ResponseEntity
                    .badRequest()
                    .body(exception.getMessage());
        }
    }
}
