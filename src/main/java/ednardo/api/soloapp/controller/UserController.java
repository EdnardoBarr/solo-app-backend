package ednardo.api.soloapp.controller;

import ednardo.api.soloapp.model.User;
import ednardo.api.soloapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;

    @PostMapping("/create")
    public User createUser(@RequestBody User user) {
        return userService.save(user);
    }

}
