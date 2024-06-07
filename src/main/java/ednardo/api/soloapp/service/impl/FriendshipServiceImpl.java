package ednardo.api.soloapp.service.impl;

import ednardo.api.soloapp.enums.FriendshipStatus;

import ednardo.api.soloapp.exception.FriendshipException;
import ednardo.api.soloapp.exception.UserValidationException;
import ednardo.api.soloapp.model.*;
import ednardo.api.soloapp.model.dto.ActivityFilterDTO;
import ednardo.api.soloapp.model.dto.RequestFriendshipDTO;
import ednardo.api.soloapp.repository.FriendshipRepository;
import ednardo.api.soloapp.repository.UserRepository;
import ednardo.api.soloapp.service.FriendshipService;
import ednardo.api.soloapp.service.UserService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

import static java.util.Objects.nonNull;

@Service
public class FriendshipServiceImpl implements FriendshipService {
    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    EntityManager entityManager;
    @Autowired
    FriendshipRepository friendshipRepository;

    @Override
    public Optional<Friendship> findFriendship(User userFrom, User userTo) {
        return this.friendshipRepository.findFriendshipByUsers(userFrom, userTo);
    }

    @Override
    public Optional<Friendship> getById(Long id) {
        return this.friendshipRepository.findById(id);
    }

