package ednardo.api.soloapp.service;

import ednardo.api.soloapp.model.ActivityMember;

public interface ActivityMemberService {
    ActivityMember getById(Long id);
    void requestToJoin(Long activityId, Long userId);
    void update(Long id, ActivityMember activityMember);
}
