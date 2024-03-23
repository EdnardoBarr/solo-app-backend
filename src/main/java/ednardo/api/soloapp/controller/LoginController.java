package ednardo.api.soloapp.controller;

import ednardo.api.soloapp.model.dto.LoginRequestDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("authorize")
    public ResponseEntity doLogin(@RequestBody LoginRequestDTO requestDto, HttpServletRequest request, HttpSession session) {
        Authentication authentication = null;
        session.invalidate();

        try {
            authentication = this.authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(requestDto.getEmail(), requestDto.getPassword()));
        } catch (Exception exception) {

        }

        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authentication);



    }
}
