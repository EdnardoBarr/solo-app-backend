package ednardo.api.soloapp.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UserFilterDTO {
    private Long userId;
    private String givenName;
    private String city;
    private List<ActivityCategoryDTO> interests;
}
