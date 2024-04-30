package ednardo.api.soloapp.controller;

import ednardo.api.soloapp.model.User;
import ednardo.api.soloapp.service.FriendshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/friendship")
@RestController
public class FriendshipController {
    @Autowired
    FriendshipService friendshipService;

    @PostMapping("/request")
    public ResponseEntity requestFriendship(@RequestBody User userFrom, @RequestBody User userTo) {
        this.friendshipService.requestFriendship(userFrom, userTo);

        return new ResponseEntity<>("Friendship request has been sent", HttpStatus.CREATED);
    }
}
