package com.teamwhite.thebuzz.Controllers.API.V1;

import com.teamwhite.thebuzz.Services.Users.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Logger;

@RestController
@RequestMapping("/api/v1/tests")
public class UserController {

    private final UserService userService;


    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user")
    public String testUserEndpoint() {
        return "User.java endpoint is working!";
    }

    /**
     * TIME SPENT GETTING AUTHENTICATION WORKING SO FAR: 5 HOURS
     */
    @GetMapping("/queryDB")
    public void getUrl() {
        Logger.getGlobal().info(userService.getUserRecordByUsername("testUser").toString());
    }
}
