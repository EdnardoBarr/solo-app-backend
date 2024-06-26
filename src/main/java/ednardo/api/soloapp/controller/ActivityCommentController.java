package ednardo.api.soloapp.controller;

import ednardo.api.soloapp.model.ActivityComment;
import ednardo.api.soloapp.model.dto.ActivityCommentDTO;
import ednardo.api.soloapp.service.ActivityCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1/activity-comment")
@RestController
public class ActivityCommentController {
    @Autowired
    ActivityCommentService activityCommentService;

    @GetMapping("/{id}")
    public ResponseEntity getActivityCommentById(@PathVariable Long id) {
        ActivityComment activityComment = activityCommentService.getById(id);

        return ResponseEntity.ok(activityComment);
    }

    @GetMapping("/all/{id}")
    public ResponseEntity getAllCommentsFromActivity(@PathVariable Long id, @PageableDefault(size = 4, page = 0) Pageable pageable) {
        Page<ActivityComment> activityComments = activityCommentService.getAllComments(id, pageable);
        return ResponseEntity.ok(activityComments);
    }

    @PostMapping("/create")
    public ResponseEntity createActivityComment (@RequestBody ActivityCommentDTO activityCommentDTO) {
        activityCommentService.create(activityCommentDTO);

        return new ResponseEntity<>("Activity created", HttpStatus.CREATED);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity updateActivityComment(@PathVariable Long id, @RequestBody ActivityCommentDTO activityCommentDTO) {
        activityCommentService.update(id, activityCommentDTO);

        return new ResponseEntity<>("Comment edited", HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity deleteActivityComment(@PathVariable Long id) {
        activityCommentService.delete(id);

        return new ResponseEntity<>("Comment deleted", HttpStatus.NO_CONTENT);
    }
}
