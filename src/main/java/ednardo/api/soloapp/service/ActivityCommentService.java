package ednardo.api.soloapp.service;

import ednardo.api.soloapp.model.ActivityComment;
import ednardo.api.soloapp.model.dto.ActivityCommentDTO;

import java.util.List;

public interface ActivityCommentService {
    ActivityComment getById(Long id);
    void create(ActivityCommentDTO activityCommentDTO);
    void update(Long id, ActivityCommentDTO activityCommentDTO);
    void delete(Long id);

    List<ActivityComment> getAllComments(Long id);
}
