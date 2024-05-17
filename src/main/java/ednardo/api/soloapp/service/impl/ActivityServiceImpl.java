package ednardo.api.soloapp.service.impl;

import ednardo.api.soloapp.exception.ActivityValidationException;
import ednardo.api.soloapp.model.Activity;
import ednardo.api.soloapp.model.LocationActivity;
import ednardo.api.soloapp.model.User;
import ednardo.api.soloapp.model.dto.ActivityDTO;
import ednardo.api.soloapp.model.dto.LocationActivityDTO;
import ednardo.api.soloapp.repository.ActivityRepository;
import ednardo.api.soloapp.service.ActivityService;
import ednardo.api.soloapp.service.LocationActivityService;
import ednardo.api.soloapp.service.UserService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ActivityServiceImpl implements ActivityService {
    @Autowired
    UserService userService;

    @Autowired
    LocationActivityService locationActivityService;

    @Autowired
    ActivityRepository activityRepository;

    @Autowired
    EntityManager entityManager;

    @Override
    public Optional<Activity> getById(Long id){
        return this.activityRepository.findById(id);
    }

    @Override
    public Activity create(ActivityDTO activityDTO) {
        User user = userService.getById(activityDTO.getOwnerId());
        LocationActivityDTO locationActivityDTO = new LocationActivityDTO(activityDTO.getAddress(), activityDTO.getCity(), activityDTO.getCountry(), activityDTO.getCooords());
        LocationActivity locationActivity = this.locationActivityService.create(locationActivityDTO);

        Activity newActivity = Activity.builder()
                .owner(user)
                .title(activityDTO.getTitle())
                .description(activityDTO.getDescription())
                .mediaLocation("http://")
                .maxParticipants(activityDTO.getMaxParticipants())
                .category(activityDTO.getCategory())
                .startsAt(activityDTO.getStartsAt())
                .finishesAt(activityDTO.getFinishesAt())
                .location(locationActivity)
                .createdAt(LocalDateTime.now())
                .active(activityDTO.getActive())
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

    @Override
    public List<Activity> getAll() {
        List<Activity> allAcitvities = activityRepository.findAll();

        return allAcitvities;
    }

    @Override
    public Page<Activity> getAll(Pageable pageable) {
       Page<Activity> allActivities = this.activityRepository.findAll(pageable);

       return allActivities;
    }
}
