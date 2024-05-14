package ednardo.api.soloapp.service.impl;

import ednardo.api.soloapp.exception.UserValidationException;
import ednardo.api.soloapp.model.User;
import ednardo.api.soloapp.model.dto.ChangePasswordDTO;
import ednardo.api.soloapp.repository.UserRepository;
import ednardo.api.soloapp.service.ChangePasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.util.StringUtils.hasText;

@Service
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
            throw new ResponseStatusException(HttpStatus.EXPECTATION_FAILED, "Fill out the required fields");
        }
        if (!passwordEncoder.matches(changePasswordDTO.getOldPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.EXPECTATION_FAILED, "Wrong Password");
        }
        if (!changePasswordDTO.getPassword().equals(changePasswordDTO.getMatchingPassword())) {
            throw new ResponseStatusException(HttpStatus.EXPECTATION_FAILED, "Passwords must match");
        }

        user.setPassword(passwordEncoder.encode(changePasswordDTO.getPassword()));
        User loggedUser = this.userRepository.save(user);

        Authentication newAuth = new UsernamePasswordAuthenticationToken(loggedUser, loggedUser.getPassword());
        SecurityContextHolder.getContext().setAuthentication(newAuth);
    }
}
