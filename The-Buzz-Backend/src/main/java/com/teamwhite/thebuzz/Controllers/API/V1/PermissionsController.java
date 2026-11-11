package com.teamwhite.thebuzz.Controllers.API.V1;

import com.teamwhite.thebuzz.Records.ContentTypes;
import com.teamwhite.thebuzz.Services.PermissionService;
import com.teamwhite.thebuzz.model.BuzzUser;
import com.teamwhite.thebuzz.model.Permissions;
import com.teamwhite.thebuzz.model.SessionPermissions;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.session.Session;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/v1/permissions")
public class PermissionsController {
    private PermissionService permissionService;

    public PermissionsController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('CAN_MODIFY_PERMISSION_GROUPS')")
    public ResponseEntity<String> getAllPermissionGroups() {
        ResponseEntity<String> resp = new ResponseEntity<>(permissionService.getAllPermissionsAsJsonObject().toString(), HttpStatus.OK);
        resp.getHeaders().add(HttpHeaders.CONTENT_TYPE, ContentTypes.JSON);

        return resp;
    }

    @GetMapping("/defaults")
//    @PreAuthorize("hasAuthority('CAN_MODIFY_PERMISSION_GROUPS')")
    public ResponseEntity<String> getAllPermissionNames(Authentication userAuth) {
        if (!(userAuth.getPrincipal() instanceof BuzzUser)) {
            throw new RuntimeException("Not a BuzzUser");
        }
        BuzzUser user = (BuzzUser) userAuth.getPrincipal();

        ResponseEntity<String> resp = new ResponseEntity<>(permissionService.getPermissionById(
                permissionService.getDefaultPermissionGroupIndex()).asJsonObject().toString(), HttpStatus.OK);
        resp.getHeaders().add(HttpHeaders.CONTENT_TYPE, ContentTypes.JSON);

        return resp;
    }

    @PostMapping("/create-permission-group")
    @PreAuthorize("hasAuthority('CAN_MODIFY_PERMISSION_GROUPS')")
    public ResponseEntity<String> createPermissionEntry(@RequestParam String permissionObject,
                                                        @RequestParam String permissionName,
                                                        @RequestParam String permissionDesc,
                                                        @RequestParam(required = false) boolean isDefaultPermissionGroup,
                                                        Authentication userAuth) {
        ResponseEntity<String> resp;
        // @TODO TEST
        SessionPermissions permissionToCreate = new SessionPermissions(permissionObject);
        if (permissionService.doesPermissionExistAtIndex(permissionToCreate.getPermissionId())) {
            resp = new ResponseEntity<>("Entry already exists at that index", HttpStatus.BAD_REQUEST);
            return resp;
        }

        if (!(userAuth.getPrincipal() instanceof BuzzUser)) {
            throw new RuntimeException("Not a BuzzUser");
        }

        BuzzUser user = (BuzzUser) userAuth.getPrincipal();

        Permissions permissionGroup = new Permissions(permissionName, permissionDesc, permissionToCreate, user.getUserId(), LocalDateTime.now());
        permissionService.createPermissionEntry(permissionGroup, isDefaultPermissionGroup);

        resp = new ResponseEntity<>(HttpStatus.CREATED);
        return resp;
    }

    @PostMapping("/update-permission-group")
//    @PreAuthorize("hasAuthority('CAN_MODIFY_PERMISSION_GROUPS')")
    public ResponseEntity<String> updatePermissionEntry(@RequestParam String permissionObject) {
        ResponseEntity<String> resp = new ResponseEntity<>(HttpStatus.OK);
        SessionPermissions permissionsToEdit = new SessionPermissions(permissionObject);

        permissionService.updatePermissionEntry(permissionsToEdit.getPermissionId(), permissionsToEdit);

        return resp;
    }

    @PostMapping("/update-default-permission-group")
    @PreAuthorize("hasAuthority('CAN_MODIFY_PERMISSION_GROUPS')")
    public ResponseEntity<String> updatePermissionEntry(@RequestParam int permissionId,
                                                        @RequestParam boolean isDefaultPermissionGroup) {
        ResponseEntity<String> resp = new ResponseEntity<>(HttpStatus.OK);
        permissionService.updateDefaultPermissionGroup(permissionId, isDefaultPermissionGroup);

        return resp;
    }

    // @TODO implement modifying user permissions
    // @TODO implement default permission changing
    // @TODO implement creating permission groups
    // @TODO implement deleting permission groups
}
