package com.teamwhite.thebuzz.Services;

import com.teamwhite.thebuzz.Repositories.PermissionsRepository;
import com.teamwhite.thebuzz.model.Permissions;
import jakarta.json.*;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PermissionService {
    private final PermissionsRepository repository;

    public PermissionService(PermissionsRepository repository) {
        this.repository = repository;
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
                    (String) currEntry[2], (String) currEntry[3], (String) currEntry[4], (LocalDateTime) currEntry[5]);
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

    public void updatePermissionEntry() {

    }

    public void createPermissionEntry(Permissions permission) {

    }
}
