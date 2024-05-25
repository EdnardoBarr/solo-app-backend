package ednardo.api.soloapp.service;

import ednardo.api.soloapp.model.Activity;
import ednardo.api.soloapp.model.ActivityComment;
import ednardo.api.soloapp.model.dto.ActivityCommentDTO;
import ednardo.api.soloapp.model.dto.ActivityFilterDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ActivityCommentService {
    ActivityComment getById(Long id);
    void create(ActivityCommentDTO activityCommentDTO);
    void update(Long id, ActivityCommentDTO activityCommentDTO);
    void delete(Long id);

    Page<ActivityComment> getAllComments(Long activityId, Pageable pageable);

}
