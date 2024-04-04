package ednardo.api.soloapp.service.impl;

import ednardo.api.soloapp.model.Activity;
import ednardo.api.soloapp.repository.ActivityRepository;
import ednardo.api.soloapp.service.ActivityService;
import ednardo.api.soloapp.service.LocationActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ActivityServiceImpl implements ActivityService {
    @Autowired
    ActivityRepository activityRepository;

    @Autowired
    LocationActivityService locationActivityService;

    public Optional<Activity> getById(Long id){
        return this.activityRepository.findById(id);
    }

    public Activity create(Activity activity) {
        return this.activityRepository.save(activity);
    }

    public void update(Long id, Activity activity) {

    }

}
