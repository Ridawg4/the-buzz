package com.teamwhite.thebuzz.Services;

import com.teamwhite.thebuzz.Repositories.AccountModerationRepository;
import com.teamwhite.thebuzz.model.AccountModeration;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Service
public class AccountModerationService {

    private final AccountModerationRepository repository;

    public AccountModerationService(AccountModerationRepository repository) {
        this.repository = repository;
    }

    public List<AccountModeration> getModerationByUserId(UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }

        return repository.getModerationByUserId(userId);
    }

    public int createAccountAction(AccountModeration moderation) {
        if (moderation == null) {
            throw new IllegalArgumentException("Moderation action cannot be null");
        }

        if (moderation.getUserId() == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }

        if (moderation.getModerationDesc() == null
                || moderation.getModerationDesc().isBlank()) {
            throw new IllegalArgumentException("Moderation description is required");
        }

        if (moderation.getModerationSeverity() == null
                || moderation.getModerationSeverity().isBlank()) {
            throw new IllegalArgumentException("Moderation severity is required");
        }

        if (moderation.getModeratorAppliedAction() == null
                || moderation.getModeratorAppliedAction().isBlank()) {
            throw new IllegalArgumentException("Moderator action is required");
        }

        if (moderation.getDateApplied() == null) {
            moderation.setDateApplied(new Timestamp(System.currentTimeMillis()));
        }

        return repository.createAccountAction(moderation);
    }
}
