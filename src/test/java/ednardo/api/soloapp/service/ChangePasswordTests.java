package ednardo.api.soloapp.service;

import ednardo.api.soloapp.exception.UserValidationException;
import ednardo.api.soloapp.model.User;
import ednardo.api.soloapp.model.dto.ChangePasswordDTO;
import ednardo.api.soloapp.repository.UserRepository;
import ednardo.api.soloapp.service.impl.ChangePasswordServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ChangePasswordTests {
    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    Authentication authentication;

    @InjectMocks
    ChangePasswordServiceImpl changePasswordService;

    private User user;
    private ChangePasswordDTO changePasswordDTO;

    @BeforeEach
    public void setUp() {
        changePasswordDTO = new ChangePasswordDTO("oldPassword", "newPassword", "newPassword");
        user = User.builder().email("ednardobl@gmail.ocom")
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

        when(authentication.getName()).thenReturn("ednardo@gmail.com");
        SecurityContextHolder.setContext(new SecurityContextImpl(authentication));
    }

    @Test
    public void testChangePasswordSuccess() {

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedNewPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        changePasswordService.changePassword(changePasswordDTO);

        Assertions.assertNotNull(user.getPassword());
        assertEquals("encodedNewPassword", user.getPassword());
    }

    @Test
    public void testChangePasswordWhenUserNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(UserValidationException.class, () -> changePasswordService.changePassword(changePasswordDTO));
    }

    @Test
    public void testChangePasswordWhenFieldsAreEmpty() {
        changePasswordDTO.setOldPassword(" ");
        changePasswordDTO.setMatchingPassword(" ");
        changePasswordDTO.setPassword(" ");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        assertThrows(ResponseStatusException.class, () -> changePasswordService.changePassword(changePasswordDTO));
    }

    @Test
    public void testChangePasswordWhenOldPasswordDoesNotMatch() {
        changePasswordDTO.setOldPassword("notOldPassword");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        passwordEncoder.matches(changePasswordDTO.getOldPassword(), user.getPassword());

        assertThrows(ResponseStatusException.class, () -> changePasswordService.changePassword(changePasswordDTO));
    }

    @Test
    public void testChangePasswordWhenNewPasswordsDontMatch() {
        changePasswordDTO.setPassword("123");
        changePasswordDTO.setMatchingPassword("1234");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        passwordEncoder.matches(changePasswordDTO.getPassword(), changePasswordDTO.getMatchingPassword());

        assertThrows(ResponseStatusException.class, () -> changePasswordService.changePassword(changePasswordDTO));
    }
}
