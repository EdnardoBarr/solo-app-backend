package ednardo.api.soloapp.controller;

import ednardo.api.soloapp.model.Activity;
import ednardo.api.soloapp.model.RefreshToken;
import ednardo.api.soloapp.model.User;
import ednardo.api.soloapp.model.dto.*;
import ednardo.api.soloapp.model.security.MyUserDetailsService;
import ednardo.api.soloapp.service.RefreshTokenService;
import ednardo.api.soloapp.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Slf4j
@RestController
@RequestMapping("/api/v1/user")
@CrossOrigin("http://localhost:5173")
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

    @GetMapping("/all")
    public ResponseEntity getAllUsers(UserFilterDTO userFilterDTO, @PageableDefault(size = 4, page = 0) Pageable pageable) {
        Page<User> users = this.userService.getAll(userFilterDTO, pageable);

        return ResponseEntity.ok(users);
    }

    @GetMapping("/retrieve/{email}")
    public ResponseEntity getUserByEmail(@PathVariable String email) {
        return ResponseEntity.ok(this.userService.getByEmail(email));
    }

    @PostMapping("/login")
    public ResponseEntity authenticateUser(@RequestBody LoginRequestDTO loginRequestDTO, HttpServletRequest request, HttpSession session) {
        RecoveryJwtTokenDTO token = userService.authenticateUser(loginRequestDTO, request, session);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(loginRequestDTO.getEmail());
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequestDTO.getEmail());

        return ResponseEntity.ok(new JwtResponseDTO(userDetails, token.getToken(), refreshToken.getToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity doLogout(Principal principal) {
        refreshTokenService.deleteRefreshToken(principal);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/refresh-token")
    public RefreshTokenResponseDTO refreshToken(@RequestBody RefreshTokenRequestDTO request) {
        String refreshToken = request.getRefreshToken();
        return this.refreshTokenService.refreshToken(refreshToken);
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUserAccount(@RequestBody @Valid UserDTO userDTO) {
        userService.registerNewUser(userDTO);
        return new ResponseEntity<>("User created.", HttpStatus.CREATED);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        userService.update(id, userDTO);
        return new ResponseEntity<>("User updated.", HttpStatus.NO_CONTENT);
    }

    @GetMapping("/logged")
    public UserDetails getUser(Principal principal) {
        return this.userDetailsService.loadUserByUsername(principal.getName());
    }

    @GetMapping("/hello")
    public String sayHello() {
        return "Hello";
    }
}
