package ednardo.api.soloapp.service;

import ednardo.api.soloapp.model.User;
import ednardo.api.soloapp.model.dto.UserDTO;

public interface UserService {
    User registerNewUser(UserDTO userDTO);
    void update(User user);
    void deleteByEmail(String email);

}
