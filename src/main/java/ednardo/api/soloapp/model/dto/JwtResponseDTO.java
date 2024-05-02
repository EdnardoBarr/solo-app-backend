package ednardo.api.soloapp.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Data
public class JwtResponseDTO extends RefreshTokenResponseDTO{
  //  private String email;
  //  private Collection<? extends GrantedAuthority> authorities;
    private UserDetails userDetails;

    public JwtResponseDTO(UserDetails userDetails, String token, String refreshToken) {
        super(token, refreshToken);
        this.userDetails = userDetails;
      //  this.email = userDetails.getUsername();
       // this.authorities = userDetails.getAuthorities();
    }
}
