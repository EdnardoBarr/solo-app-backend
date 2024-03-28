package ednardo.api.soloapp.controller;

import ednardo.api.soloapp.model.User;
import ednardo.api.soloapp.model.dto.LoginRequestDTO;
import ednardo.api.soloapp.model.dto.RecoveryJwtTokenDTO;
import ednardo.api.soloapp.model.dto.UserDTO;
import ednardo.api.soloapp.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;

    @PostMapping("/login")
    public ResponseEntity<RecoveryJwtTokenDTO> authenticateUser(@RequestBody LoginRequestDTO loginRequestDTO) {
        RecoveryJwtTokenDTO token = userService.authenticateUser(loginRequestDTO);
        return new ResponseEntity<>(token, HttpStatus.OK);
    }

    @PostMapping("/registration")
    public User registerUserAccount(@RequestBody @Valid UserDTO userDTO) {
        return userService.registerNewUser(userDTO);
    }

}
