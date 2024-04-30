package ednardo.api.soloapp.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Data
public class JwtResponseDTO extends RefreshTokenResponseDTO{
   // private String email;
 //   private Collection<? extends GrantedAuthority> authorities;

    public JwtResponseDTO(String token, String refreshToken) {
        super(token, refreshToken);
    //    this.email = refreshToken.g();
     //   this.authorities = userDetails.getAuthorities();
    }
}
