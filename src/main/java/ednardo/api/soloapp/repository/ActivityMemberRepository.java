package ednardo.api.soloapp.repository;

import ednardo.api.soloapp.model.ActivityMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityMemberRepository extends JpaRepository<ActivityMember, Long> {
}
