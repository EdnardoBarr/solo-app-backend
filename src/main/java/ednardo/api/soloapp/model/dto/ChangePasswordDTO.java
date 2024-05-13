package ednardo.api.soloapp.model.dto;

import lombok.Data;

@Data
public class ChangePasswordDTO {
    private String oldPassword;
    private String password;
    private String matchingPassword;
}
