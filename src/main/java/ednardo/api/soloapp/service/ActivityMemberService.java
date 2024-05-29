package ednardo.api.soloapp.service;

import ednardo.api.soloapp.model.ActivityMember;
import ednardo.api.soloapp.model.dto.JoinActivityRequestDTO;

public interface ActivityMemberService {
    ActivityMember getById(Long id);
    void requestToJoin(JoinActivityRequestDTO joinActivityRequestDTO);
    void update(Long id, ActivityMember activityMember);
}
