package com.teamwhite.thebuzz.Controllers.API.V1;

import com.teamwhite.thebuzz.Services.AccountModerationService;
import com.teamwhite.thebuzz.model.AccountModeration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/{userId}")
    public ResponseEntity<List<AccountModeration>> getModerationByUserId(
            @PathVariable UUID userId) {
        List<AccountModeration> records =
                moderationService.getModerationByUserId(userId);

        return ResponseEntity.ok(records);
    }
}
