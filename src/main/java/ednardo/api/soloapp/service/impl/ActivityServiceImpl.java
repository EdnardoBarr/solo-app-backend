package ednardo.api.soloapp.service.impl;

import ednardo.api.soloapp.exception.ActivityValidationException;
import ednardo.api.soloapp.model.Activity;
import ednardo.api.soloapp.repository.ActivityRepository;
import ednardo.api.soloapp.service.ActivityService;
import ednardo.api.soloapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ActivityServiceImpl implements ActivityService {
    @Autowired
    UserService userService;

    @Autowired
    ActivityRepository activityRepository;

    @Override
    public Optional<Activity> getById(Long id){
        return this.activityRepository.findById(id);
    }

    @Override
    public Activity create(Activity activity) {
        userService.getById(activity.getOwner().getId());

        Activity newActivity = Activity.builder()
                .owner(activity.getOwner())
                .title(activity.getTitle())
                .description(activity.getDescription())
                .mediaLocation(activity.getMediaLocation())
                .maxParticipants(activity.getMaxParticipants())
                .category(activity.getCategory())
                .startsAt(activity.getStartsAt())
                .finishesAt(activity.getFinishesAt())
                .createdAt(activity.getCreatedAt())
                .active(true)
                .location(activity.getLocation())
                .build();

        return this.activityRepository.save(newActivity);
    }

    @Override
    public void update(Long id, Activity activity) {
        Activity newActivity = activityRepository.findById(id).orElseThrow(() -> new ActivityValidationException("Activity not found"));

        newActivity.setTitle(activity.getTitle());
        newActivity.setDescription(activity.getDescription());
        newActivity.setMediaLocation(activity.getMediaLocation());
        newActivity.setMaxParticipants(activity.getMaxParticipants());
        newActivity.setCategory(activity.getCategory());
        newActivity.setStartsAt(activity.getStartsAt());
        newActivity.setFinishesAt(activity.getFinishesAt());
        newActivity.setCreatedAt(activity.getCreatedAt());
        newActivity.setActive(activity.getActive());
        newActivity.setLocation(activity.getLocation());

        this.activityRepository.save(newActivity);
    }

    @Override
    public void deleteById(Long id) {
        Activity activity = activityRepository.findById(id).orElseThrow(() -> new ActivityValidationException("Activity not found"));

        activity.setActive(false);
        activityRepository.save(activity);
    }



}
