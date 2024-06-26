package ednardo.api.soloapp.filter;

import ednardo.api.soloapp.config.SecurityConfig;
import ednardo.api.soloapp.exception.UserNotFoundException;
import ednardo.api.soloapp.model.User;
import ednardo.api.soloapp.util.JwtUtils;
import ednardo.api.soloapp.service.impl.MyUserDetailsService;
import ednardo.api.soloapp.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;

@Component
@Slf4j
public class UserAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtils jwt;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MyUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
      // response.setContentType("application/json");
        try {
            if (checkIfEndpointIsNotPublic(request)) {
                String token = recoveryToken(request);
                if (token != null && jwt.validateJwtToken(token)) {
                    String subject = jwt.getSubjectFromToken(token);
                    User user = userRepository.findByEmail(subject).orElseThrow(()->new UserNotFoundException("User not found"));
                    UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());

                    Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails.getUsername(), null, userDetails.getAuthorities());

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception e) {
            log.error("Cannot set user authentication: {}", e);
        }
        filterChain.doFilter(request, response);
    }

    private String recoveryToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null) {
            return authorizationHeader.replace("Bearer ", "");
        }
        return null;
    }

    private boolean checkIfEndpointIsNotPublic(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        return Arrays.asList(SecurityConfig.SWAGGER_WHITELIST).stream().noneMatch(uri -> requestURI.contains(uri));
    }

    private String getRequestHeaders(HttpServletRequest request) {
        Enumeration<String> headerNames = request.getHeaderNames();
        StringBuilder headers = new StringBuilder();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            headers.append(headerName).append(": ").append(headerValue).append("; ");
        }
        return headers.toString();
    }


}
