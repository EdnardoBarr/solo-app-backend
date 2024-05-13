package ednardo.api.soloapp.service.impl;

import ednardo.api.soloapp.exception.UserValidationException;
import ednardo.api.soloapp.model.User;
import ednardo.api.soloapp.model.dto.ChangePasswordDTO;
import ednardo.api.soloapp.repository.UserRepository;
import ednardo.api.soloapp.service.ChangePasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.springframework.util.StringUtils.hasText;

public class ChangePasswordServiceImpl implements ChangePasswordService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    public void changePassword(ChangePasswordDTO changePasswordDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow(()->new UserValidationException("User not found"));

        if (!hasText(changePasswordDTO.getOldPassword()) || !hasText(changePasswordDTO.getPassword()) || !hasText(changePasswordDTO.getMatchingPassword())) {
            throw new UserValidationException("Fill out the required fields.");
        }
        if (!changePasswordDTO.getOldPassword().equals(passwordEncoder.encode(user.getPassword()))) {
            throw new UserValidationException("Wrong password");
        }
        if (!changePasswordDTO.getPassword().equals(changePasswordDTO.getMatchingPassword())) {
            throw new UserValidationException("Passwords must match");
        }

        user.setPassword(passwordEncoder.encode(changePasswordDTO.getPassword()));
        User loggedUser = this.userRepository.save(user);

        Authentication newAuth = new UsernamePasswordAuthenticationToken(loggedUser, loggedUser.getPassword());
        SecurityContextHolder.getContext().setAuthentication(newAuth);
    }
}
