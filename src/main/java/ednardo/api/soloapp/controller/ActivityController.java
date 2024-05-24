package ednardo.api.soloapp.controller;

import ednardo.api.soloapp.exception.ActivityValidationException;
import ednardo.api.soloapp.model.Activity;
import ednardo.api.soloapp.model.dto.ActivityDTO;
import ednardo.api.soloapp.model.dto.ActivityFilterDTO;
import ednardo.api.soloapp.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/activity")
@RestController
public class ActivityController {
    @Autowired
    ActivityService activityService;

    @GetMapping("/{id}")
    public ResponseEntity getActivityById(@PathVariable Long id) {
        Activity activity = activityService.getById(id).orElseThrow(()-> new ActivityValidationException("Activity not found"));

        return ResponseEntity.ok(activity);
    }

    @GetMapping
    public ResponseEntity getAllActivities(ActivityFilterDTO activityFilterDTO, @PageableDefault(size = 4, page = 0) Pageable pageable) {
        Page<Activity> activities = this.activityService.getAll(activityFilterDTO, pageable);

        return ResponseEntity.ok(activities);
    }

    @PostMapping("/add")
    public ResponseEntity registerActivity (@RequestBody ActivityDTO activityDTO) {
        activityService.create(activityDTO);

        return new ResponseEntity<>("Activity created", HttpStatus.CREATED);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity updateActivity(@PathVariable Long id, @RequestBody Activity activity) {
        activityService.update(id, activity);

        return new ResponseEntity<>("Activity updated", HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity deleteActivity(@PathVariable Long id) {
        activityService.deleteById(id);

        return new ResponseEntity<>("Activity deleted", HttpStatus.NO_CONTENT);
    }

}
