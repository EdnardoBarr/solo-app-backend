package ednardo.api.soloapp.service.impl;

import ednardo.api.soloapp.exception.ActivityValidationException;
import ednardo.api.soloapp.model.Activity;
import ednardo.api.soloapp.model.LocationActivity;
import ednardo.api.soloapp.model.User;
import ednardo.api.soloapp.model.dto.ActivityDTO;
import ednardo.api.soloapp.model.dto.ActivityFilterDTO;
import ednardo.api.soloapp.model.dto.LocationActivityDTO;
import ednardo.api.soloapp.repository.ActivityRepository;
import ednardo.api.soloapp.service.ActivityService;
import ednardo.api.soloapp.service.LocationActivityService;
import ednardo.api.soloapp.service.UserService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.nonNull;

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
        List<Activity> allActivities = activityRepository.findAll();

        return allActivities;
    }

    public Long count(ActivityFilterDTO activityFilterDTO) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Activity> activityRoot = cq.from(Activity.class);
        cq.select(cb.count(activityRoot));
        List<Predicate> predicates = this.buildPredicates(activityFilterDTO, cb, activityRoot);
        cq.where(predicates.toArray(Predicate[]::new));
        return entityManager.createQuery(cq).getSingleResult();
    }

    private List<Predicate> buildPredicates(ActivityFilterDTO activityFilterDTO, CriteriaBuilder cb, Root<Activity> activityRoot) {
        List<Predicate> predicates = new ArrayList<>();
        if (nonNull(activityFilterDTO.getTitle())) {
            Expression<String> upper = cb.upper(activityRoot.get("title"));
            Predicate activity = cb.like(upper, "%" + activityFilterDTO.getTitle().toUpperCase() + "%");
            predicates.add(activity);
        }
        if (nonNull(activityFilterDTO.getCity())) {
            Join<Activity, LocationActivity> locationJoin = activityRoot.join("location");
            Expression<String> upper = cb.upper(locationJoin.get("city"));
            Predicate cityPredicate = cb.like(upper, "%" + activityFilterDTO.getCity().toUpperCase() + "%");
            predicates.add(cityPredicate);
        }
        if (nonNull(activityFilterDTO.getCategory())) {
            Expression<String> upper = cb.upper(activityRoot.get("category"));
            Predicate activity = cb.like(upper, "%" + activityFilterDTO.getCategory().toUpperCase() + "%");
            predicates.add(activity);
        }
        if (nonNull(activityFilterDTO.getInitialStartDate())) {
            Predicate activity = cb.greaterThanOrEqualTo(activityRoot.get("startsAt"), activityFilterDTO.getInitialStartDate());
            predicates.add(activity);
        }
        if (nonNull(activityFilterDTO.getEndStartDate())) {
            Predicate activity = cb.lessThanOrEqualTo(activityRoot.get("startsAt"), activityFilterDTO.getEndStartDate());
            predicates.add(activity);
        }

        return predicates;
    }

    @Override
    public Page<Activity> getAll(ActivityFilterDTO activityFilterDTO, Pageable pageable) {
       //Page<Activity> allActivities = this.activityRepository.findAll(pageable);
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Activity> cq = cb.createQuery(Activity.class);
        Root<Activity> activity = cq.from(Activity.class);
        List<Predicate> predicates = this.buildPredicates(activityFilterDTO, cb, activity);

        cq.where(predicates.toArray(Predicate[]::new));
        int offset = pageable.getPageSize() * pageable.getPageNumber();
        TypedQuery<Activity> query = entityManager.createQuery(cq).setMaxResults(pageable.getPageSize()).setFirstResult(offset);

        return new PageImpl<>(query.getResultList(), pageable, this.count(activityFilterDTO));
    }


}
