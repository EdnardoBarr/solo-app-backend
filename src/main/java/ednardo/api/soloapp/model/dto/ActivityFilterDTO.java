package ednardo.api.soloapp.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
public class ActivityFilterDTO {
    private String title;
    private String category;
    private String city;
    private LocalDate initialStartDate;
    private LocalDate endStartDate;
}
