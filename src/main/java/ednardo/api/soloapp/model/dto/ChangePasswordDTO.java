package ednardo.api.soloapp.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChangePasswordDTO {
    private String oldPassword;
    private String password;
    private String matchingPassword;
}
