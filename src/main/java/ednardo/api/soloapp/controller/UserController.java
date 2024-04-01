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
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<String> registerUserAccount(@RequestBody @Valid UserDTO userDTO) {
        userService.registerNewUser(userDTO);
        return new ResponseEntity<>("User created.", HttpStatus.CREATED);
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateUser(@RequestBody UserDTO userDTO) {
        userService.update(userDTO);
        return new ResponseEntity<>("User updated.", HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("")

    @GetMapping("/test")
    public String getAuthenticationTest() {
        return "OK";
    }

    @GetMapping("/test/user")
    public ResponseEntity<String>getUserAuthenticationTest() {
        return new ResponseEntity<>("User Authenticated", HttpStatus.OK);
    }

    @GetMapping("/test/administrator")
    public ResponseEntity<String> getAdminAuthenticationTest() {
        return new ResponseEntity<>("Admin authenticated", HttpStatus.OK);
    }

}
