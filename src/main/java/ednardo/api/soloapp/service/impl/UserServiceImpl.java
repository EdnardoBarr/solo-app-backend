package ednardo.api.soloapp.service.impl;

import ednardo.api.soloapp.exception.UserValidationException;
import ednardo.api.soloapp.model.User;
import ednardo.api.soloapp.model.dto.UserDTO;
import ednardo.api.soloapp.repository.UserRepository;
import ednardo.api.soloapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User registerNewUser(final UserDTO userDTO) {

        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new UserValidationException("This login email already exists.");
        }

        final User user = new User();

        try {
            user.setFirstName(userDTO.getFirstName());
            user.setLastName(userDTO.getLastName());
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            user.setEmail(user.getEmail());
            user.setActive(true);

            return userRepository.save(user);
        } catch (Exception exception) {
            throw new UserValidationException("Unable to save new user. Please try again.");
        }
    }

    @Override
    public void update(User user) {
        User userUpdated = this.userRepository.findByEmail(user.getEmail()).orElseThrow(() -> new UserValidationException("User not found."));

        userUpdated.setFirstName(user.getFirstName());
        userUpdated.setLastName(user.getLastName());
        userUpdated.setActive(user.getActive());
       // userUpdated.setUserRoles(user.getUserRoles());
        userUpdated.setPassword(user.getPassword());
        userUpdated.setEmail(user.getEmail());

        try {
            this.userRepository.save(userUpdated);
        } catch (Exception exception) {
            throw new UserValidationException("Unable to update user.");
        }
    }

    @Override
    public void deleteByEmail(String email) {
        User user = this.userRepository.findByEmail(email).orElseThrow(() -> new UserValidationException("User not found."));

        try {
            user.setActive(false);
            this.userRepository.save(user);
        } catch (Exception exception) {
            throw new UserValidationException("Unable to delete user.");
        }
    }
}
