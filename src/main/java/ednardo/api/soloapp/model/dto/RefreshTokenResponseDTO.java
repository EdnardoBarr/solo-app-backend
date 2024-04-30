package ednardo.api.soloapp.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RefreshTokenResponseDTO extends RefreshTokenRequestDTO {
    private String token;
    private String type = "Bearer";

    public RefreshTokenResponseDTO(String token, String refreshToken) {
        super(refreshToken);
        this.token = token;
    }
}
