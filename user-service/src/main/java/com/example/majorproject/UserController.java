package com.example.majorproject;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/user")
    public User getUserByEmail(@RequestParam("email") String email) {
        return userService.getUserByEmail(email);
    }

    @PostMapping("/user")
    public void createUser(@RequestBody User user) throws JsonProcessingException {
        userService.createUser(user);
    }
}
