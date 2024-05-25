package ednardo.api.soloapp.service.impl;

import ednardo.api.soloapp.exception.ActivityValidationException;
import ednardo.api.soloapp.exception.UserValidationException;
import ednardo.api.soloapp.model.Activity;
import ednardo.api.soloapp.model.ActivityComment;
import ednardo.api.soloapp.model.LocationActivity;
import ednardo.api.soloapp.model.User;
import ednardo.api.soloapp.model.dto.ActivityCommentDTO;
import ednardo.api.soloapp.model.dto.ActivityDTO;
import ednardo.api.soloapp.model.dto.ActivityFilterDTO;
import ednardo.api.soloapp.repository.ActivityCommentRepository;
import ednardo.api.soloapp.repository.ActivityRepository;
import ednardo.api.soloapp.repository.UserRepository;
import ednardo.api.soloapp.service.ActivityCommentService;
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

import static java.util.Objects.nonNull;

@Service
public class ActivityCommentServiceImpl implements ActivityCommentService {
    @Autowired
    ActivityCommentRepository activityCommentRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ActivityRepository activityRepository;

    @Autowired
    EntityManager entityManager;

    @Override
    public ActivityComment getById(Long id) {
        return this.activityCommentRepository.findById(id).orElseThrow(()->new ActivityValidationException("Comment not found"));
    }

    public Long count(Long activityId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<ActivityComment> root = cq.from(ActivityComment.class);
        cq.select(cb.count(root));
        List<Predicate> predicates = this.buildPredicates(activityId, cb, root);
        cq.where(predicates.toArray(Predicate[]::new));

        return entityManager.createQuery(cq).getSingleResult();
    }

    private List<Predicate> buildPredicates(Long activityId, CriteriaBuilder cb, Root<ActivityComment> root) {
        List<Predicate> predicates = new ArrayList<>();
        if (nonNull(activityId)) {
            Expression<String> expression = root.join("activity").get("id");
            Predicate activityComment = cb.equal(expression, activityId);
            predicates.add(activityComment);
        }

        return predicates;
    }

    @Override
    public Page<ActivityComment> getAllComments(Long activityId, Pageable pageable) {
        if (!activityRepository.existsById(activityId)) {
            throw new ActivityValidationException("Activity not found");
        }
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<ActivityComment> cq = cb.createQuery(ActivityComment.class);
        Root<ActivityComment> root = cq.from(ActivityComment.class);
        List<Predicate> predicates = this.buildPredicates(activityId, cb, root);
        cq.where(predicates.toArray(Predicate[]::new));
        cq.orderBy(cb.desc(root.get("createdAt")));

        int offset = pageable.getPageSize() * pageable.getPageNumber();
        TypedQuery<ActivityComment> query = entityManager.createQuery(cq).setMaxResults(pageable.getPageSize()).setFirstResult(offset);

        return new PageImpl<>(query.getResultList(), pageable, this.count(activityId));
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
