package ednardo.api.soloapp.service;

import ednardo.api.soloapp.model.Activity;
import ednardo.api.soloapp.model.ActivityMember;
import ednardo.api.soloapp.model.dto.ActivityFilterDTO;
import ednardo.api.soloapp.model.dto.ActivityMemberRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ActivityMemberService {
    ActivityMember getById(Long id);
    void requestToJoin(ActivityMemberRequestDTO activityMemberRequestDTO);
    void update(Long id, ActivityMember activityMember);
    String getStatus(ActivityMemberRequestDTO activityMemberRequestDTO);

}
