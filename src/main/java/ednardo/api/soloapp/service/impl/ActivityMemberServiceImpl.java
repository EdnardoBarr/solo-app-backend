package ednardo.api.soloapp.service.impl;

import ednardo.api.soloapp.enums.ActivityMemberPrivilege;
import ednardo.api.soloapp.enums.ActivityStatus;
import ednardo.api.soloapp.exception.ActivityMemberException;
import ednardo.api.soloapp.exception.ActivityValidationException;
import ednardo.api.soloapp.model.Activity;
import ednardo.api.soloapp.model.ActivityMember;
import ednardo.api.soloapp.model.LocationActivity;
import ednardo.api.soloapp.model.User;
import ednardo.api.soloapp.model.dto.ActivityFilterDTO;
import ednardo.api.soloapp.model.dto.ActivityMemberRequestDTO;
import ednardo.api.soloapp.repository.ActivityMemberRepository;
import ednardo.api.soloapp.repository.ActivityRepository;
import ednardo.api.soloapp.service.ActivityMemberService;
import ednardo.api.soloapp.service.ActivityService;
import ednardo.api.soloapp.service.UserService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.nonNull;

@Service
public class ActivityMemberServiceImpl implements ActivityMemberService {
    @Autowired
    ActivityService activityService;

    @Autowired
    UserService userService;

    @Autowired
    ActivityMemberRepository activityMemberRepository;

    @Autowired
    ActivityRepository activityRepository;

    @Autowired
    EntityManager entityManager;

    @Override
    public ActivityMember getById(Long id) {
        return this.activityMemberRepository.findById(id).orElseThrow(() -> new ActivityMemberException("Not found"));
    }

    @Override
    public void requestToJoin(ActivityMemberRequestDTO activityMemberRequestDTO) {
        Activity activity = activityService.getById(activityMemberRequestDTO.getActivityId()).orElseThrow(() -> new ActivityValidationException("Activity not found"));
        User user = userService.getById(activityMemberRequestDTO.getUserId());

        if (activity.getOwner().getId().equals(activityMemberRequestDTO.getUserId())) {
            throw new ActivityValidationException("User is the owner of the activity");
        }

        try {
            Optional<ActivityMember> existingActivityMember = activityMemberRepository.findByActivityIdAndMemberId(activityMemberRequestDTO.getActivityId(), activityMemberRequestDTO.getUserId());
            if (existingActivityMember.isPresent()) {
                ActivityStatus currentStatus = existingActivityMember.get().getStatus();

                switch (currentStatus) {
                    case MEMBER_PENDING:
                        throw new ActivityValidationException("Your request to join this activity is already pending.");
                    case MEMBER_ACCEPTED:
                        throw new ActivityValidationException("You are already a member of this activity.");
                    case MEMBER_DECLINED:
                    case MEMBER_REMOVED:
                    case MEMBER_DROPPED:
                        ActivityMember activityMember = existingActivityMember.get();

                        activityMember.setStatus(ActivityStatus.MEMBER_PENDING);
                        activityMember.setUpdatedAt(LocalDateTime.now());

                        activityMemberRepository.save(activityMember);
                    default:
                        throw new ActivityValidationException("An error occurred while processing your request to join the activity.");
                }
            } else {
                ActivityMember newActivityMember = ActivityMember.builder()
                        .member(user)
                        .activity(activity)
                        .status(ActivityStatus.MEMBER_PENDING)
                        .privilege(ActivityMemberPrivilege.PRIVILEGE_DEFAULT)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(null)
                        .build();

                this.activityMemberRepository.save(newActivityMember);
            }
        } catch (Exception exception) {
            throw new ActivityValidationException("An error occurred while processing the request");
        }
    }

    @Override
    public void update(Long id, ActivityMember activityMember) {
        ActivityMember newActivityMember = activityMemberRepository.findById(id).orElseThrow(() -> new ActivityMemberException("User didn't request to join the activity"));

        newActivityMember.setStatus(activityMember.getStatus());
        newActivityMember.setPrivilege(activityMember.getPrivilege());
        newActivityMember.setUpdatedAt(LocalDateTime.now());

        this.activityMemberRepository.save(newActivityMember);
    }

    @Override
    public String getStatus(ActivityMemberRequestDTO activityMemberRequestDTO) {
        Activity activity = activityService.getById(activityMemberRequestDTO.getActivityId()).orElseThrow(() -> new ActivityValidationException("Activity not found"));
        User user = userService.getById(activityMemberRequestDTO.getUserId());

        try {
            Optional<ActivityMember> existingActivityMember = this.activityMemberRepository.findByActivityIdAndMemberId(activityMemberRequestDTO.getActivityId(), activityMemberRequestDTO.getUserId());

            if(existingActivityMember.isPresent()) {
                return existingActivityMember.get().getStatus().toString();
            } else {
                return "MEMBER_AVAILABLE";
            }
        } catch (Exception exception) {
            throw new ActivityValidationException("An error occurred while processing the request");
        }
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
            Predicate activity = cb.like(upper, "%" + activityFilterDTO.getCategory().name().toUpperCase() + "%");
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
    public Page<User> getPending(Long activityId, Pageable pageable) {
        Activity activity = activityRepository.findById(activityId).orElseThrow(() -> new ActivityValidationException("Activity not found"));

        if (!activityMemberRepository.existsByActivityId(activityId)) {
            return Page.empty(pageable);
        }

        List<User> users = activityMemberRepository.findUsersPending(activityId);
        return new PageImpl<>(users, pageable, users.size());
    }

    @Override
    public List<User> getAccepted(Long activityId) {
        Activity activity = activityRepository.findById(activityId).orElseThrow(() -> new ActivityValidationException("Activity not found"));

        if (!activityMemberRepository.existsByActivityId(activityId)) {
            return List.of();
        }

        List<User> users = activityMemberRepository.findUsersAccepted(activityId);
        return users;
    }
}
