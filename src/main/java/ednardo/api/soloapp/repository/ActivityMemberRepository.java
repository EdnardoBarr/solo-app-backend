package ednardo.api.soloapp.repository;

import ednardo.api.soloapp.model.ActivityMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ActivityMemberRepository extends JpaRepository<ActivityMember, Long> {
    boolean existsByActivityIdAndMemberId(Long activityId, Long memberId);

    Optional<ActivityMember> findByActivityIdAndMemberId(Long activityId, Long memberId);


}
