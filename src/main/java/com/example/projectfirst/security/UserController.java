package com.example.projectfirst.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/signin")
    public JWTResponse getTokenForUser(@RequestBody JWTRequest jwtRequest) throws BadCredentialsException {
        return userService.getTokenForUser(jwtRequest);
    }

    @PostMapping("/signup")
    public String saveUser(@RequestBody MyUser user){
        return userService.saveUser(user);
    }

    @GetMapping("/signout")
    public String revokeTokenFromUser(){
        return "Successfully logged out!";
    }
}
