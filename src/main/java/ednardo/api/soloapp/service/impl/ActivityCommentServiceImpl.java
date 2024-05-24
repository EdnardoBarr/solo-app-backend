package ednardo.api.soloapp.service.impl;

import ednardo.api.soloapp.exception.ActivityValidationException;
import ednardo.api.soloapp.exception.UserValidationException;
import ednardo.api.soloapp.model.Activity;
import ednardo.api.soloapp.model.ActivityComment;
import ednardo.api.soloapp.model.User;
import ednardo.api.soloapp.model.dto.ActivityCommentDTO;
import ednardo.api.soloapp.model.dto.ActivityDTO;
import ednardo.api.soloapp.repository.ActivityCommentRepository;
import ednardo.api.soloapp.repository.ActivityRepository;
import ednardo.api.soloapp.repository.UserRepository;
import ednardo.api.soloapp.service.ActivityCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ActivityCommentServiceImpl implements ActivityCommentService {
    @Autowired
    ActivityCommentRepository activityCommentRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ActivityRepository activityRepository;

    @Override
    public ActivityComment getById(Long id) {
        return this.activityCommentRepository.findById(id).orElseThrow(()->new ActivityValidationException("Comment not found"));
    }

    @Override
    public List<ActivityComment> getAllComments(Long id) {
        if (!activityRepository.existsById(id)) {
            throw new ActivityValidationException("Activity not found");
        }
        return this.activityCommentRepository.findAllByActivityId(id);
    }

    @Override
    public void create(ActivityCommentDTO activityCommentDTO) {
        Activity activity = activityRepository.findById(activityCommentDTO.getActivityId()).orElseThrow(()->new ActivityValidationException("Activity not found"));
        User user = userRepository.findById(activityCommentDTO.getUserId()).orElseThrow(()-> new  UserValidationException("User not found"));

        ActivityComment activityComment = ActivityComment.builder()
                .activity(activity)
                .user(user)
                .comment(activityCommentDTO.getComment())
                .createdAt(LocalDateTime.now())
                .build();

        this.activityCommentRepository.save(activityComment);
    }

    @Override
    public void update(Long id, ActivityCommentDTO activityCommentDTO) {
        ActivityComment activityComment = activityCommentRepository.findById(id).orElseThrow(()->new ActivityValidationException("Comment not found"));

        activityComment.setComment(activityCommentDTO.getComment());
        activityComment.setUpdatedAt(LocalDateTime.now());

        this.activityCommentRepository.save(activityComment);
    }

    @Override
    public void delete(Long id) {
        ActivityComment activityComment = activityCommentRepository.findById(id).orElseThrow(()->new ActivityValidationException("Comment not found"));

        this.activityCommentRepository.delete(activityComment);
    }

}
