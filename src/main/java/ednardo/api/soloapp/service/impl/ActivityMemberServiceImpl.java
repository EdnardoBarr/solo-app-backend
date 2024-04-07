package ednardo.api.soloapp.service.impl;

import ednardo.api.soloapp.enums.ActivityMemberPrivilege;
import ednardo.api.soloapp.enums.ActivityStatus;
import ednardo.api.soloapp.exception.ActivityMemberException;
import ednardo.api.soloapp.exception.ActivityValidationException;
import ednardo.api.soloapp.model.Activity;
import ednardo.api.soloapp.model.ActivityMember;
import ednardo.api.soloapp.model.User;
import ednardo.api.soloapp.repository.ActivityMemberRepository;
import ednardo.api.soloapp.repository.ActivityRepository;
import ednardo.api.soloapp.service.ActivityMemberService;
import ednardo.api.soloapp.service.ActivityService;
import ednardo.api.soloapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

public class ActivityMemberServiceImpl implements ActivityMemberService {
    @Autowired
    ActivityService activityService;

    @Autowired
    UserService userService;

    @Autowired
    ActivityMemberRepository activityMemberRepository;

    @Override
    public ActivityMember getById(Long id) {
        return this.activityMemberRepository.findById(id).orElseThrow(()-> new ActivityMemberException("Not found"));
    }

    @Override
    public ActivityMember requestToJoin (ActivityMember activityMember) {
       Activity activity = activityService.getById(activityMember.getActivity().getId()).orElseThrow(()-> new ActivityValidationException("Activity not found"));
       User user = userService.getById(activityMember.getUser().getId());

       if (activity.getOwner().getId() ==  user.getId()) {
           throw new ActivityValidationException("User is the owner of the activity");
       }

       ActivityMember newActivityMember = ActivityMember.builder()
               .user(user)
               .activity(activity)
               .status(ActivityStatus.MEMBER_PENDING)
               .privilege(ActivityMemberPrivilege.PRIVILEGE_DEFAULT)
               .createdAt(LocalDateTime.now())
               .updatedAt(null)
               .build();

       return this.activityMemberRepository.save(newActivityMember);
    }

    @Override
    public void update(Long id, ActivityMember activityMember) {
        ActivityMember newActivityMember = activityMemberRepository.findById(id).orElseThrow(()-> new ActivityMemberException("User didn't request to join the activity"));

        newActivityMember.setStatus(activityMember.getStatus());
        newActivityMember.setPrivilege(activityMember.getPrivilege());
        newActivityMember.setUpdatedAt(LocalDateTime.now());

        this.activityMemberRepository.save(newActivityMember);
    }

}
