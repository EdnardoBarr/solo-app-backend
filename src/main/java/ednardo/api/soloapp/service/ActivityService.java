package ednardo.api.soloapp.service;

import ednardo.api.soloapp.model.Activity;
import ednardo.api.soloapp.model.User;
import ednardo.api.soloapp.model.dto.ActivityDTO;
import ednardo.api.soloapp.model.dto.ActivityFilterDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ActivityService {
    Optional<Activity> getById(Long id);
    Activity create(ActivityDTO activityDTO);
    void update(Long id, Activity activity);
    void deleteById(Long id);
    void addParticipant(Long userId, ActivityDTO activityDTO);
    void declineParticipant(Long userId, ActivityDTO activityDTO);
    void removeParticipant(Long userId, ActivityDTO activityDTO);
    void dropParticipant(Long userId, ActivityDTO activityDTO);
    List<Activity> getAll();
    Page<Activity> getAll(ActivityFilterDTO activityFilterDTO, Pageable pageable);

}
