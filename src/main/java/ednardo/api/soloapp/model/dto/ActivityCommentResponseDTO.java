package ednardo.api.soloapp.model.dto;

import java.time.LocalDateTime;

public class ActivityCommentResponseDTO {
    private Long commentId;
    private Long userId;
    private Long activityId;
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String comment;
}
