package ednardo.api.soloapp.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JoinActivityRequestDTO {
    private Long activityId;
    private Long userId;
}
