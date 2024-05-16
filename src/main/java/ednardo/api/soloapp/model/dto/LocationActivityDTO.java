package ednardo.api.soloapp.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationActivityDTO {
    private String address;
    private String city;
    private String country;
    private String coords;
}
