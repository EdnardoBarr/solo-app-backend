package ednardo.api.soloapp.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserFilterDTO {
    private String givenName;
    private String city;
}
