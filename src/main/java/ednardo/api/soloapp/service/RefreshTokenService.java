package ednardo.api.soloapp.service;

import ednardo.api.soloapp.model.RefreshToken;
import ednardo.api.soloapp.model.dto.RefreshTokenResponseDTO;

public interface RefreshTokenService {
    RefreshTokenResponseDTO refreshToken(String requestRefreshToken);
    RefreshToken verifyExpiration(RefreshToken token);
    RefreshToken createRefreshToken(String email);
}
