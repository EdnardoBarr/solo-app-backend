package ednardo.api.soloapp.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActivityMemberRequestDTO {
    private Long activityId;
    private Long userId;
}
