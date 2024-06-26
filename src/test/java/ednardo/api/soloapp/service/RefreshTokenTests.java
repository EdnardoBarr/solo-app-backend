package ednardo.api.soloapp.service;

import ednardo.api.soloapp.exception.TokenRefreshException;
import ednardo.api.soloapp.exception.UserNotFoundException;
import ednardo.api.soloapp.model.RefreshToken;
import ednardo.api.soloapp.model.User;
import ednardo.api.soloapp.util.JwtUtils;
import ednardo.api.soloapp.service.impl.MyUserDetailsService;
import ednardo.api.soloapp.repository.RefreshTokenRepository;
import ednardo.api.soloapp.repository.UserRepository;
import ednardo.api.soloapp.service.impl.RefreshTokenServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RefreshTokenTests {
    @Mock
    MyUserDetailsService userDetailsService;

    @Mock
    RefreshTokenRepository refreshTokenRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    JwtUtils jwtUtils;

    @InjectMocks
    RefreshTokenServiceImpl refreshTokenService;

    private User user;
    private Long refreshTokenDurationSec = 4000L;

    @BeforeEach
    public void setUp() {

        user = User.builder()
                .id(1L)
                .email("ednardo@gmail.ocom")
                .password("encodedOldPassword")
                .givenName("Ednardo")
                .surname("Barreto")
                .country("Brazil")
                .city("Rio de Janeiro")
                .dateOfBirth("06/09/1989")
                .bio("I am an easygoing person and I like doing sports.")
                .active(true)
                .pictureLocation("http://")
                .interests(List.of())
                .roles(List.of()).build();
    }

    @Test
    public void testCreateNonExistingRefreshToken() {
        String email = "ednardobl@gmail.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(refreshTokenRepository.findByUserCode(user.getId())).thenReturn(Optional.empty());
        ReflectionTestUtils.setField(refreshTokenService, "refreshTokenDurationSec", 3600L);

        RefreshToken token = refreshTokenService.createRefreshToken(email);

        assertNotNull(token);
        Assertions.assertEquals(user, token.getUser());
        Assertions.assertTrue(token.getExpirationDate().isAfter(LocalDateTime.now()));
    }

    @Test
    public void testCreateExistingRefreshToken() {
        String email = "ednardobl@gmail.com";
        RefreshToken refreshToken = RefreshToken.builder()
                .token("token")
                .expirationDate(LocalDateTime.now())
                .userCode(1L)
                .user(user).build();
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(refreshTokenRepository.findByUserCode(user.getId())).thenReturn(Optional.of(refreshToken));
        ReflectionTestUtils.setField(refreshTokenService, "refreshTokenDurationSec", 3600L);

        RefreshToken token = refreshTokenService.createRefreshToken(email);

        assertNotNull(token);
        Assertions.assertEquals(user, token.getUser());
        Assertions.assertTrue(token.getExpirationDate().isAfter(LocalDateTime.now()));
    }

    @Test
    public void testCreateRefreshTokenUserNotFound() {
        String email = "ednardobl@gmail.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        Assertions.assertThrows(UserNotFoundException.class, () -> refreshTokenService.createRefreshToken(email));
        verifyNoMoreInteractions(refreshTokenRepository);
    }

    @Test
    public void testVerifyTokenExpirationNotExpired() {
        RefreshToken refreshToken = RefreshToken.builder()
                .token("token")
                .expirationDate(LocalDateTime.now().plusSeconds(4000))
                .userCode(1L)
                .user(user).build();

        RefreshToken token = refreshTokenService.verifyExpiration(refreshToken);

        assertNotNull(token);
        Assertions.assertTrue(token.getExpirationDate().isAfter(LocalDateTime.now()));
    }

    @Test
    public void testVerifyTokenExpirationExpired() {
        RefreshToken refreshToken = RefreshToken.builder()
                .token("token")
                .expirationDate(LocalDateTime.now().minusSeconds(4000))
                .userCode(1L)
                .user(user).build();

        Assertions.assertThrows(TokenRefreshException.class, () -> refreshTokenService.verifyExpiration(refreshToken));
    }

//    @Test
//    public void testRefreshTokenNotExpired() {
//        String refreshTokenString = "refreshToken";
//        RefreshToken refreshToken = RefreshToken.builder()
//                .token("token")
//                .expirationDate(LocalDateTime.now().plusSeconds(4000))
//                .userCode(1L)
//                .user(user).build();
//        UserDetails userDetails = new UserDetailsDTO(user);
//        when(refreshTokenRepository.findByToken(refreshTokenString)).thenReturn(Optional.of(refreshToken));
//      //  when(refreshTokenService.verifyExpiration(refreshToken)).thenReturn(refreshToken);
//        when(userDetailsService.loadUserByUsername("ednardobl@gmail.com")).thenReturn(userDetails);
//        when(jwtUtils.generateToken(any())).thenReturn("newJwtToken");
//
//        ReflectionTestUtils.setField(refreshTokenService, "refreshTokenDurationSec", 3600L);
//
//        RefreshTokenResponseDTO refreshTokenResponseDTO = refreshTokenService.refreshToken(refreshTokenString);
//        assertNotNull(refreshTokenResponseDTO);
//    }

}
