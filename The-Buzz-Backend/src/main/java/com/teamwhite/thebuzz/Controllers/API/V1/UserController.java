package com.teamwhite.thebuzz.Controllers.API.V1;

import com.teamwhite.thebuzz.Contexts.BuzzSessionService;
import com.teamwhite.thebuzz.Exceptions.InvalidAuthenticationPrincipalException;
import com.teamwhite.thebuzz.Records.ContentTypes;
import com.teamwhite.thebuzz.Resources.PermissionValues;
import com.teamwhite.thebuzz.Resources.ResourcePaths;
import com.teamwhite.thebuzz.Services.LocalStorage;
import com.teamwhite.thebuzz.Services.UserService;
import com.teamwhite.thebuzz.model.BuzzUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.modelmapper.internal.bytebuddy.utility.nullability.NeverNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Optional;


@RestController
@RequestMapping("/api/v1/user")
@EnableMethodSecurity
public class UserController {

    private final UserService userService;
    private final BuzzSessionService sessionService;
    private final PasswordEncoder passwordEncoder;


    @Autowired
    public UserController(UserService userService, BuzzSessionService sessionService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.sessionService = sessionService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("AuthValid")
    public ResponseEntity<String> isUserAuthenticated(Authentication userAuth) {
        BuzzUser user = null;
        try {
            user = getUserFromPrincipal(userAuth);
        } catch (InvalidAuthenticationPrincipalException exc) {
            ResponseEntity.badRequest().build();
        }

        assert user != null;
        return ResponseEntity.ok(user.getUserId());
    }

    @GetMapping("HasPermission")
    public ResponseEntity<String> doesUserHavePermission(@RequestParam(required = false) String userId,
                                                         @RequestParam String permissionToLookFor,
                                                         Authentication userAuth) {
        BuzzUser user = null;
        try {
            user = getUserFromPrincipal(userAuth);
        } catch (InvalidAuthenticationPrincipalException exc) {
            ResponseEntity.badRequest().build();
        }

        if(userId != null) {
            if(!PermissionValues.ALLOWED.equals(user.getPermissions().getPermission("CAN_SEE_SYSTEM_PANELS"))) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            return ResponseEntity.ok(userService.getUserRecordByUserId(userId).getPermissions().getPermission(permissionToLookFor));
        }

        assert user != null;
        return ResponseEntity.ok(user.getPermissions().getPermission(permissionToLookFor));
    }

    @GetMapping("/profile-picture")
    @PreAuthorize("hasAnyAuthority('CAN_SEE_ACCOUNT_INFO', 'CAN_SEE_SYSTEM_PANELS')")
    public ResponseEntity<Resource> loadProfilePicture(@RequestParam(required = false) String time,
                                                       @RequestParam(required = false) String userId,
                                                       Authentication userAuth) {
        ResponseEntity<Resource> resp;
        BuzzUser user = null;
        try {
            user = getUserFromPrincipal(userAuth);
        } catch (InvalidAuthenticationPrincipalException exc) {
            ResponseEntity.badRequest().build();
        }

        // Verifies the user has the proper permission to access the endpoint
        assert user != null;
        if(!PermissionValues.IF_OWNER.equals(user.getPermissions().getPermission("CAN_SEE_ACCOUNT_INFO"))) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        if(userId != null) {
            if(!PermissionValues.ALLOWED.equals(user.getPermissions().getPermission("CAN_SEE_SYSTEM_PANELS"))) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            try {
                // @TODO issue here somewhere
                Optional<Resource> resourceOptional = LocalStorage.retrieveFileFromLocalStorage(
                        userService.getUserRecordByUserId(userId).getUserPfpLocation());

                if(resourceOptional.isPresent()) {
                    resp = new ResponseEntity<>(resourceOptional.get(), HttpStatus.OK);
                    return resp;
                }
            } catch (Exception e) {

            }

            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        try {
            // @TODO issue here somewhere
            Optional<Resource> resourceOptional = LocalStorage.retrieveFileFromLocalStorage(
                    user.getUserPfpLocation());

            if(resourceOptional.isPresent()) {
                resp = new ResponseEntity<>(resourceOptional.get(), HttpStatus.OK);
                return resp;
            }
        } catch (Exception e) {

        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/load-details")
    @PreAuthorize("hasAnyAuthority('CAN_SEE_ACCOUNT_INFO', 'CAN_SEE_SYSTEM_PANELS')")
    public ResponseEntity<String> loadUserDetails(@RequestParam(required = false) String userId, Authentication userAuth) {
        BuzzUser user = null;
        try {
            user = getUserFromPrincipal(userAuth);
        } catch (InvalidAuthenticationPrincipalException exc) {
            ResponseEntity.badRequest().build();
        }

        // Verifies the user has the proper permission to access the endpoint
        assert user != null;
        if(!PermissionValues.IF_OWNER.equals(user.getPermissions().getPermission("CAN_SEE_ACCOUNT_INFO"))) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        if(userId != null) {
            // @TODO Create permission to see other users details instead of assuming all moderators can see system panels
            if(!PermissionValues.ALLOWED.equals(user.getPermissions().getPermission("CAN_SEE_SYSTEM_PANELS"))) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            ResponseEntity<String> response = new ResponseEntity<>(userService.getUserRecordByUserId(userId).getAccountDetailsWithoutSettings().toString(), HttpStatus.OK);
            response.getHeaders().add("Content-Type", ContentTypes.JSON);

            return response;
        }

        ResponseEntity<String> response = new ResponseEntity<>(user.getAccountDetailsWithoutSettings().toString(), HttpStatus.OK);
        response.getHeaders().add("Content-Type", ContentTypes.JSON);

        return response;
    }

    /**
     * TIME SPENT GETTING AUTHENTICATION WORKING SO FAR:  32 HOURS 15 MINUTES
     * ALMOST DONE AT 20 HOURS 18 MINUTES
     * ACTUALLY ALMOST DONE 32 HOURS 15 MINUTES
     */

    @PostMapping("/modify-user")
    @PreAuthorize("hasAnyAuthority('CAN_MODIFY_ACCOUNT_INFO', 'CAN_MODIFY_ANY_ACCOUNT_INFO')")
    public ResponseEntity<String> modifyUserDetails(@RequestParam("username") String username,
                                                    @RequestParam(required = false) String userId,
                                                    @RequestParam("email") String email,
                                                    @RequestParam("full_name") String fullName,
                                                    @RequestParam("user_desc") String userDesc,
                                                    Authentication userAuth,
                                                    HttpServletRequest request,
                                                    HttpServletResponse response) {
        BuzzUser user = null;
        try {
            user = getUserFromPrincipal(userAuth);
        } catch (InvalidAuthenticationPrincipalException exc) {
            ResponseEntity.badRequest().build();
        }

        // If the user doesn't have the correct permissions, they are unauthorized
        assert user != null;
        if(!PermissionValues.IF_OWNER.equals(user.getPermissions().getPermission("CAN_MODIFY_ACCOUNT_INFO"))) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        if(userId != null) {
            if(!PermissionValues.ALLOWED.equals(user.getPermissions().getPermission("CAN_MODIFY_ANY_ACCOUNT_INFO"))) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            userService.modifyUser(userId, email, username, fullName, userDesc);
            // Needs the request and response for some reason
            sessionService.labelAllSessionsForUserToRenew(userId, userAuth, request, response);

            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        }

        /**
         * Fun Fact: If you swap the email and username field, it breaks things
         */
        // Modifies the user in the database
        userService.modifyUser(user.getUserId(), email, username, fullName, userDesc);
        // Needs the request and response for some reason
        sessionService.labelAllSessionsForUserToRenew(user.getUserId(), userAuth, request, response);

        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @PostMapping("/modify-profile-picture")
    @PreAuthorize("hasAnyAuthority('CAN_MODIFY_ACCOUNT_INFO', 'CAN_MODIFY_ANY_ACCOUNT_INFO')")
    public ResponseEntity<String> modifyProfilePicture(@RequestParam("file") MultipartFile file,
                                                       @RequestParam(required = false) String userId,
                                                       Authentication userAuth,
                                                       HttpServletRequest request,
                                                       HttpServletResponse response) {
        BuzzUser user = null;
        try {
            user = getUserFromPrincipal(userAuth);
        } catch (InvalidAuthenticationPrincipalException exc) {
            ResponseEntity.badRequest().build();
        }
        // If the user doesn't have the correct permissions, they are unauthorized
        assert user != null;
        if(!PermissionValues.IF_OWNER.equals(user.getPermissions().getPermission("CAN_MODIFY_ACCOUNT_INFO"))) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        if(userId != null) {
            if(!PermissionValues.ALLOWED.equals(user.getPermissions().getPermission("CAN_MODIFY_ANY_ACCOUNT_INFO"))) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            BuzzUser userFromRequestParam = userService.getUserRecordByUserId(userId);
            // Removes the old stored profile picture, which will create concurrency issues
            LocalStorage.removeFileFromLocalStorage(userFromRequestParam.getUserPfpLocation());

            String fileType = file.getContentType();
            fileType = fileType.substring(fileType.indexOf("/") + 1);
            String fileName = userFromRequestParam.getUserId() + "." + fileType;
            try {
                LocalStorage.moveFileToLocalStorage(file, ResourcePaths.PROFILE_PICTURE_DIRECTORY, fileName);
            } catch (Exception e) {
                return new ResponseEntity<>("Request Failed: Server was unable to save profile picture to storage", HttpStatus.INTERNAL_SERVER_ERROR);
            }


            // Modifies the user in the database
            try {
                userService.modifyProfilePicture(ResourcePaths.PROFILE_PICTURE_DIRECTORY, fileName, userId);
            } catch (Exception e) {
                return new ResponseEntity<>("Request Failed: Server was unable to persist profile picture change", HttpStatus.INTERNAL_SERVER_ERROR);
            }
            // Needs the request and response for some reason
            sessionService.labelAllSessionsForUserToRenew(userId, userAuth, request, response);


            return new ResponseEntity<>(HttpStatus.CREATED);
        }

        // Removes the old stored profile picture, which will create concurrency issues
        LocalStorage.removeFileFromLocalStorage(user.getUserPfpLocation());

        String fileType = file.getContentType();
        fileType = fileType.substring(fileType.indexOf("/") + 1);
        String fileName = user.getUserId() + "." + fileType;
        try {
            LocalStorage.moveFileToLocalStorage(file, ResourcePaths.PROFILE_PICTURE_DIRECTORY, fileName);
        } catch (Exception e) {

        }


        // Modifies the user in the database
        try {
            userService.modifyProfilePicture(ResourcePaths.PROFILE_PICTURE_DIRECTORY, fileName, user.getUserId());
        } catch (Exception e) {

        }
        // Needs the request and response for some reason
        sessionService.labelAllSessionsForUserToRenew(user.getUserId(), userAuth, request, response);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    // @TODO ALLOW FOR MODERATORS TO DELETE ACCOUNTS
    @PostMapping("/delete-account")
    @PreAuthorize("hasAuthority('CAN_DELETE_ACCOUNT')")
    public ResponseEntity<String> deleteAccount(@RequestParam boolean approved,
                                                Authentication userAuth,
                                                HttpServletRequest request,
                                                HttpServletResponse response) {
        BuzzUser user = null;
        try {
            user = getUserFromPrincipal(userAuth);
        } catch (InvalidAuthenticationPrincipalException exc) {
            ResponseEntity.badRequest().build();
        }

        // If the user doesn't have the correct permissions, they are unauthorized
        assert user != null;
        if(!PermissionValues.IF_OWNER.equals(user.getPermissions().getPermission("CAN_DELETE_ACCOUNT"))) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        if(!approved) {
            return new ResponseEntity<>("Flag for verification is not true", HttpStatus.BAD_REQUEST);
        }

        sessionService.removeAllSessionsByUserId(user.getUserId());
        userService.deleteUser(user.getEmail());

        return new ResponseEntity<>("Account Deleted", HttpStatus.OK);
    }

    @PostMapping("/modify-password")
    @PreAuthorize("hasAuthority('CAN_MODIFY_ACCOUNT_INFO')")
    public ResponseEntity<String> modifyPassword(@RequestParam String newPassword,
                                                 Authentication userAuth,
                                                 HttpServletRequest request,
                                                 HttpServletResponse response) {
        BuzzUser user = null;
        try {
            user = getUserFromPrincipal(userAuth);
        } catch (InvalidAuthenticationPrincipalException exc) {
            ResponseEntity.badRequest().build();
        }

        // If the user doesn't have the correct permissions, they are unauthorized
        assert user != null;
        if(!PermissionValues.IF_OWNER.equals(user.getPermissions().getPermission("CAN_MODIFY_ACCOUNT_INFO"))) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String newHashedPassword = passwordEncoder.encode(newPassword);

        userService.updateUserPasswordHash(user.getEmail(), user.getPasswordHash(),  newHashedPassword);
        sessionService.removeAllSessionsByUserId(user.getUserId());

        return new ResponseEntity<>("Password Changed Successfully", HttpStatus.OK);
    }

    private @NeverNull BuzzUser getUserFromPrincipal(Authentication userAuth) {
        BuzzUser user = null;
        // Checks if the authentication contains a version of our user
        if(userAuth.getPrincipal() instanceof BuzzUser) {
            user = (BuzzUser) userAuth.getPrincipal();
        }

        // If the user is null, the user is not authenticated properly and therefore has a bad request
        if(user == null) {
            throw new InvalidAuthenticationPrincipalException("Principal was not BuzzUser object");
        }

        return user;
    }

}
