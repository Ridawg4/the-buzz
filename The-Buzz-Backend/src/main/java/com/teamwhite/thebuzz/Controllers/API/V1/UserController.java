package com.teamwhite.thebuzz.Controllers.API.V1;


import com.teamwhite.thebuzz.Records.ContentTypes;
import com.teamwhite.thebuzz.Resources.PermissionNames;
import com.teamwhite.thebuzz.Resources.PermissionValues;
import com.teamwhite.thebuzz.Services.UserService;
import com.teamwhite.thebuzz.model.BuzzUser;
import jakarta.json.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.web.exchanges.HttpExchange;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.logging.Logger;

import static org.springframework.security.authorization.AuthorityAuthorizationManager.hasAuthority;

@RestController
@RequestMapping("/api/v1/user")
@EnableMethodSecurity
public class UserController {

    private final UserService userService;


    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/load-details")
    @PreAuthorize("hasAuthority('CAN_SEE_ACCOUNT_INFO')")
    public ResponseEntity<String> testUserEndpoint(Authentication userAuth) {
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

    // @TODO implement account modification and accessing endpoints

    @PostMapping("/modify-user")
    @PreAuthorize("hasAuthority('CAN_MODIFY_ACCOUNT_INFO')")
    public ResponseEntity<String> modifyUserDetails(@RequestParam("username") String username,
                                                    @RequestParam("email") String email,
                                                    @RequestParam("full_name") String fullName,
                                                    @RequestParam("user_desc") String userDesc,
                                                    Authentication userAuth) {
        BuzzUser user = null;
        if(userAuth.getPrincipal() instanceof BuzzUser) {
            user = (BuzzUser) userAuth.getPrincipal();
        }

        if(user == null) {
            return ResponseEntity.badRequest().build();
        }

        if(!PermissionValues.IF_OWNER.equals(user.getPermissions().getPermission("CAN_MODIFY_ACCOUNT_INFO"))) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        // @TODO MAKES SESSION STALE, UPDATE SESSION AFTER CALLING
        userService.modifyUser(user.getUserId(), username, email, fullName, userDesc);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
}
