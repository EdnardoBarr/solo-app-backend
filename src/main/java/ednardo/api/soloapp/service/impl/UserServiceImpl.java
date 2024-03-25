package ednardo.api.soloapp.service.impl;

import ednardo.api.soloapp.model.User;
import ednardo.api.soloapp.repository.UserRepository;
import ednardo.api.soloapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserRepository userRepository;

    public User save(User user) {

        user = this.userRepository.save(user);
        user.setActive(true);

        return user;
    }
}
