package ednardo.api.soloapp.service;

import ednardo.api.soloapp.model.User;
import ednardo.api.soloapp.model.dto.LoginRequestDTO;
import ednardo.api.soloapp.model.dto.RecoveryJwtTokenDTO;
import ednardo.api.soloapp.model.dto.UserDTO;
import org.springframework.http.ResponseEntity;

public interface UserService {
    void registerNewUser(UserDTO userDTO);
    void update(UserDTO userDTO);
    void deleteByEmail(String email);
    RecoveryJwtTokenDTO authenticateUser(LoginRequestDTO loginRequestDTO);
}
