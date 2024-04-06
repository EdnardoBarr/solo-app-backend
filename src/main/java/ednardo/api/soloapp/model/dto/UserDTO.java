package ednardo.api.soloapp.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ednardo.api.soloapp.enums.RoleName;
import ednardo.api.soloapp.model.Role;
import ednardo.api.soloapp.validation.PasswordMatches;
import ednardo.api.soloapp.validation.ValidEmail;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.lang.NonNull;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@PasswordMatches
@Data
public class UserDTO {
    @ValidEmail
    private String email;
    private String givenName;
    private String surname;
    private String country;
    private String city;
    private String dateOfBirth;
    private String password;
    private String matchingPassword;
    private RoleName roleName;
    private boolean active;
    private String pictureLocation;
    private String bio;
}
