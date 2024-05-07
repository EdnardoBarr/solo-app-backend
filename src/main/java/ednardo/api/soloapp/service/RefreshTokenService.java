package ednardo.api.soloapp.service;

import ednardo.api.soloapp.model.RefreshToken;
import ednardo.api.soloapp.model.dto.RefreshTokenResponseDTO;

import java.security.Principal;

public interface RefreshTokenService {
    RefreshTokenResponseDTO refreshToken(String requestRefreshToken);
    RefreshToken verifyExpiration(RefreshToken token);
    RefreshToken createRefreshToken(String email);
    void deleteRefreshToken(Principal principal);
}
