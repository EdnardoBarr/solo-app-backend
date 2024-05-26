package ednardo.api.soloapp.service;

import ednardo.api.soloapp.model.User;
import ednardo.api.soloapp.model.dto.LoginRequestDTO;
import ednardo.api.soloapp.model.dto.RecoveryJwtTokenDTO;
import ednardo.api.soloapp.model.dto.UserDTO;
import ednardo.api.soloapp.model.dto.UserFilterDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface UserService {
    User getById(Long id);
    User getByEmail(String email);
    void registerNewUser(UserDTO userDTO);
    void update(Long id, UserDTO userDTO);
    void deleteByEmail(String email);
    RecoveryJwtTokenDTO authenticateUser(LoginRequestDTO loginRequestDTO, HttpServletRequest request, HttpSession session);
    Page<User> getAll(UserFilterDTO userFilterDTO, Pageable pageable);
}
