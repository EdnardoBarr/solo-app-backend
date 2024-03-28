package ednardo.api.soloapp.model.dto;

public class RecoveryJwtTokenDTO {
    String token;

    public RecoveryJwtTokenDTO(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
