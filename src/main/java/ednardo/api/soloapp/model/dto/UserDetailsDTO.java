package ednardo.api.soloapp.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ednardo.api.soloapp.enums.ActivityCategory;
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

import java.util.*;

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
       // this.interests = user.getInterests();
        this.interests = this.getInterests(user.getInterests());
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
  //  private List<ActivityCategory> interests;
    private List<ActivityCategoryDTO> interests;
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

    private static final Map<ActivityCategory, Integer> categoryToIdMap = new HashMap<>();
    static {
        categoryToIdMap.put(ActivityCategory.SPORT, 1);
        categoryToIdMap.put(ActivityCategory.TREKKING, 2);
        categoryToIdMap.put(ActivityCategory.TRAVEL, 3);
        categoryToIdMap.put(ActivityCategory.GYM, 4);
        categoryToIdMap.put(ActivityCategory.BEACH, 5);
        categoryToIdMap.put(ActivityCategory.OTHER, 6);
    }


    private static List<ActivityCategoryDTO> getInterests(List<ActivityCategory> activityCategories) {
        List<ActivityCategoryDTO> interests = new ArrayList<>();
        for (ActivityCategory activityCategory : activityCategories) {
            int id = categoryToIdMap.get(activityCategory);
            String label = activityCategory.toString().substring(0, 1).toUpperCase() + activityCategory.toString().substring(1).toLowerCase(Locale.ROOT);
            ActivityCategoryDTO dto = new ActivityCategoryDTO(id, label, activityCategory.toString());
            interests.add(dto);
        }
        return interests;
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
