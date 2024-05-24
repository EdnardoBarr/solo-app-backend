package ednardo.api.soloapp.repository;

import ednardo.api.soloapp.model.ActivityComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityCommentRepository extends JpaRepository<ActivityComment, Long> {
    List<ActivityComment> findAllByActivityId(Long id);
}
