package ednardo.api.soloapp.controller;

import ednardo.api.soloapp.model.User;
import ednardo.api.soloapp.model.dto.RequestFriendshipDTO;
import ednardo.api.soloapp.service.FriendshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/friendship")
@RestController
public class FriendshipController {
    @Autowired
    FriendshipService friendshipService;

    @PostMapping("/request")
    public ResponseEntity requestFriendship(@RequestBody RequestFriendshipDTO requestFriendshipDTO) {
        this.friendshipService.requestFriendship(requestFriendshipDTO);

        return new ResponseEntity<>("Friendship request has been sent", HttpStatus.CREATED);
    }

    @PutMapping("/update")
    public ResponseEntity updateFriendship(@RequestBody RequestFriendshipDTO requestFriendshipDTO) {
        this.friendshipService.updateFriendship(requestFriendshipDTO);

        return new ResponseEntity<>("Friendship update request has been sent", HttpStatus.NO_CONTENT);
    }

    @GetMapping("/get-status")
    public ResponseEntity getStatus(RequestFriendshipDTO requestFriendshipDTO) {
        String status = this.friendshipService.getStatus(requestFriendshipDTO);

        return ResponseEntity.ok(status);
    }

    @GetMapping("/get-pending/{userId}")
    public ResponseEntity getPending(@PathVariable Long userId, @PageableDefault(size = 8, page = 0) Pageable pageable) {
        Page<User> users = this.friendshipService.getFriendshipsPending(userId, pageable);

        return ResponseEntity.ok(users);
    }

    @GetMapping("/get-accepted/{userId}")
    public ResponseEntity getAccepted(@PathVariable Long userId, @PageableDefault(size = 8, page = 0) Pageable pageable) {
        Page<User> users = this.friendshipService.getFriendshipsAccepted(userId, pageable);

        return ResponseEntity.ok(users);
    }

    @GetMapping("count/friendship/{userId}")
    public ResponseEntity countFriendships(@PathVariable Long userId) {
        int count = this.friendshipService.countFriendships(userId);

        return ResponseEntity.ok(count);
    }
}
