package ednardo.api.soloapp.controller;

import ednardo.api.soloapp.model.RefreshToken;
import ednardo.api.soloapp.model.User;
import ednardo.api.soloapp.model.dto.*;
import ednardo.api.soloapp.model.security.MyUserDetailsService;
import ednardo.api.soloapp.service.RefreshTokenService;
import ednardo.api.soloapp.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;

    @Autowired
    RefreshTokenService refreshTokenService;

    @Autowired
    MyUserDetailsService userDetailsService;

    @GetMapping("/{id}")
    public ResponseEntity getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getById(id));
    }

    @PostMapping("/login")
    public ResponseEntity authenticateUser(@RequestBody LoginRequestDTO loginRequestDTO) {
        RecoveryJwtTokenDTO token = userService.authenticateUser(loginRequestDTO);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(loginRequestDTO.getEmail());
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequestDTO.getEmail());

        return ResponseEntity.ok(new JwtResponseDTO(userDetails, token.getToken(), refreshToken.getToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity doLogout(@RequestBody Long userId) {
        SecurityContextHolder.clearContext();
        refreshTokenService.deleteRefreshToken(userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/refresh-token")
    public RefreshTokenResponseDTO refreshToken(@RequestBody RefreshTokenRequestDTO request) {
        String refreshToken = request.getRefreshToken();
        return this.refreshTokenService.refreshToken(refreshToken);
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
