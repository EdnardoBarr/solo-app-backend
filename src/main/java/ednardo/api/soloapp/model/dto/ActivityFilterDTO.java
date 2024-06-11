package ednardo.api.soloapp.model.dto;

import ednardo.api.soloapp.enums.ActivityCategory;
import ednardo.api.soloapp.enums.ActivityStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
public class ActivityFilterDTO {
    private Long ActivityId;
    private Long userId;
    private String title;
    @Enumerated(EnumType.STRING)
    private ActivityCategory category;
    @Enumerated(EnumType.STRING)
    private ActivityStatus status;
    private String city;
    private LocalDate initialStartDate;
    private LocalDate endStartDate;
}
