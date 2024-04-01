package ednardo.api.soloapp.service.impl;

import ednardo.api.soloapp.exception.UserAlreadyExistsException;
import ednardo.api.soloapp.exception.UserNotFoundException;
import ednardo.api.soloapp.exception.UserValidationException;
import ednardo.api.soloapp.model.Role;
import ednardo.api.soloapp.model.User;
import ednardo.api.soloapp.model.dto.LoginRequestDTO;
import ednardo.api.soloapp.model.dto.RecoveryJwtTokenDTO;
import ednardo.api.soloapp.model.dto.UserDTO;
import ednardo.api.soloapp.model.security.JwtUtils;
import ednardo.api.soloapp.model.security.MyUserDetailsService;
import ednardo.api.soloapp.repository.UserRepository;
import ednardo.api.soloapp.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    MyUserDetailsService userDetailsService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void registerNewUser(final UserDTO userDTO) {

        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new UserValidationException("This login email already exists.");
        }

        User newUser = User.builder().email(userDTO.getEmail())
                .password(passwordEncoder.encode(userDTO.getPassword()))
                .givenName(userDTO.getGivenName())
                .surname(userDTO.getSurname())
                .country(userDTO.getCountry())
                .city(userDTO.getCity())
                .dateOfBirth(userDTO.getDateOfBirth())
                .role(userDTO.getRole())
                .active(userDTO.isActive())
                .build();

        try {
            userRepository.save(newUser);
        } catch (Exception exception) {
            throw new UserValidationException("Unable to save new user. Please try again.");
        }
    }

    @Override
    public void update(UserDTO userDTO) {
        User userUpdated = this.userRepository.findByEmail(userDTO.getEmail());
        if (userUpdated == null) {
            throw new UserValidationException("User not found.");
        }

        userUpdated.setGivenName(userDTO.getGivenName());
        userUpdated.setSurname(userDTO.getSurname());
        userUpdated.setCountry(userDTO.getCountry());
        userUpdated.setCity(userDTO.getCity());
        userUpdated.setDateOfBirth(userDTO.getDateOfBirth());
        userUpdated.setRole(userDTO.getRole());
        userUpdated.setActive(userDTO.isActive());
        userUpdated.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        userUpdated.setEmail(userDTO.getEmail());

        try {
            this.userRepository.save(userUpdated);
        } catch (Exception exception) {
            throw new UserValidationException("Unable to update user.");
        }
    }

    @Override
    public void deleteByEmail(String email) {
        User user = this.userRepository.findByEmail(email);

        if (user == null) {
            throw new UserValidationException("User not found.");
        }

        try {
            user.setActive(false);
            this.userRepository.save(user);
        } catch (Exception exception) {
            throw new UserValidationException("Unable to delete user.");
        }
    }

    @Override
    public RecoveryJwtTokenDTO authenticateUser(LoginRequestDTO loginRequestDTO) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(loginRequestDTO.getEmail(), loginRequestDTO.getPassword());

        Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);

        UserDetails userDetails = (UserDetails) userDetailsService.loadUserByUsername(loginRequestDTO.getEmail());

        return new RecoveryJwtTokenDTO(jwtUtils.generateToken(userDetails));
    }
}
