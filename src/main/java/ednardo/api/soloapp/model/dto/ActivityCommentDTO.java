package ednardo.api.soloapp.model.dto;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActivityCommentDTO {
    private Long activityId;
    private Long userId;
    private String comment;

    public ActivityCommentDTO(Long activityId, Long userId, String comment) {
        this.activityId = activityId;
        this.userId = userId;
        this.comment = comment;
    }
}
