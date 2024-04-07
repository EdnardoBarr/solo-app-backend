package ednardo.api.soloapp.service;

import ednardo.api.soloapp.model.ActivityMember;

public interface ActivityMemberService {
    ActivityMember getById(Long id);
    ActivityMember requestToJoin(ActivityMember activityMember);
    void update(Long id, ActivityMember activityMember);
}
