package ednardo.api.soloapp.repository;

import ednardo.api.soloapp.model.ActivityMember;
import ednardo.api.soloapp.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ActivityMemberRepository extends JpaRepository<ActivityMember, Long> {
    boolean existsByActivityIdAndMemberId(Long activityId, Long memberId);
    boolean existsByActivityId(Long activityId);

    Optional<ActivityMember> findByActivityIdAndMemberId(Long activityId, Long memberId);

    @Query("SELECT a.member FROM ActivityMember a WHERE a.activity.id = :activityId AND a.status = 'MEMBER_PENDING'")
    List<User> findUsersPending(@Param("activityId") Long activityId);

    @Query("SELECT a.member FROM ActivityMember a WHERE a.activity.id = :activityId AND a.status = 'MEMBER_ACCEPTED'")
    List<User> findUsersAccepted(@Param("activityId") Long activityId);




}
