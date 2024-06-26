package ednardo.api.soloapp.service.impl;

import ednardo.api.soloapp.exception.UserNotFoundException;
import ednardo.api.soloapp.exception.UserValidationException;
import ednardo.api.soloapp.model.Role;
import ednardo.api.soloapp.model.User;
import ednardo.api.soloapp.model.dto.UserDetailsDTO;
import ednardo.api.soloapp.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service("userDetailsService")
@Transactional
public class  MyUserDetailsService implements UserDetailsService {
    private User user;
    @Autowired
    private UserRepository userRepository;

    public MyUserDetailsService() {
        super();
    }

    public UserDetails loadUserByUsername(String email) throws UserValidationException {
        User user = userRepository.findByEmail(email).orElseThrow(()-> new UserNotFoundException("User not found"));

        return new UserDetailsDTO(user);
    }
    private static Collection<GrantedAuthority> getAuthorities (Collection<Role> roles) {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        for (Role role : roles) {
            authorities.add(new SimpleGrantedAuthority(role.getRoleName().toString()));
        }
        return authorities;
    }
}
