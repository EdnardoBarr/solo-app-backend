package ednardo.api.soloapp.service.impl;

import ednardo.api.soloapp.enums.ActivityMemberPrivilege;
import ednardo.api.soloapp.enums.ActivityStatus;
import ednardo.api.soloapp.exception.ActivityMemberException;
import ednardo.api.soloapp.exception.ActivityValidationException;
import ednardo.api.soloapp.model.Activity;
import ednardo.api.soloapp.model.ActivityMember;
import ednardo.api.soloapp.model.User;
import ednardo.api.soloapp.model.dto.ActivityMemberRequestDTO;
import ednardo.api.soloapp.repository.ActivityMemberRepository;
import ednardo.api.soloapp.service.ActivityMemberService;
import ednardo.api.soloapp.service.ActivityService;
import ednardo.api.soloapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ActivityMemberServiceImpl implements ActivityMemberService {
    @Autowired
    ActivityService activityService;

    @Autowired
    UserService userService;

    @Autowired
    ActivityMemberRepository activityMemberRepository;

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

}
