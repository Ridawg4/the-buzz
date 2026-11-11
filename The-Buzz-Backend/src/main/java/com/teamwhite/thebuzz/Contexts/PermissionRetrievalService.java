package com.teamwhite.thebuzz.Contexts;

import com.teamwhite.thebuzz.Repositories.PermissionsRepository;
import com.teamwhite.thebuzz.model.Permissions;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PermissionRetrievalService {
    private static PermissionsRepository repository;

    public PermissionRetrievalService(PermissionsRepository repository) {
        PermissionRetrievalService.repository = repository;
    }

    public static Permissions getPermissionById(int id) {
        List<Object[]> entries = repository.getPermissionById(id);

        if(entries.isEmpty()) {
            throw new RuntimeException("Permissions table is empty");
        }

        Object[] currEntry = entries.get(0);

        return new Permissions((int) currEntry[0], (String) currEntry[1],
                (String) currEntry[2], (String) currEntry[3], (String) currEntry[4], (LocalDateTime) currEntry[5]);
    }

    public static int getDefaultPermissionGroupIndex() {
        List<Object[]> retrievedPreviousDefault = repository.getPermissionIdByIsDefaultPermissionGroup(true);

        if(retrievedPreviousDefault.size() > 1) {
            throw new RuntimeException("More than one default group");
        }

        return (int) retrievedPreviousDefault.get(0)[0];
    }
}
