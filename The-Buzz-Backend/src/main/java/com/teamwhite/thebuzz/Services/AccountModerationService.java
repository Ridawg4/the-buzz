package com.teamwhite.thebuzz.Services;

import com.teamwhite.thebuzz.Repositories.AccountModerationRepository;
import com.teamwhite.thebuzz.model.AccountModeration;
import org.springframework.stereotype.Service;

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
}
