package com.teamwhite.thebuzz.Services;

import com.teamwhite.thebuzz.Contexts.BuzzSessionService;
import com.teamwhite.thebuzz.Repositories.AccountModerationRepository;
import com.teamwhite.thebuzz.model.AccountModeration;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AccountModerationService {

    private final AccountModerationRepository repository;
    private final BuzzSessionService sessionService;

    public AccountModerationService(AccountModerationRepository repository, BuzzSessionService sessionService) {
        this.repository = repository;
        this.sessionService = sessionService;
    }

    public AccountModeration[] getModerationByUserId(String userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        List<Object[]> entries = repository.getModerationByUserId(userId);

        if(entries.isEmpty()) {
            return null;
        }

        AccountModeration[] actions = new AccountModeration[entries.size()];

        for(int i = 0; i < entries.size(); i++) {
            Object[] currObject = entries.get(i);

            actions[i] = new AccountModeration((int) currObject[0], (String) currObject[1], (String) currObject[2],
                    (LocalDateTime) currObject[3], (String) currObject[4], (String) currObject[5], (LocalDateTime) currObject[6], (boolean) currObject[7]);
        }

        return actions;
    }

    public AccountModeration getModerationById(int id) {
        if (id == 0) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        List<Object[]> entries = repository.getModerationById(id);

        if(entries.isEmpty()) {
            throw new IllegalArgumentException("Entry does not exist at this Id: " + id);
        }

        if(entries.size() > 1) {
            throw new IllegalArgumentException("More than one entry exists at this Id: " + id);
        }

        Object[] currObject = entries.get(0);

        return new AccountModeration((int) currObject[0], (String) currObject[1], (String) currObject[2],
                    (LocalDateTime) currObject[3], (String) currObject[4], (String) currObject[5], (LocalDateTime) currObject[6], (boolean) currObject[7]);
    }

    public AccountModeration[] getUnacknowledgedActionsByUserId(String userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        List<Object[]> entries = repository.getActionsNotAcknowledgedByUserId(userId);

        if(entries.isEmpty()) {
            return null;
        }

        AccountModeration[] actions = new AccountModeration[entries.size()];

        for(int i = 0; i < entries.size(); i++) {
            Object[] currObject = entries.get(i);

            actions[i] = new AccountModeration((int) currObject[0], (String) currObject[1], (String) currObject[2],
                    (LocalDateTime) currObject[3], (String) currObject[4], (String) currObject[5], (LocalDateTime) currObject[6], (boolean) currObject[7]);
        }

        return actions;
    }

    public void createAccountModeration(String actedUser, String moderationDesc, String moderatorWhoActed, String moderationSeverity, Timestamp dateExpires) {
        repository.createAccountModeration(actedUser, moderationDesc, Timestamp.valueOf(LocalDateTime.now()), moderatorWhoActed, moderationSeverity, dateExpires, false);
        sessionService.removeAllSessionsByUserId(actedUser);
    }

    public void createAccountModeration(String actedUser, String moderationDesc, String moderatorWhoActed, String moderationSeverity) {
        repository.createAccountModeration(actedUser, moderationDesc, Timestamp.valueOf(LocalDateTime.now()), moderatorWhoActed, moderationSeverity, Timestamp.valueOf(LocalDateTime.now()), false);
        sessionService.removeAllSessionsByUserId(actedUser);
    }

    public void acknowledgeAccountModeration(int reportId, boolean isAcknowledged) {
        repository.acknowledgeAccountModeration(reportId, isAcknowledged);
    }
}
