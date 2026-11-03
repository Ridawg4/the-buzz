package com.teamwhite.thebuzz.Controllers.API.V1;


import com.teamwhite.thebuzz.Contexts.BuzzSessionService;
import com.teamwhite.thebuzz.Records.ContentTypes;
import com.teamwhite.thebuzz.Resources.PermissionNames;
import com.teamwhite.thebuzz.Resources.PermissionValues;
import com.teamwhite.thebuzz.Resources.ResourcePaths;
import com.teamwhite.thebuzz.Services.LocalStorage;
import com.teamwhite.thebuzz.Services.UserService;
import com.teamwhite.thebuzz.model.BuzzUser;
import jakarta.json.JsonObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.tomcat.util.http.parser.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.web.exchanges.HttpExchange;
import org.springframework.cglib.core.Local;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Optional;
import java.util.logging.Logger;


@RestController
@RequestMapping("/api/v1/user")
@EnableMethodSecurity
public class UserController {

    private final UserService userService;
    private final BuzzSessionService sessionService;


    @Autowired
    public UserController(UserService userService, BuzzSessionService sessionService) {
        this.userService = userService;
        this.sessionService = sessionService;
    }

    @GetMapping("AuthValid")
    public ResponseEntity<String> isUserAuthenticated(Authentication userAuth) {
        BuzzUser user = null;
        if(userAuth.getPrincipal() instanceof BuzzUser) {
            user = (BuzzUser) userAuth.getPrincipal();
        }

        // Verifies authentication is a valid BuzzUser
        if(user == null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(user.getUserId());
    }

    @GetMapping("/profile-picture{time}")
    @PreAuthorize("hasAuthority('CAN_SEE_ACCOUNT_INFO')")
    public ResponseEntity<Resource> loadProfilePicture(@PathVariable String time, Authentication userAuth) {
        ResponseEntity<Resource> resp;
        BuzzUser user = null;

        if(userAuth.getPrincipal() instanceof BuzzUser) {
            user = (BuzzUser) userAuth.getPrincipal();
        }

        // Verifies authentication is a valid BuzzUser
        if(user == null) {
            return ResponseEntity.badRequest().build();
        }

        // Verifies the user has the proper permission to access the endpoint
        if(!PermissionValues.IF_OWNER.equals(user.getPermissions().getPermission("CAN_SEE_ACCOUNT_INFO"))) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        try {
            Optional<Resource> resourceOptional = LocalStorage.retrieveFileFromLocalStorage(
                    user.getUserPfpLocation());

            if(resourceOptional.isPresent()) {
                resp = new ResponseEntity<>(resourceOptional.get(), HttpStatus.OK);
                resp.getHeaders().add("Cache-Control", "no-cache");
                return resp;
            }
        } catch (Exception e) {

        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/load-details")
    @PreAuthorize("hasAuthority('CAN_SEE_ACCOUNT_INFO')")
    public ResponseEntity<String> loadUserDetails(Authentication userAuth) {

        BuzzUser user = null;
        if(userAuth.getPrincipal() instanceof BuzzUser) {
            user = (BuzzUser) userAuth.getPrincipal();
        }

        // Verifies authentication is a valid BuzzUser
        if(user == null) {
            return ResponseEntity.badRequest().build();
        }

        // Verifies the user has the proper permission to access the endpoint
        if(!PermissionValues.IF_OWNER.equals(user.getPermissions().getPermission("CAN_SEE_ACCOUNT_INFO"))) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
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
    @PreAuthorize("hasAuthority('CAN_MODIFY_ACCOUNT_INFO')")
    public ResponseEntity<String> modifyUserDetails(@RequestParam("username") String username,
                                                    @RequestParam("email") String email,
                                                    @RequestParam("full_name") String fullName,
                                                    @RequestParam("user_desc") String userDesc,
                                                    Authentication userAuth,
                                                    HttpServletRequest request,
                                                    HttpServletResponse response) {
        BuzzUser user = null;
        // Checks if the authentication contains a version of our user
        if(userAuth.getPrincipal() instanceof BuzzUser) {
            user = (BuzzUser) userAuth.getPrincipal();
        }

        // If the user is null, the user is not authenticated properly and therefore has a bad request
        if(user == null) {
            return ResponseEntity.badRequest().build();
        }

        // If the user doesn't have the correct permissions, they are unauthorized
        if(!PermissionValues.IF_OWNER.equals(user.getPermissions().getPermission("CAN_MODIFY_ACCOUNT_INFO"))) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        /**
         * Fun Fact: If you swap the email and username field, it breaks things
         */
        // Modifies the user in the database
        userService.modifyUser(user.getUserId(), email, username, fullName, userDesc);
        // Needs the request and response for some reason
        sessionService.labelAllSessionsForUserToRenew(user.getUserId(), userAuth, request, response);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/modify-profile-picture")
    @PreAuthorize("hasAuthority('CAN_MODIFY_ACCOUNT_INFO')")
    public ResponseEntity<String> modifyProfilePicture(@RequestParam("file") MultipartFile file,
                                                    Authentication userAuth,
                                                       HttpServletRequest request,
                                                       HttpServletResponse response) {
        BuzzUser user = null;
        // Checks if the authentication contains a version of our user
        if(userAuth.getPrincipal() instanceof BuzzUser) {
            user = (BuzzUser) userAuth.getPrincipal();
        }

        // If the user is null, the user is not authenticated properly and therefore has a bad request
        if(user == null) {
            return ResponseEntity.badRequest().build();
        }

        // If the user doesn't have the correct permissions, they are unauthorized
        if(!PermissionValues.IF_OWNER.equals(user.getPermissions().getPermission("CAN_MODIFY_ACCOUNT_INFO"))) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        File movedFile = null;
        String fileType = file.getContentType();
        fileType = fileType.substring(fileType.indexOf("/") + 1);
        String fileName = user.getUserId() + "." + fileType;
        try {
            LocalStorage.moveFileToLocalStorage(file, ResourcePaths.PROFILE_PICTURE_DIRECTORY, fileName);
        } catch (Exception e) {

        }

        /**
         * Fun Fact: If you swap the email and username field, it breaks things
         */
        // Modifies the user in the database
        try {
            userService.modifyProfilePicture(ResourcePaths.PROFILE_PICTURE_DIRECTORY, fileName, user.getUserId());
        } catch (Exception e) {

        }
        // Needs the request and response for some reason
        sessionService.labelAllSessionsForUserToRenew(user.getUserId(), userAuth, request, response);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/delete-account")
    @PreAuthorize("hasAuthority('CAN_DELETE_ACCOUNT')")
    public ResponseEntity<String> deleteAccount(@RequestParam boolean approved, Authentication userAuth) {
        BuzzUser user = null;
        // Checks if the authentication contains a version of our user
        if(userAuth.getPrincipal() instanceof BuzzUser) {
            user = (BuzzUser) userAuth.getPrincipal();
        }

        // If the user is null, the user is not authenticated properly and therefore has a bad request
        if(user == null) {
            return ResponseEntity.badRequest().build();
        }

        // If the user doesn't have the correct permissions, they are unauthorized
        if(!PermissionValues.IF_OWNER.equals(user.getPermissions().getPermission("CAN_DELETE_ACCOUNT"))) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        if(!approved) {
            return new ResponseEntity<>("Flag for verification is not true", HttpStatus.BAD_REQUEST);
        }

        // Gets the HttpServletRequest and HttpServletResponse objects in the request
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        HttpServletRequest request;
        HttpServletResponse response;

        try {
            request = attributes.getRequest();
            response = attributes.getResponse();
        } catch (NullPointerException exception) {
            // If no request or response exist, something weird happened
            Logger.getGlobal().warning("Account Deletion has an error: " + exception.getMessage());
            return new ResponseEntity<>("Error deleting your account", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        sessionService.labelAllSessionsForUserToRenew(user.getUserId(), userAuth, request, response);
        userService.deleteUser(user.getEmail());

        return new ResponseEntity<>("Account Deleted", HttpStatus.OK);
    }

    // @TODO IMPLEMENT USER PASSWORD CHANGING
}
