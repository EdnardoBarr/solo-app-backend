package ednardo.api.soloapp.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ednardo.api.soloapp.enums.RoleName;
import ednardo.api.soloapp.model.Role;
import ednardo.api.soloapp.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

@Data
@Builder
@AllArgsConstructor
public class UserDetailsDTO implements UserDetails {
    public UserDetailsDTO(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.givenName = user.getGivenName();
        this.password = user.getPassword();
        this.surname = user.getSurname();
        this.country = user.getCountry();
        this.city = user.getCity();
        this.dateOfBirth = user.getDateOfBirth();
        this.bio = user.getBio();
        this.authorities = this.getAuthorities(user.getRoles());
    }
    private Long id;
    private String email;
    private String givenName;
    @JsonIgnore
    private String password;
    private String surname;
    private String country;
    private String city;
    private String dateOfBirth;
    private String bio;
    private Collection<GrantedAuthority> authorities;
    private static Collection<GrantedAuthority> getAuthorities (Collection<Role> roles) {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        for (Role role : roles) {
            authorities.add(new SimpleGrantedAuthority(role.getRoleName().toString()));
        }
        return authorities;
    }

    @Override
    public String getUsername() {
        return this.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
