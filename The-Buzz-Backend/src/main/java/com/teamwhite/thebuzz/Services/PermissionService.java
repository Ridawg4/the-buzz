package com.teamwhite.thebuzz.Services;

import com.teamwhite.thebuzz.Contexts.BuzzSessionService;
import com.teamwhite.thebuzz.Repositories.PermissionsRepository;
import com.teamwhite.thebuzz.model.Permissions;
import com.teamwhite.thebuzz.model.SessionPermissions;
import jakarta.json.*;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PermissionService {
    private final PermissionsRepository repository;
    private final BuzzSessionService sessionService;

    public PermissionService(PermissionsRepository repository, BuzzSessionService sessionService) {
        this.repository = repository;
        this.sessionService = sessionService;
    }

    public Permissions getPermissionById(int id) {
        List<Object[]> entries = repository.getAllPermissions();

        if(entries.isEmpty()) {
            throw new RuntimeException("Permissions table is empty");
        }

        Object[] currEntry = entries.get(0);

        return new Permissions((int) currEntry[0], (String) currEntry[1],
                (String) currEntry[2], (String) currEntry[3], (String) currEntry[4],
                (LocalDateTime) currEntry[5], (boolean) currEntry[6]);
    }

    public Permissions[] getAllPermissions() {
        List<Object[]> entries = repository.getAllPermissions();

        if(entries.isEmpty()) {
            throw new RuntimeException("Permissions table is empty");
        }

        Permissions[] permissionsRetrieved = new Permissions[entries.size()];

        /** permission_id INTEGER PRIMARY KEY AUTO_INCREMENT,
         permissions_name VARCHAR(50) NOT NULL,
         permission_desc VARCHAR(255),
         permissions JSON NOT NULL,
         created_by BINARY(16) NOT NULL,
         created_at TIMESTAMP NOT NULL, */
        for(int i = 0; i < entries.size(); i++) {
            Object[] currEntry = entries.get(i);

            permissionsRetrieved[i] = new Permissions((int) currEntry[0], (String) currEntry[1],
                    (String) currEntry[2], (String) currEntry[3], (String) currEntry[4],
                    (LocalDateTime) currEntry[5], (boolean) currEntry[6]);
        }

        return permissionsRetrieved;
    }

    public JsonObject getAllPermissionsAsJsonObject() {
        Permissions[] allPermissions = getAllPermissions();
        JsonArrayBuilder arrBuilder = Json.createArrayBuilder();
        JsonObjectBuilder arrWrapper = Json.createObjectBuilder();

        for(Permissions permissions : allPermissions) {
            arrBuilder.add(permissions.asJsonObject());
        }

        return arrWrapper.add("permissions", arrBuilder.build()).build();
    }

    public void updateSessionPermissionEntry(int id, SessionPermissions permissions) {
        repository.updateSessionPermissionByPermissionId(id, permissions.asJsonObject().toString());

        sessionService.labelAllSessionsWithPermissionGroupToRenew(id, id);
    }

    public void updatePermissionEntry(int id, String permissionName, String permissionDesc, SessionPermissions permissions) {
        repository.updatePermissionByPermissionId(id, permissions.asJsonObject().toString(), permissionName, permissionDesc);

        sessionService.labelAllSessionsWithPermissionGroupToRenew(id, id);
    }

    public void deletePermissionEntry(int id) {
        repository.deletePermissionByPermissionId(id);

        sessionService.labelAllSessionsWithPermissionGroupToRenew(id, getDefaultPermissionGroupIndex());
    }

    public boolean doesPermissionExistAtIndex(int id) {
        List<Object[]> responseFromDb = repository.getPermissionById(id);

        return !responseFromDb.isEmpty();
    }

    public void createPermissionEntry(Permissions permission, boolean isDefaultPermission) {
        if(isDefaultPermission) {
            repository.createPermissionGroup(permission.getPermissionId(),
                    permission.getPermissionsName(),
                    permission.getPermissionDesc(),
                    permission.getSessionPermissionsAsJsonObject().toString(),
                    permission.getCreatedBy(),
                    permission.getCreatedAt(),
                    true);

            updateDefaultPermissionGroup(permission.getPermissionId(), true);
        }
        repository.createPermissionGroup(permission.getPermissionId(),
                permission.getPermissionsName(),
                permission.getPermissionDesc(),
                permission.getSessionPermissionsAsJsonObject().toString(),
                permission.getCreatedBy(),
                permission.getCreatedAt(),
                isDefaultPermission);
    }

    public int getDefaultPermissionGroupIndex() {
        List<Object[]> retrievedPreviousDefault = repository.getPermissionIdByIsDefaultPermissionGroup(true);

        if(retrievedPreviousDefault.size() > 1) {
            throw new RuntimeException("More than one default group");
        }

        if(retrievedPreviousDefault.isEmpty()) {
            throw new RuntimeException("Illegal DB State! No default permissions exist");
        }

        return (int) retrievedPreviousDefault.get(0)[0];
    }

    public void updateDefaultPermissionGroup(int id, boolean isDefaultPermissionGroup) {
        int oldDefaultIndex = getDefaultPermissionGroupIndex();

        if(oldDefaultIndex == id) {
            throw new RuntimeException("This group is already the default permission group");
        }

        repository.updateDefaultPermissionByPermissionId(oldDefaultIndex, !isDefaultPermissionGroup);
        repository.updateDefaultPermissionByPermissionId(id, isDefaultPermissionGroup);

        sessionService.labelAllSessionsWithPermissionGroupToRenew(id, oldDefaultIndex);
    }
}
