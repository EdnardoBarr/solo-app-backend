package ednardo.api.soloapp.controller;

import ednardo.api.soloapp.model.ActivityMember;
import ednardo.api.soloapp.service.ActivityMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/activity/member")
public class ActivityMemberController {
    @Autowired
    ActivityMemberService activityMemberService;

    @GetMapping("/{id}")
    public ResponseEntity getActivityMemberById(@PathVariable Long id) {
        ActivityMember activityMember = activityMemberService.getById(id);

        return ResponseEntity.ok(activityMember);
    }

    @PostMapping("/request/{activityId}")
    public ResponseEntity requestToJoinActivity(@PathVariable Long activityId, @RequestBody Long userId) {
        this.activityMemberService.requestToJoin(activityId, userId);

        return new ResponseEntity<>("Request to join the activity has been sent", HttpStatus.CREATED);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity updateActivityMember(@PathVariable Long id, @RequestBody ActivityMember activityMember) {
        this.activityMemberService.update(id, activityMember);

        return new ResponseEntity<>("Updated", HttpStatus.NO_CONTENT);
    }

}
