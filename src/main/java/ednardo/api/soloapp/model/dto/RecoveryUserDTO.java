package ednardo.api.soloapp.model.dto;

import ednardo.api.soloapp.model.Role;
import lombok.Data;

import java.util.Collection;

@Data
public class RecoveryUserDTO {
    private Long id;
    private String email;
    private String givenName;
    private String surname;
    private String country;
    private String city;
    private String dateOfBirth;
    private String bio;
    private String pictureLocation;
    private Collection<Role> roles;
}
