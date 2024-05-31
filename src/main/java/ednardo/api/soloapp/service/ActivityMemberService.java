package ednardo.api.soloapp.service;

import ednardo.api.soloapp.model.ActivityMember;
import ednardo.api.soloapp.model.dto.ActivityMemberRequestDTO;

public interface ActivityMemberService {
    ActivityMember getById(Long id);
    void requestToJoin(ActivityMemberRequestDTO activityMemberRequestDTO);
    void update(Long id, ActivityMember activityMember);
    String getStatus(ActivityMemberRequestDTO activityMemberRequestDTO);
}
