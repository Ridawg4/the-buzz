package com.teamwhite.thebuzz.Controllers.API.V1;

import com.teamwhite.thebuzz.Repositories.UserRepository;
import com.teamwhite.thebuzz.Services.UserService;
import org.springframework.boot.micrometer.observation.autoconfigure.ObservationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/signup")
public class SignupHandler {
    private UserService userService;

    public SignupHandler(UserService userService) {
        this.userService = userService;
    }

    @PostMapping()
    public ResponseEntity<String>
    createAccount(@RequestParam("username") String username,
                  @RequestParam("email") String email,
                  @RequestParam("password") String unhashedPassword) {
        if(username.isEmpty()) {
            return new ResponseEntity<>("Username cannot be blank", HttpStatus.BAD_REQUEST);
        }
        if(email.isEmpty()) {
            return new ResponseEntity<>("Email cannot be blank", HttpStatus.BAD_REQUEST);
        }
        if(unhashedPassword.isEmpty()) {
            return new ResponseEntity<>("Password cannot be blank", HttpStatus.BAD_REQUEST);
        }
        if(userService.doesUserExist(email)) {
            return new ResponseEntity<>("Email already has an account", HttpStatus.BAD_REQUEST);
        }

        userService.createUser(username, email, unhashedPassword);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create("/login.html"))
                .build();
    }

    @GetMapping("/emailExist?email={email}")
    public ResponseEntity<String> doesEmailExist(@PathVariable("email") String email) {
        if(email == null && (!email.isEmpty())) {
            return new ResponseEntity<>("No email was received", HttpStatus.BAD_REQUEST);
        }
        boolean result = userService.doesUserExist(email);

        if(result) {
            return new ResponseEntity<>("Email already registered", HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
