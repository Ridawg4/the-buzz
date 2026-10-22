package com.teamwhite.thebuzz.Controllers.API.V1;


import com.teamwhite.thebuzz.Resources.PermissionNames;
import com.teamwhite.thebuzz.Services.UserService;
import jakarta.json.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/load-details?user={userId}")
    @PreAuthorize("hasAuthority('CAN_SEE_ACCOUNT_INFO')")
    public RequestEntity<JsonObject> testUserEndpoint(@PathVariable("userId") String userId) {
        
    }

    /**
     * TIME SPENT GETTING AUTHENTICATION WORKING SO FAR:  32 HOURS 15 MINUTES
     * ALMOST DONE AT 20 HOURS 18 MINUTES
     * ACTUALLY ALMOST DONE 32 HOURS 15 MINUTES
     */

    // @TODO implement account modification and accessing endpoints
}
