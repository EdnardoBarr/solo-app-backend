package ednardo.api.soloapp.model.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class UserFilterDTO {
    private Long userId;
    private String givenName;
    private String city;
    private List<ActivityCategoryDTO> interests;
}
