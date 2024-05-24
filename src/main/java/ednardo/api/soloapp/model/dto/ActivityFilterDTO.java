package ednardo.api.soloapp.model.dto;

import ednardo.api.soloapp.enums.ActivityCategory;
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
    private String title;
    @Enumerated(EnumType.STRING)
    private ActivityCategory category;
    private String city;
    private LocalDate initialStartDate;
    private LocalDate endStartDate;
}
