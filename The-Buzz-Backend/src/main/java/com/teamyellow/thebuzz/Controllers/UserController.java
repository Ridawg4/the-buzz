package com.teamyellow.thebuzz.Controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @GetMapping("/api/v1/user-test")
    public String testUserEndpoint() {
        return "User endpoint is working!";
    }
}