    @Override
    @Transactional
    public Friendship requestFriendship(RequestFriendshipDTO requestFriendshipDTO) {
        User newUserFrom = userService.getById(requestFriendshipDTO.getFromId());
        User newUserTo = userService.getById(requestFriendshipDTO.getToId());

        Optional<Friendship> existingFriendship = this.friendshipRepository.findFriendshipByUsers(newUserFrom, newUserTo);

        if (existingFriendship.isPresent()) {
            FriendshipStatus currentStatus = existingFriendship.get().getStatus();

            switch (currentStatus) {
                case FRIENDSHIP_PENDING:
                    throw new FriendshipException("Friendship request is pending");
                case FRIENDSHIP_ACCEPTED:
                    throw new FriendshipException("Friendship already exists");
                case FRIENDSHIP_BLOCKED:
                    throw new FriendshipException("Friendship is blocked");
                case FRIENDSHIP_DECLINED:
                case FRIENDSHIP_REMOVED:
                    Friendship friendship = existingFriendship.get();
                    friendship.setFrom(newUserFrom);
                    friendship.setTo(newUserTo);
                    friendship.setStatus(FriendshipStatus.FRIENDSHIP_PENDING);
                    friendship.setUpdatedAt(LocalDateTime.now());
                    return friendshipRepository.save(friendship);
                default:
                    throw new FriendshipException("Friendship request has failed");
            }
        }

        Friendship newFriendship = Friendship.builder()
                .from(newUserFrom)
                .to(newUserTo)
                .status(FriendshipStatus.FRIENDSHIP_PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(null).build();

        return this.friendshipRepository.save(newFriendship);
    }

    @Override
    @Transactional
    public void updateFriendship (RequestFriendshipDTO requestFriendshipDTO) {
        User newUserFrom = userService.getById(requestFriendshipDTO.getFromId());
        User newUserTo = userService.getById(requestFriendshipDTO.getToId());

        Optional<Friendship> existingFriendship = this.friendshipRepository.findFriendshipByUsers(newUserFrom, newUserTo);

        if (existingFriendship.isPresent()) {
            FriendshipStatus status = requestFriendshipDTO.getStatus();

            existingFriendship.get().setStatus(status);
            existingFriendship.get().setUpdatedAt(LocalDateTime.now());
        } else {
            throw new FriendshipException("An error has occured while processing the request");
        }

        this.friendshipRepository.save(existingFriendship.get());
    }

    @Override
    public String getStatus(RequestFriendshipDTO requestFriendshipDTO) {
        User newUserFrom = userService.getById(requestFriendshipDTO.getFromId());
        User newUserTo = userService.getById(requestFriendshipDTO.getToId());

        Optional<Friendship> existingFriendship = this.friendshipRepository.findFriendshipByUsers(newUserFrom, newUserTo);

        try {
            if (existingFriendship.isPresent()) {
                return existingFriendship.get().getStatus().toString();
            } else {
                return "FRIENDSHIP_AVAILABLE";
            }
        } catch (Exception exception) {
            throw new FriendshipException("An error has occurred while processing the request");
        }
    }

//    public Long count(ActivityFilterDTO activityFilterDTO) {
//        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
//        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
//        Root<Activity> activityRoot = cq.from(Activity.class);
//        cq.select(cb.count(activityRoot));
//        List<Predicate> predicates = this.buildPredicates(activityFilterDTO, cb, activityRoot);
//        cq.where(predicates.toArray(Predicate[]::new));
//        return entityManager.createQuery(cq).getSingleResult();
//    }
//
//    private List<Predicate> buildPredicates(Long userTo, CriteriaBuilder cb, Root<Friendship> root) {
//        List<Predicate> predicates = new ArrayList<>();
//        if (nonNull(activityFilterDTO.getTitle())) {
//            Expression<String> upper = cb.upper(activityRoot.get("title"));
//            Predicate activity = cb.like(upper, "%" + activityFilterDTO.getTitle().toUpperCase() + "%");
//            predicates.add(activity);
//        }
//        if (nonNull(activityFilterDTO.getCity())) {
//            Join<Activity, LocationActivity> locationJoin = activityRoot.join("location");
//            Expression<String> upper = cb.upper(locationJoin.get("city"));
//            Predicate cityPredicate = cb.like(upper, "%" + activityFilterDTO.getCity().toUpperCase() + "%");
//            predicates.add(cityPredicate);
//        }
//        if (nonNull(activityFilterDTO.getCategory())) {
//            Expression<String> upper = cb.upper(activityRoot.get("category"));
//            Predicate activity = cb.like(upper, "%" + activityFilterDTO.getCategory().name().toUpperCase() + "%");
//            predicates.add(activity);
//        }
//        if (nonNull(activityFilterDTO.getStatus())) {
//            String status = activityFilterDTO.getStatus().toString();
//            User user = userService.getById(activityFilterDTO.getUserId());
//            if (status == "MEMBER_OWNER") {
//                Predicate activity = cb.equal(activityRoot.get("owner"), user);
//                predicates.add(activity);
//            } else {
//                Join<Activity, ActivityMember> activityMemberJoin = activityRoot.join("member");
//                Expression<String> upper = cb.upper(activityMemberJoin.get("status"));
//                Predicate statusPredicate = cb.like(upper, "%" + activityFilterDTO.getStatus().toString().toUpperCase() + "%");
//                predicates.add(statusPredicate);
//            }
//        }
//        if (nonNull(activityFilterDTO.getInitialStartDate())) {
//            Predicate activity = cb.greaterThanOrEqualTo(activityRoot.get("startsAt"), activityFilterDTO.getInitialStartDate());
//            predicates.add(activity);
//        }
//        if (nonNull(activityFilterDTO.getEndStartDate())) {
//            Predicate activity = cb.lessThanOrEqualTo(activityRoot.get("startsAt"), activityFilterDTO.getEndStartDate());
//            predicates.add(activity);
//        }
//
//        return predicates;
//    }

    @Override
    public Page<User> getFriendshipsPending(Long userTo, Pageable pageable) {
        if (!userRepository.existsById(userTo)) {
            throw new UserValidationException("User not found");

        }

        Page<User> users = this.friendshipRepository.getPendingFriendshipsByUserId(userTo, PageRequest.of(pageable.getPageNumber(), pageable.getPageSize()));


//        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
//        CriteriaQuery<Friendship> cq = cb.createQuery(Friendship.class);
//        Root<Friendship> root = cq.from(Friendship.class);
//        List<Predicate> predicates = this.buildPredicates(userTo, cb, root);
//
//        cq.where(predicates.toArray(Predicate[]::new));
        int offset = pageable.getPageSize() * pageable.getPageNumber();
//        TypedQuery<Friendship> query = friendshipRepository.getPendingFriendshipsByUserId(userTo).get().setMaxResults(pageable.getPageSize()).setFirstResult(offset);
//
      //  Pageable topEight = new PageRequest(0, 8);
      //  return new PageImpl<>(users, PageRequest.of(0, 8));
        return users;
    }

    @Override
    public Page<User> getFriendshipsAccepted(Long userId, Pageable pageable) {
        Page<User> friendsToPage = friendshipRepository.findFriendsToByUserId(userId, pageable);
        Page<User> friendsFromPage = friendshipRepository.findFriendsFromByUserId(userId, pageable);

        Set<User> allFriendsSet = new HashSet<>();
        allFriendsSet.addAll(friendsToPage.getContent());
        allFriendsSet.addAll(friendsFromPage.getContent());

        List<User> allFriendsList = new ArrayList<>(allFriendsSet);

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allFriendsList.size());
        Page<User> page = new PageImpl<>(allFriendsList.subList(start, end), pageable, allFriendsList.size());

        return page;
    }

}
