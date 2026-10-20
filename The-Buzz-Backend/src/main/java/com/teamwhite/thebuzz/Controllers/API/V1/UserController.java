package com.teamwhite.thebuzz.Controllers.API.V1;

import com.teamwhite.thebuzz.Services.UserService;
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
        return "BuzzUser.java endpoint is working!";
    }

    /**
     * TIME SPENT GETTING AUTHENTICATION WORKING SO FAR:  22 HOURS 39 MINUTES 1:50 -> 3:48 && 4:38 -> 6:00  && 9:38 ->
     * ALMOST DONE AT 20 HOURS 18 MINUTES
     */
    @GetMapping("/queryDB")
    public void getUrl() {
        Logger.getGlobal().info(userService.getUserRecordByEmail("a@a.c").toString());
    }
}
