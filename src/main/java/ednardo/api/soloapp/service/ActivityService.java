package ednardo.api.soloapp.service;

import ednardo.api.soloapp.model.Activity;

import java.util.Optional;

public interface ActivityService {
    Optional<Activity> getById(Long id);
    Activity create(Activity activity);
    void update(Long id, Activity activity);
    void deleteById(Long id);

}
