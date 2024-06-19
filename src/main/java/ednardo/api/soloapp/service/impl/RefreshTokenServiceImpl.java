package ednardo.api.soloapp.service.impl;

import ednardo.api.soloapp.exception.JWTException;
import ednardo.api.soloapp.exception.TokenRefreshException;
import ednardo.api.soloapp.exception.UserNotFoundException;
import ednardo.api.soloapp.model.RefreshToken;
import ednardo.api.soloapp.model.User;
import ednardo.api.soloapp.model.dto.RefreshTokenResponseDTO;
import ednardo.api.soloapp.model.security.JwtUtils;
import ednardo.api.soloapp.model.security.MyUserDetailsService;
import ednardo.api.soloapp.repository.RefreshTokenRepository;
import ednardo.api.soloapp.repository.UserRepository;
import ednardo.api.soloapp.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {
    @Autowired
    MyUserDetailsService userDetailsService;

    private final RefreshTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;

    @Value("${solo.app.jwtRefreshExpirationSec}")
    private Long refreshTokenDurationSec;

    @Override
    public RefreshTokenResponseDTO refreshToken(String requestRefreshToken) {
        return this.findByToken(requestRefreshToken)
                .map(this::verifyExpiration)
                .map(refreshToken -> {

                    String email = refreshToken.getUser().getEmail();
                    UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                    String token = jwtUtils.generateToken(userDetails);

                    refreshToken.setExpirationDate(LocalDateTime.now().plusSeconds(refreshTokenDurationSec));
                    refreshToken.setToken(UUID.randomUUID().toString());
                    this.tokenRepository.save(refreshToken);
                    return new RefreshTokenResponseDTO(token, requestRefreshToken);
                })
                .orElseThrow(() -> new TokenRefreshException(requestRefreshToken, "Token for update not found"));
    }

    public Optional<RefreshToken> findByToken(String token) {
        return this.tokenRepository.findByToken(token);
    }

    @Override
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (!token.getExpirationDate().isAfter(LocalDateTime.now())) {
            this.tokenRepository.delete(token);
            throw new TokenRefreshException(token.getToken(), "Token expired. Please login again");
        }

        return token;
    }

    @Override
    public RefreshToken createRefreshToken(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found"));
        //  RefreshToken refreshToken = tokenRepository.findByUserCode(user.getId()).get();

        if (tokenRepository.findByUserCode(user.getId()).isPresent()) {
            tokenRepository.delete(tokenRepository.findByUserCode(user.getId()).get());
        }

        RefreshToken newRefreshToken = new RefreshToken();

        newRefreshToken.setUser(user);
        newRefreshToken.setUserCode(user.getId());
        newRefreshToken.setExpirationDate(LocalDateTime.now().plusSeconds(refreshTokenDurationSec));
        newRefreshToken.setToken(UUID.randomUUID().toString());

        this.tokenRepository.save(newRefreshToken);

        return newRefreshToken;
    }

    @Override
    public void deleteRefreshToken(Principal principal) {
        RefreshToken refreshToken = tokenRepository.findByUserCode(userRepository.findByEmail(principal.getName()).get().getId()).orElseThrow(() -> new JWTException("Refresh token not found"));
        SecurityContextHolder.clearContext();

        this.tokenRepository.delete(refreshToken);
    }
}