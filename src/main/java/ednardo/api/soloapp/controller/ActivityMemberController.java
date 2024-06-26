package ednardo.api.soloapp.controller;

import ednardo.api.soloapp.model.ActivityMember;
import ednardo.api.soloapp.model.User;
import ednardo.api.soloapp.model.dto.ActivityMemberRequestDTO;
import ednardo.api.soloapp.service.ActivityMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/activity/member")
public class ActivityMemberController {
    @Autowired
    ActivityMemberService activityMemberService;

    @GetMapping("/{id}")
    public ResponseEntity getActivityMemberById(@PathVariable Long id) {
        ActivityMember activityMember = activityMemberService.getById(id);

        return ResponseEntity.ok(activityMember);
    }

    @PostMapping("/join")
    public ResponseEntity requestToJoinActivity(@RequestBody ActivityMemberRequestDTO activityMemberRequestDTO) {
        this.activityMemberService.requestToJoin(activityMemberRequestDTO);

        return new ResponseEntity<>("Request to join the activity has been sent", HttpStatus.CREATED);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity updateActivityMember(@PathVariable Long id, @RequestBody ActivityMember activityMember) {
        this.activityMemberService.update(id, activityMember);

        return new ResponseEntity<>("Updated", HttpStatus.NO_CONTENT);
    }

    @GetMapping("/get-status")
    public ResponseEntity getStatusActivityMember(ActivityMemberRequestDTO activityMemberRequestDTO) {
        String status = this.activityMemberService.getStatus(activityMemberRequestDTO);

        return ResponseEntity.ok(status);
    }

    @GetMapping("/get-pending/{activityId}")
    public ResponseEntity getPendingUsers(@PathVariable Long activityId, @PageableDefault(size = 8, page = 0) Pageable pageable) {
        Page<User> users = this.activityMemberService.getPending(activityId, pageable);

        return ResponseEntity.ok(users);
    }

    @GetMapping("/get-accepted/{activityId}")
    public ResponseEntity getAcceptedUsers(@PathVariable Long activityId) {
        List<User> users = this.activityMemberService.getAccepted(activityId);

        return ResponseEntity.ok(users);
    }



}
