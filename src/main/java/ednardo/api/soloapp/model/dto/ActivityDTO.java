package ednardo.api.soloapp.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ednardo.api.soloapp.enums.ActivityCategory;
import ednardo.api.soloapp.model.ActivityMember;
import ednardo.api.soloapp.model.LocationActivity;
import ednardo.api.soloapp.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ActivityDTO {
    private Long ownerId;
    private String address;
    private String city;
    private String country;
    private String cooords;
    private String title;
    private String description;
    private int maxParticipants;
    @Enumerated(EnumType.STRING)
    private ActivityCategory category;
    private LocalDateTime startsAt;
    private LocalDateTime finishesAt;
    private Boolean active;
}
